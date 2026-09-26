// OFA Transfer Engine
//
// Handles file slicing, chunking, encryption, sending, receiving,
// reconstruction, and progress tracking.

// Chunked file reading
export { DEFAULT_CHUNK_SIZE, readFileChunks, getTotalChunks, readChunk } from "./chunk-reader";
export type { ChunkProgress, ChunkResult } from "./chunk-reader";

// File utilities
export { formatSize, formatSpeed, formatETA, validateFile, validateFiles, MAX_FILE_SIZE, MAX_TOTAL_SIZE, MAX_FILE_COUNT, getExtension, sanitizeFileName } from "./file-utils";
export type { FileValidationResult } from "./file-utils";

// Sender engine
export { sendFiles } from "./sender";
export type { SendProgress, SendProgressCallback } from "./sender";

// Receiver engine
export { receiveFiles } from "./receiver";
export type { ReceiveProgress, ReceiveProgressCallback } from "./receiver";

export const CHUNK_SIZE = 64 * 1024; // 64 KB default

export interface FileMetadata {
  name: string;
  size: number;
  type: string;
  totalChunks: number;
}

export interface TransferState {
  status:
    | "idle"
    | "selecting"
    | "preparing"
    | "pairing"
    | "connecting"
    | "secure-channel"
    | "transferring"
    | "verifying"
    | "complete"
    | "cancelled"
    | "expired"
    | "peer-disconnected"
    | "connection-failed"
    | "transfer-failed"
    | "verification-failed"
    | "unsupported";
  progress: number;
  bytesTransferred: number;
  totalBytes: number;
  speed: number;
}

/** Create FileMetadata from a File object */
export function createFileMetadata(file: File, chunkSize: number = CHUNK_SIZE): FileMetadata {
  return {
    name: file.name,
    size: file.size,
    type: file.type || "application/octet-stream",
    totalChunks: Math.ceil(file.size / chunkSize),
  };
}

/** Reconstruct a file from decrypted chunks */
export function reconstructFile(
  chunks: ArrayBuffer[],
  metadata: FileMetadata,
): Blob {
  return new Blob(chunks, { type: metadata.type });
}

/** Create a download trigger for a reconstructed file */
export function downloadFile(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
  setTimeout(() => URL.revokeObjectURL(url), 5000);
}

/** Compute transfer speed from samples */
export function computeSpeed(bytesTransferred: number, startTime: number, now: number): number {
  const elapsed = (now - startTime) / 1000;
  if (elapsed <= 0) return 0;
  return bytesTransferred / elapsed;
}

/** Compute ETA from speed and remaining bytes */
export function computeETA(speed: number, remainingBytes: number): number {
  if (speed <= 0) return Infinity;
  return remainingBytes / speed;
}