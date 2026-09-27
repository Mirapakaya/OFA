// OFA Transfer Receiver
//
// Receives encrypted chunks from the DataChannel,
// decrypts and authenticates each chunk,
// reconstructs files, and triggers browser downloads.
// Metadata is received through the encrypted peer channel.

import { decryptChunk, deserializeChunk } from "@/lib/crypto/cipher";
import { reconstructFile, downloadFile, type FileMetadata } from "./index";
import { formatSize, formatETA, sanitizeFileName } from "./file-utils";

export interface ReceiveProgress {
  fileName: string;
  fileIndex: number;
  fileTotal: number;
  chunkIndex: number;
  chunkTotal: number;
  bytesTransferred: number;
  totalBytes: number;
  percent: number;
  speed: number;
  speedFormatted: string;
  eta: number;
  etaFormatted: string;
}

export type ReceiveProgressCallback = (progress: ReceiveProgress) => void;

interface FileState {
  metadata: FileMetadata;
  chunks: ArrayBuffer[];
  receivedChunks: number;
  receivedIndices: Set<number>;
}

/**
 * Receive files from the DataChannel.
 * Listens for incoming messages, parses protocol messages,
 * decrypts file chunks, and reconstructs files.
 *
 * Returns a promise that resolves when the transfer is complete.
 */
export function receiveFiles(
  channel: RTCDataChannel,
  encryptionKey: CryptoKey,
  onProgress: ReceiveProgressCallback,
  onComplete: () => void,
): { cancel: () => void } {
  let cancelled = false;
  const fileStates = new Map<number, FileState>();
  let totalBytes = 0;
  let bytesTransferred = 0;
  let fileCount = 0;
  let startTime = Date.now();

  async function handleMessage(data: ArrayBuffer): Promise<void> {
    if (cancelled) return;

    // Try to parse as a control message (JSON)
    try {
      const text = new TextDecoder().decode(data);
      const msg = JSON.parse(text);

      if (msg.type === "transfer-start") {
        fileCount = msg.fileCount;
        totalBytes = msg.totalBytes;
        startTime = Date.now();
        return;
      }

      if (msg.type === "file-start") {
        const metadata = msg.metadata as FileMetadata;
        fileStates.set(msg.fileIndex, {
          metadata,
          chunks: new Array(metadata.totalChunks),
          receivedChunks: 0,
          receivedIndices: new Set<number>(),
        });
        return;
      }

      if (msg.type === "file-end") {
        // File complete — reconstruct and download
        const state = fileStates.get(msg.fileIndex);
        if (state) {
          const blob = reconstructFile(state.chunks, state.metadata);
          downloadFile(blob, sanitizeFileName(state.metadata.name));
          // Release chunk references for GC
          state.chunks = [];
        }
        return;
      }

      if (msg.type === "transfer-complete") {
        onComplete();
        return;
      }

      // If it parsed as JSON but isn't a known type, ignore
      return;
    } catch {
      // Not JSON — treat as an encrypted chunk
    }

    // Process as encrypted chunk
    try {
      const encryptedChunk = deserializeChunk(data);
      const decrypted = await decryptChunk(encryptionKey, encryptedChunk);

      // Find the current file being received
      const fileIndex = findCurrentFileIndex(fileStates);
      if (fileIndex === null) return;

      const state = fileStates.get(fileIndex)!;

      // Reject duplicate chunks
      if (state.receivedIndices.has(encryptedChunk.index)) {
        console.warn("Duplicate chunk rejected:", encryptedChunk.index);
        return;
      }
      state.receivedIndices.add(encryptedChunk.index);

      state.chunks[encryptedChunk.index] = decrypted;
      state.receivedChunks++;
      bytesTransferred += decrypted.byteLength;

      const elapsed = (Date.now() - startTime) / 1000;
      const speed = elapsed > 0 ? bytesTransferred / elapsed : 0;
      const remaining = totalBytes - bytesTransferred;
      const eta = speed > 0 ? remaining / speed : 0;

      onProgress({
        fileName: state.metadata.name,
        fileIndex,
        fileTotal: fileCount,
        chunkIndex: encryptedChunk.index,
        chunkTotal: state.metadata.totalChunks,
        bytesTransferred,
        totalBytes,
        percent: totalBytes > 0 ? (bytesTransferred / totalBytes) * 100 : 0,
        speed,
        speedFormatted: formatSize(speed) + "/s",
        eta,
        etaFormatted: formatETA(eta),
      });
    } catch (error) {
      // Decryption failed — invalid authentication tag, corrupted data, etc.
      // Per the spec: malformed encrypted data is rejected.
      console.error("Chunk decryption failed:", error);
    }
  }

  // Set up message handler
  channel.onmessage = (event) => {
    if (event.data instanceof ArrayBuffer) {
      handleMessage(event.data);
    }
  };

  return {
    cancel: () => {
      cancelled = true;
      channel.onmessage = null;
    },
  };
}

function findCurrentFileIndex(
  fileStates: Map<number, FileState>,
): number | null {
  // Find the file with the most recent "file-start" that hasn't completed
  for (const [index, state] of fileStates) {
    if (state.receivedChunks < state.metadata.totalChunks) {
      return index;
    }
  }
  return null;
}