"use client";

import { useState, useEffect, useRef } from "react";
import { createReceiverPairing } from "@/lib/pairing/broadcast";
import { checkBrowserSupport, createError } from "@/lib/error";

type ReceiveStep = "waiting" | "connecting" | "connected" | "transferring" | "complete" | "error";

export default function ReceivePage() {
  const [step, setStep] = useState<ReceiveStep>("waiting");
  const [error, setError] = useState<string>("");
  const cleanupRef = useRef<(() => void)[]>([]);

  useEffect(() => {
    const unsupported = checkBrowserSupport();
    if (unsupported) {
      setStep("error");
      setError(createError(unsupported).userMessage);
      return;
    }

    const localPairing = createReceiverPairing(
      () => {
        setStep("connecting");
      },
    );
    cleanupRef.current.push(() => localPairing.destroy());

    const cleanup = cleanupRef.current;
    return () => {
      cleanup.forEach((fn) => fn());
    };
  }, []);

  return (
    <div className="receive-page">
      <h1>Receive files</h1>

      {step === "waiting" && (
        <div className="receive-connect">
          <p>Connect a sending device.</p>
          <p className="text-xs text-muted mt-3">
            Open a secure link from the sender, or wait for a sender on this device.
          </p>
        </div>
      )}

      {step === "connecting" && (
        <div className="connection-state">
          <span className="connection-state__dot connection-state__dot--connecting" />
          <span>Establishing secure connection…</span>
        </div>
      )}

      {step === "connected" && (
        <div>
          <p className="status-label">Connected</p>
          <p className="text-small text-muted">Waiting for files…</p>
        </div>
      )}

      {step === "transferring" && (
        <div className="connection-state">
          <span className="connection-state__dot connection-state__dot--connected" />
          <span>Receiving files…</span>
        </div>
      )}

      {step === "complete" && (
        <div className="transfer-complete">
          <p className="status-label">Transfer complete</p>
          <p className="transfer-complete__check">All files received.</p>
          <p className="text-small text-muted mb-4">
            The transfer session has ended.
          </p>
        </div>
      )}

      {step === "error" && (
        <div>
          <p className="status-label text-error">Connection failed</p>
          <p className="text-small text-muted mb-4">{error}</p>
          <button className="button secondary" onClick={() => window.location.reload()}>
            Try again
          </button>
        </div>
      )}
    </div>
  );
}
