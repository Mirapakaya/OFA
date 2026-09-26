"use client";

interface TransferProgressProps {
  fileName: string;
  percent: number;
  bytesTransferred: number;
  totalBytes: number;
  speed: string;
  eta: string;
  onCancel: () => void;
}

function formatBytes(bytes: number): string {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
  if (bytes < 1073741824) return (bytes / 1048576).toFixed(1) + " MB";
  return (bytes / 1073741824).toFixed(1) + " GB";
}

export function TransferProgress({
  fileName,
  percent,
  bytesTransferred,
  totalBytes,
  speed,
  eta,
  onCancel,
}: TransferProgressProps) {
  const clampedPercent = Math.min(100, Math.max(0, percent));

  return (
    <div className="transfer-progress">
      <p className="transfer-progress__name">{fileName}</p>

      <div className="transfer-progress__bar">
        <div
          className="transfer-progress__fill"
          style={{ width: `${clampedPercent}%` }}
        />
      </div>

      <div className="transfer-progress__stats">
        <span>{Math.round(clampedPercent)}%</span>
        <span>
          {formatBytes(bytesTransferred)} / {formatBytes(totalBytes)}
        </span>
      </div>

      {speed && (
        <div className="transfer-progress__detail">
          <span>{speed}</span>
          {eta && <span>{eta}</span>}
        </div>
      )}

      <button
        className="button button-secondary"
        onClick={onCancel}
      >
        Cancel transfer
      </button>
    </div>
  );
}