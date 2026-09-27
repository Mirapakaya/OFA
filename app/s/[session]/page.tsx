"use client";

import { use, useCallback, useEffect, useRef, useState } from "react";
import Link from "next/link";
import { parsePairingUrl } from "@/lib/pairing/session";
import { createServerSignalingTransport } from "@/lib/signaling";
import { generateKeyPair, performKeyAgreement, deriveSessionKeys, deriveVerificationPhrase, type KeyPair } from "@/lib/crypto";
import { createPeerConnection, acceptOfferAndSignal, closePeerConnection } from "@/lib/webrtc/connection";
import { receiveFiles, type ReceiveProgress } from "@/lib/transfer/receiver";
import { checkBrowserSupport, createError } from "@/lib/error";
import { TransferProgress } from "@/components/transfer/TransferProgress";

type SessionStep = "parsing" | "connecting" | "verifying" | "transferring" | "complete" | "error";

export default function SessionPage({
  params,
}: {
  params: Promise<{ session: string }>;
}) {
  const { session: sessionId } = use(params);
  const [step, setStep] = useState<SessionStep>("parsing");
  const [verificationPhrase, setVerificationPhrase] = useState("");
  const [progress, setProgress] = useState<ReceiveProgress | null>(null);
  const [error, setError] = useState("");
  const cleanupRef = useRef<(() => void)[]>([]);
  const cancelRef = useRef<(() => void) | null>(null);

  const initConnection = useCallback(async function(sid: string, secret: string) {
    try {
      setStep("connecting");

      // Generate our ephemeral key pair
      const keyPair = await generateKeyPair();

      // Create signaling transport
      const signaling = createServerSignalingTransport(sid);
      await signaling.joinSession(sid);
      cleanupRef.current.push(() => signaling.close());

      // Create peer connection (receiver side)
      const pc = createPeerConnection(
        (state) => {
          if (state.status === "failed" || state.status === "closed") {
            setStep("error");
            setError("The connection could not be established. Create a new transfer and try again.");
            closePeerConnection(pc);
          }
        },
      );
      cleanupRef.current.push(() => closePeerConnection(pc));

      // Handle incoming data channel from sender
      pc.ondatachannel = (event) => {
        const channel = event.channel;
        channel.binaryType = "arraybuffer";

        channel.onopen = () => {
          channel.bufferedAmountLowThreshold = 4 * 1024 * 1024;
          doKeyExchangeAndReceive(channel, keyPair, secret);
        };

        channel.onclose = () => {
          setStep((prev) => {
            if (prev !== "complete") {
              setError("The other device disconnected. Create a new transfer and try again.");
              return "error";
            }
            return prev;
          });
        };

        channel.onerror = (ev) => {
          const errorEvent = ev as RTCErrorEvent;
          setStep("error");
          setError(errorEvent.error?.message ?? "DataChannel error");
        };

        // Placeholder — will be replaced during key exchange
        channel.onmessage = () => {};
      };

      // Accept the offer from the sender
      await acceptOfferAndSignal(pc, signaling, sid);
    } catch (err) {
      setStep("error");
      setError(err instanceof Error ? err.message : "Something went wrong");
    }
  }, [sessionId]);

  useEffect(() => {
    // Check browser support
    const unsupported = checkBrowserSupport();
    if (unsupported) {
      setStep("error");
      setError(createError(unsupported).userMessage);
      return;
    }

    // Extract secret from URL fragment
    const fullUrl = window.location.href;
    const parsed = parsePairingUrl(fullUrl);

    if (!parsed) {
      setStep("error");
      setError("The session link is not valid. Ask the sender for a new link.");
      return;
    }

    // Start connection process
    initConnection(parsed.sessionId, parsed.secret);

    return () => {
      cleanupRef.current.forEach((fn) => fn());
      cancelRef.current?.();
    };
  }, [sessionId, initConnection]);

  /** Perform the ECDH key exchange over the DataChannel, then start receiving files */
  async function doKeyExchangeAndReceive(
    channel: RTCDataChannel,
    keyPair: KeyPair,
    sessionSecret: string,
  ) {
    try {
      // 1. Wait for the sender's key exchange message
      const senderPublicKeyBase64 = await new Promise<string>((resolve, reject) => {
        const timeout = setTimeout(() => reject(new Error("Key exchange timed out")), 15_000);
        channel.onmessage = (event) => {
          if (typeof event.data === "string") {
            try {
              const msg = JSON.parse(event.data);
              if (msg.type === "key-exchange" && msg.publicKey && msg.curve) {
                clearTimeout(timeout);
                resolve(msg.publicKey);
                return;
              }
            } catch {
              // Not a key-exchange message — ignore
            }
          }
          // Binary data during key exchange phase — ignore
        };
      });

      // 2. Send our public key back to the sender
      const ourPublicKeyBase64 = btoa(String.fromCharCode(...keyPair.publicKeyBytes));
      const keyExchangeMsg = JSON.stringify({
        type: "key-exchange",
        publicKey: ourPublicKeyBase64,
        curve: keyPair.curve,
      });
      channel.send(keyExchangeMsg);

      // 3. Decode sender's public key and perform ECDH key agreement
      const senderPublicKeyBytes = Uint8Array.from(atob(senderPublicKeyBase64), (c) => c.charCodeAt(0));
      const sharedSecret = await performKeyAgreement(keyPair.privateKey, senderPublicKeyBytes, keyPair.curve);

      // 4. Derive session keys using shared secret + URL fragment secret as HKDF salt
      const salt = new TextEncoder().encode(sessionSecret);
      const sessionKeys = await deriveSessionKeys(sharedSecret, salt);

      // 5. Derive and display verification phrase
      const phrase = await deriveVerificationPhrase(sharedSecret);
      setVerificationPhrase(phrase);
      setStep("transferring");

      // 6. Start receiving files with the real encryption key
      // The receiver's onmessage handler will be set by receiveFiles
      const receiver = receiveFiles(
        channel,
        sessionKeys.encryptionKey,
        (prog) => setProgress(prog),
        () => setStep("complete"),
      );
      cancelRef.current = () => receiver.cancel();
    } catch (err) {
      setStep("error");
      setError(err instanceof Error ? err.message : "Key exchange or transfer failed");
    }
  }

  return (
    <div className="session-page">
      <h1>{step === "complete" ? "Transfer complete" : step === "error" ? "Connection failed" : "Connecting"}</h1>

      {step === "parsing" && (
        <p className="session-status">Establishing secure connection…</p>
      )}

      {step === "connecting" && (
        <div className="connection-state">
          <span className="connection-state__dot connection-state__dot--connecting" />
          <span>Establishing secure connection…</span>
        </div>
      )}

      {step === "verifying" && verificationPhrase && (
        <div style={{ textAlign: "center" }}>
          <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "12px" }}>
            Verify that both devices show the same phrase:
          </p>
          <p className="verification-phrase">{verificationPhrase}</p>
          <p style={{ fontSize: "13px", color: "var(--muted)", marginTop: "12px" }}>
            If the phrases match, the connection is verified.
          </p>
        </div>
      )}

      {step === "transferring" && (
        <>
          {verificationPhrase && (
            <div style={{ textAlign: "center", marginBottom: "16px" }}>
              <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "8px" }}>
                Verify that both devices show the same phrase:
              </p>
              <p className="verification-phrase">{verificationPhrase}</p>
            </div>
          )}
          {progress && (
            <TransferProgress
              fileName={progress.fileName}
              percent={progress.percent}
              bytesTransferred={progress.bytesTransferred}
              totalBytes={progress.totalBytes}
              speed={progress.speedFormatted}
              eta={progress.etaFormatted}
              onCancel={() => window.location.href = "/"}
            />
          )}
        </>
      )}

      {step === "complete" && (
        <div className="transfer-complete">
          <p className="transfer-complete__check">All files received.</p>
          <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "16px" }}>
            The transfer session has ended. Nothing remains on OFA servers.
          </p>
        </div>
      )}

      {step === "error" && (
        <div>
          <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "16px" }}>{error}</p>
          <Link href="/" className="button button-secondary">Go back</Link>
        </div>
      )}

      {step !== "complete" && step !== "error" && (
        <p className="session-status" style={{ fontSize: "13px", color: "var(--muted)", marginTop: "24px" }}>
          Session: {sessionId.slice(0, 8)}…
        </p>
      )}
    </div>
  );
}
