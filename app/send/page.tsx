"use client";

import type { ChangeEvent, DragEvent } from "react";
import { useState, useCallback, useRef, useEffect } from "react";
import { PairingView } from "@/components/pairing/PairingView";
import { TransferProgress } from "@/components/transfer/TransferProgress";
import { createSendSession, type PairingSession } from "@/lib/pairing/session";
import { createSenderPairing } from "@/lib/pairing/broadcast";
import { createServerSignalingTransport } from "@/lib/signaling";
import { generateKeyPair, performKeyAgreement, deriveSessionKeys, deriveVerificationPhrase, type KeyPair } from "@/lib/crypto";
import { createPeerConnection, createOfferAndSignal, closePeerConnection } from "@/lib/webrtc/connection";
import { sendFiles, type SendProgress } from "@/lib/transfer/sender";
import { validateFiles, formatSize } from "@/lib/transfer/file-utils";
import { checkBrowserSupport, createError } from "@/lib/error";

interface SelectedFile {
  id: string;
  file: File;
  name: string;
  size: number;
}

type SendStep = "selecting" | "pairing" | "connecting" | "transferring" | "complete" | "error";

export default function SendPage() {
  const [files, setFiles] = useState<SelectedFile[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [step, setStep] = useState<SendStep>("selecting");
  const [session, setSession] = useState<PairingSession | null>(null);
  const [verificationPhrase, setVerificationPhrase] = useState<string>("");
  const [progress, setProgress] = useState<SendProgress | null>(null);
  const [error, setError] = useState<string>("");
  const inputRef = useRef<HTMLInputElement>(null);
  const abortRef = useRef<AbortController | null>(null);
  const cleanupRef = useRef<(() => void)[]>([]);

  // Check browser support on mount
  useEffect(() => {
    const unsupported = checkBrowserSupport();
    if (unsupported) {
      setStep("error");
      setError(createError(unsupported).userMessage);
    }
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    const cleanup = cleanupRef.current;
    const abort = abortRef.current;
    return () => {
      cleanup.forEach((fn) => fn());
      abort?.abort();
    };
  }, []);

  const addFiles = useCallback((fileList: FileList | File[]) => {
    const newFiles = Array.from(fileList).map((file) => ({
      id: crypto.randomUUID(),
      file,
      name: file.name,
      size: file.size,
    }));
    setFiles((prev) => [...prev, ...newFiles]);
  }, []);

  const removeFile = useCallback((id: string) => {
    setFiles((prev) => prev.filter((f) => f.id !== id));
  }, []);

  const handleDrop = useCallback(
    (e: DragEvent) => {
      e.preventDefault();
      setIsDragging(false);
      if (e.dataTransfer.files.length > 0) addFiles(e.dataTransfer.files);
    },
    [addFiles],
  );

  const handleChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files.length > 0) addFiles(e.target.files);
    },
    [addFiles],
  );

  const totalSize = files.reduce((sum, f) => sum + f.size, 0);

  const startPairing = useCallback(async () => {
    const validation = validateFiles(files.map((f) => f.file));
    if (!validation.valid) {
      setStep("error");
      setError(validation.error ?? "Invalid files");
      return;
    }

    try {
      // Create pairing session
      const baseUrl = window.location.origin;
      const pairingSession = createSendSession(baseUrl);
      setSession(pairingSession);
      setStep("pairing");

      // Generate ephemeral key pair
      const keyPair = await generateKeyPair();

      // Create signaling transport
      const signaling = createServerSignalingTransport(pairingSession.sessionId);
      await signaling.createSession();
      cleanupRef.current.push(() => signaling.close());

      // Also try local pairing via BroadcastChannel
      const localPairing = createSenderPairing(
        pairingSession.sessionId,
        () => {
          // Local peer found — could switch to local signaling
        },
      );
      cleanupRef.current.push(() => localPairing.destroy());

      // Create peer connection
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

      // Create data channel (sender creates it)
      const channel = pc.createDataChannel("ofa-transfer", { ordered: true });
      channel.binaryType = "arraybuffer";

      // Set up key exchange on channel open
      channel.onopen = () => {
        channel.bufferedAmountLowThreshold = 4 * 1024 * 1024;
        doKeyExchangeAndSend(channel, keyPair, pairingSession.secret);
      };

      channel.onclose = () => {
        // Only show error if we haven't completed the transfer
        setStep((prev) => {
          if (prev !== "complete") {
            setError("The other device disconnected. Create a new transfer and try again.");
            return "error";
          }
          return prev;
        });
      };

      channel.onerror = (event) => {
        const errorEvent = event as RTCErrorEvent;
        setStep("error");
        setError(errorEvent.error?.message ?? "DataChannel error");
      };

      // Placeholder message handler — will be replaced during key exchange
      channel.onmessage = () => {};

      // Send offer via signaling
      await createOfferAndSignal(pc, signaling, pairingSession.sessionId);

      setStep("connecting");
    } catch (err) {
      setStep("error");
      setError(err instanceof Error ? err.message : "Something went wrong");
    }
  }, [files]); // eslint-disable-line react-hooks/exhaustive-deps

  /** Perform the ECDH key exchange over the DataChannel, then start sending files */
  const doKeyExchangeAndSend = useCallback(async (
    channel: RTCDataChannel,
    keyPair: KeyPair,
    sessionSecret: string,
  ) => {
    try {
      // 1. Send our public key to the receiver
      const publicKeyBase64 = btoa(String.fromCharCode(...keyPair.publicKeyBytes));
      const keyExchangeMsg = JSON.stringify({
        type: "key-exchange",
        publicKey: publicKeyBase64,
        curve: keyPair.curve,
      });
      channel.send(keyExchangeMsg);

      // 2. Wait for the receiver's public key message
      const theirPublicKeyBase64 = await new Promise<string>((resolve, reject) => {
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

      // 3. Decode their public key and perform ECDH key agreement
      const theirPublicKeyBytes = Uint8Array.from(atob(theirPublicKeyBase64), (c) => c.charCodeAt(0));
      const sharedSecret = await performKeyAgreement(keyPair.privateKey, theirPublicKeyBytes, keyPair.curve);

      // 4. Derive session keys using shared secret + URL fragment secret as HKDF salt
      const salt = new TextEncoder().encode(sessionSecret);
      const sessionKeys = await deriveSessionKeys(sharedSecret, salt);

      // 5. Derive and display verification phrase
      const phrase = await deriveVerificationPhrase(sharedSecret);
      setVerificationPhrase(phrase);
      setStep("transferring");

      // 6. Create abort controller for cancellation
      const abort = new AbortController();
      abortRef.current = abort;

      // 7. Start sending files with the real encryption key
      await sendFiles(
        files.map((f) => f.file),
        channel,
        sessionKeys.encryptionKey,
        (prog) => setProgress(prog),
        abort.signal,
      );

      // Transfer complete
      setStep("complete");
    } catch (err) {
      if (err instanceof Error && err.message === "Transfer cancelled") {
        setStep("selecting");
        setSession(null);
        setProgress(null);
      } else {
        setStep("error");
        setError(err instanceof Error ? err.message : "Key exchange or transfer failed");
      }
    }
  }, [files]);

  const cancelTransfer = useCallback(() => {
    abortRef.current?.abort();
    cleanupRef.current.forEach((fn) => fn());
    cleanupRef.current = [];
    setStep("selecting");
    setSession(null);
    setProgress(null);
    setError("");
  }, []);

  const resetPage = useCallback(() => {
    setStep("selecting");
    setSession(null);
    setProgress(null);
    setError("");
    setVerificationPhrase("");
  }, []);

  return (
    <div className="send-page">
      <h1>Send files</h1>

      {step === "selecting" && (
        <>
          <div
            className={`drop-zone ${isDragging ? "drop-zone--active" : ""}`}
            onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }}
            onDragLeave={() => setIsDragging(false)}
            onDrop={handleDrop}
          >
            <p className="drop-zone__text">Drop files here</p>
            <p className="drop-zone__hint">or choose from device</p>
            <input
              ref={inputRef}
              type="file"
              multiple
              onChange={handleChange}
              className="drop-zone__input"
              aria-label="Choose files to send"
            />
            <button className="button button-secondary" onClick={() => inputRef.current?.click()}>
              Choose files
            </button>
          </div>

          {files.length > 0 && (
            <div className="file-section">
              <div className="file-summary">
                <span>{files.length} file{files.length !== 1 ? "s" : ""}</span>
                <span>{formatSize(totalSize)}</span>
              </div>
              <ul className="file-list">
                {files.map((f) => (
                  <li key={f.id} className="file-item">
                    <span className="file-item__name">{f.name}</span>
                    <span className="file-item__size">{formatSize(f.size)}</span>
                    <button className="file-item__remove" onClick={() => removeFile(f.id)} aria-label={"Remove " + f.name}>&times;</button>
                  </li>
                ))}
              </ul>
              <button className="button button-primary" onClick={startPairing}>
                Connect device
              </button>
            </div>
          )}
        </>
      )}

      {step === "pairing" && session && (
        <PairingView session={session} onCancel={cancelTransfer} />
      )}

      {step === "connecting" && (
        <div className="connection-state">
          <span className="connection-state__dot connection-state__dot--connecting" />
          <span>Establishing secure connection…</span>
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
              onCancel={cancelTransfer}
            />
          )}
        </>
      )}

      {step === "complete" && (
        <div className="transfer-complete">
          <p className="status-label">Transfer complete</p>
          <p className="transfer-complete__check">{files.length} file{files.length !== 1 ? "s" : ""} sent.</p>
          <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "16px" }}>
            The transfer session has ended.
          </p>
          <button className="button button-secondary" onClick={resetPage}>Done</button>
        </div>
      )}

      {step === "error" && (
        <div>
          <p className="status-label" style={{ color: "var(--error)" }}>Transfer failed</p>
          <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "16px" }}>{error}</p>
          <button className="button button-secondary" onClick={resetPage}>Try again</button>
        </div>
      )}
    </div>
  );
}