"use client";

import { useState } from "react";

export default function ConnectPage() {
  const [scanning, setScanning] = useState(false);

  return (
    <div className="connect-page">
      <h1>Connect device</h1>
      <p>Scan the sender&apos;s QR code or open their OFA link.</p>

      {!scanning ? (
        <button
          className="button button-primary"
          onClick={() => setScanning(true)}
        >
          Use camera
        </button>
      ) : (
        <div className="scanner-placeholder">
          <p>Camera access requested.</p>
          <p className="hint">Point your camera at the sender&apos;s QR code.</p>
        </div>
      )}
    </div>
  );
}