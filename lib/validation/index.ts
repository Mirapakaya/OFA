// OFA Input Validation
//
// Runtime validation at every trust boundary.
// Every function returns { valid: boolean; error?: string }.

/** Session ID: 64 lowercase hex characters (32 bytes) */
const SESSION_ID_RE = /^[a-f0-9]{64}$/;

export function validateSessionId(id: string): { valid: boolean; error?: string } {
  if (typeof id !== "string") return { valid: false, error: "Session ID must be a string" };
  if (!SESSION_ID_RE.test(id)) return { valid: false, error: "Invalid session ID format" };
  return { valid: true };
}

/** Signaling message: must have a known type and valid structure */
const SIGNALING_TYPES = new Set(["offer", "answer", "ice-candidate"]);

export function validateSignalingMessage(msg: unknown): { valid: boolean; error?: string } {
  if (!msg || typeof msg !== "object") return { valid: false, error: "Invalid message" };
  const obj = msg as Record<string, unknown>;
  if (typeof obj.type !== "string" || !SIGNALING_TYPES.has(obj.type)) {
    return { valid: false, error: "Unknown signaling message type" };
  }
  if (obj.type === "offer" || obj.type === "answer") {
    if (!obj.sdp || typeof obj.sdp !== "string") return { valid: false, error: "Missing SDP" };
  }
  if (obj.type === "ice-candidate") {
    if (typeof obj.candidate !== "string") return { valid: false, error: "Missing ICE candidate" };
  }
  return { valid: true };
}

/** MIME type format check */
const MIME_RE = /^[a-zA-Z0-9][a-zA-Z0-9!#$&\-^_.+]*\/[a-zA-Z0-9][a-zA-Z0-9!#$&\-^_.+]*$/;

/** File metadata: validate name, size, type, totalChunks */
export function validateFileMetadata(meta: unknown): { valid: boolean; error?: string } {
  if (!meta || typeof meta !== "object") return { valid: false, error: "Invalid metadata" };
  const obj = meta as Record<string, unknown>;

  if (typeof obj.name !== "string" || obj.name.length === 0 || obj.name.length > 255) {
    return { valid: false, error: "Invalid file name" };
  }
  // No path separators
  if (/[/\\]/.test(obj.name)) return { valid: false, error: "File name must not contain path separators" };

  if (typeof obj.size !== "number" || !Number.isFinite(obj.size) || obj.size < 0) {
    return { valid: false, error: "Invalid file size" };
  }

  if (typeof obj.type !== "string" || !MIME_RE.test(obj.type)) {
    return { valid: false, error: "Invalid MIME type" };
  }

  if (typeof obj.totalChunks !== "number" || !Number.isInteger(obj.totalChunks) || obj.totalChunks < 1) {
    return { valid: false, error: "Invalid chunk count" };
  }

  return { valid: true };
}

/** Encrypted chunk: validate index, nonce length, data presence */
export function validateEncryptedChunk(chunk: unknown): { valid: boolean; error?: string } {
  if (!chunk || typeof chunk !== "object") return { valid: false, error: "Invalid chunk" };
  const obj = chunk as Record<string, unknown>;

  if (typeof obj.index !== "number" || !Number.isInteger(obj.index) || obj.index < 0) {
    return { valid: false, error: "Invalid chunk index" };
  }

  if (!(obj.nonce instanceof Uint8Array) || obj.nonce.length !== 12) {
    return { valid: false, error: "Invalid nonce" };
  }

  if (!(obj.ciphertext instanceof ArrayBuffer) && !(obj.ciphertext instanceof Uint8Array)) {
    return { valid: false, error: "Missing ciphertext" };
  }

  return { valid: true };
}