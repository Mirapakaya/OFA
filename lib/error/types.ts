// OFA Error Types
//
// Typed errors with human-readable messages.
// No error codes exposed to users. Every error explains
// what happened and what the user can do next.

export type OFAErrorCode =
  | "connection-timeout"
  | "connection-failed"
  | "peer-disconnected"
  | "session-expired"
  | "session-not-found"
  | "transfer-cancelled"
  | "transfer-failed"
  | "decryption-failed"
  | "encryption-failed"
  | "invalid-chunk"
  | "invalid-session-id"
  | "unsupported-browser"
  | "signaling-error"
  | "file-too-large"
  | "too-many-files"
  | "camera-denied"
  | "clipboard-denied"
  | "unknown";

export class OFAError extends Error {
  code: OFAErrorCode;
  userMessage: string;

  constructor(code: OFAErrorCode, userMessage: string, cause?: Error) {
    super(userMessage, { cause });
    this.name = "OFAError";
    this.code = code;
    this.userMessage = userMessage;
  }
}

/** Map internal errors to human-readable messages */
const ERROR_MESSAGES: Record<OFAErrorCode, string> = {
  "connection-timeout":
    "The connection is taking too long. Make sure both devices are online and try again.",
  "connection-failed":
    "The connection could not be established. Create a new transfer and try again.",
  "peer-disconnected":
    "The other device is no longer connected. Create a new transfer and try again.",
  "session-expired":
    "The pairing session has expired. Create a new transfer to try again.",
  "session-not-found":
    "The session could not be found. It may have expired. Ask the sender for a new link.",
  "transfer-cancelled":
    "The transfer was cancelled.",
  "transfer-failed":
    "The transfer failed. Create a new transfer and try again.",
  "decryption-failed":
    "A data chunk could not be verified. The transfer may have been tampered with or corrupted. Try again.",
  "encryption-failed":
    "Encryption failed. Your browser may not support the required cryptographic operations.",
  "invalid-chunk":
    "Received invalid data. The transfer may be corrupted. Try again.",
  "invalid-session-id":
    "The session link is not valid. Ask the sender for a new link.",
  "unsupported-browser":
    "Your browser does not support the features required for OFA. Try using a recent version of Chrome, Firefox, Safari, or Edge.",
  "signaling-error":
    "Could not reach the pairing server. Check your internet connection and try again.",
  "file-too-large":
    "One or more files exceed the size limit.",
  "too-many-files":
    "Too many files selected for a single transfer.",
  "camera-denied":
    "Camera access was denied. You can still copy the secure link instead.",
  "clipboard-denied":
    "Could not copy to clipboard. Try copying the link manually.",
  "unknown":
    "Something went wrong. Try creating a new transfer.",
};

export function getUserMessage(code: OFAErrorCode): string {
  return ERROR_MESSAGES[code] ?? ERROR_MESSAGES["unknown"];
}

export function createError(code: OFAErrorCode, cause?: Error): OFAError {
  return new OFAError(code, getUserMessage(code), cause);
}

/**
 * Detect if the browser supports required features.
 * Returns null if supported, or an error code if not.
 */
export function checkBrowserSupport(): OFAErrorCode | null {
  if (typeof RTCPeerConnection === "undefined") return "unsupported-browser";
  if (typeof RTCDataChannel === "undefined") return "unsupported-browser";
  if (typeof crypto?.subtle === "undefined") return "unsupported-browser";
  if (typeof BroadcastChannel === "undefined") return "unsupported-browser";
  return null;
}

/**
 * Wrap a WebRTC/transfer operation with timeout.
 * Rejects with connection-timeout if the operation takes too long.
 */
export function withTimeout<T>(
  promise: Promise<T>,
  timeoutMs: number = 30_000,
): Promise<T> {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(
      () => reject(createError("connection-timeout")),
      timeoutMs,
    );
    promise.then(
      (result) => { clearTimeout(timer); resolve(result); },
      (error) => { clearTimeout(timer); reject(error); },
    );
  });
}