"use client";

import { useState, useCallback } from "react";
import { QRCode } from "@/components/qr/QRCode";
import type { PairingSession } from "@/lib/pairing/session";

interface PairingViewProps {
  session: PairingSession;
  onCancel: () => void;
}

type PairingStep = "waiting" | "connected" | "expired";

export function PairingView({ session, onCancel }: PairingViewProps) {
  const [step] = useState<PairingStep>("waiting");
  const [copied, setCopied] = useState(false);

  const copyLink = useCallback(async () => {
    try {
      await navigator.clipboard.writeText(session.url);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Clipboard API not available — ignore
    }
  }, [session.url]);

  if (step === "expired") {
    return (
      <div className="pairing-view">
        <p className="status-label">Session expired</p>
        <p style={{ fontSize: "14px", color: "var(--muted)" }}>
          The pairing session timed out. Create a new transfer.
        </p>
        <button className="button button-secondary" onClick={onCancel}>
          Try again
        </button>
      </div>
    );
  }

  if (step === "connected") {
    return (
      <div className="pairing-view">
        <p className="status-label">Connected</p>
        <p style={{ fontSize: "14px", color: "var(--muted)" }}>
          Secure connection established.
        </p>
      </div>
    );
  }

  return (
    <div className="pairing-view">
      <p className="status-label">Waiting for another device</p>
      <p style={{ fontSize: "14px", color: "var(--muted)", marginBottom: "24px" }}>
        Scan the code or open the secure link.
      </p>

      <div style={{ display: "flex", justifyContent: "center", marginBottom: "24px" }}>
        <QRCode data={session.url} size={200} />
      </div>

      <div style={{ display: "flex", gap: "12px", justifyContent: "center" }}>
        <button
          className="button button-secondary"
          onClick={copyLink}
        >
          {copied ? "Copied" : "Copy secure link"}
        </button>
        <button
          className="button button-secondary"
          onClick={onCancel}
        >
          Cancel
        </button>
      </div>
    </div>
  );
}