// OFA Transfer Sender
//
// Reads file chunks, encrypts each chunk with AES-256-GCM,
// and sends the serialized encrypted chunk over the DataChannel.
// Tracks progress and supports cancellation via AbortSignal.

import { readFileChunks, DEFAULT_CHUNK_SIZE } from "./chunk-reader";
import { createFileMetadata, type FileMetadata } from "./index";
import { encryptChunk, serializeChunk, type EncryptedChunk } from "@/lib/crypto/cipher";
import { NonceManager } from "@/lib/crypto/nonce";
import { sendDataWithBackpressure, isChannelReady } from "@/lib/webrtc/data-channel";
import { formatSize, formatSpeed, formatETA } from "./file-utils";

export interface SendProgress {
  fileIndex: number;
  fileTotal: number;
  fileName: string;
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

export type SendProgressCallback = (progress: SendProgress) => void;

/**
 * Send one or more files over the DataChannel.
 * Each file is chunked, encrypted, and sent sequentially.
 * Metadata (filename, size, type) is sent first as a control message.
 *
 * Protocol:
 * 1. Send file metadata (JSON) over the channel
 * 2. For each file:
 *    a. Send file start marker
 *    b. Read chunk → encrypt → serialize → send
 *    c. Send file end marker
 * 3. Send transfer complete marker
 */
export async function sendFiles(
  files: File[],
  channel: RTCDataChannel,
  encryptionKey: CryptoKey,
  onProgress: SendProgressCallback,
  signal?: AbortSignal,
): Promise<void> {
  const nonceManager = new NonceManager();
  const totalBytes = files.reduce((sum, f) => sum + f.size, 0);
  let bytesTransferred = 0;
  const startTime = Date.now();

  // Send transfer header
  const transferMeta = {
    type: "transfer-start",
    fileCount: files.length,
    totalBytes,
    timestamp: Date.now(),
  };
  await sendDataWithBackpressure(channel, new TextEncoder().encode(JSON.stringify(transferMeta)).buffer);

  for (let fileIndex = 0; fileIndex < files.length; fileIndex++) {
    if (signal?.aborted) throw new Error("Transfer cancelled");

    const file = files[fileIndex]!;
    const metadata = createFileMetadata(file);
    const chunkTotal = metadata.totalChunks;

    // Send file metadata
    const fileMeta = {
      type: "file-start",
      fileIndex,
      metadata,
    };
    await sendDataWithBackpressure(channel, new TextEncoder().encode(JSON.stringify(fileMeta)).buffer);

    // Send encrypted chunks
    for await (const chunk of readFileChunks(file, DEFAULT_CHUNK_SIZE)) {
      if (signal?.aborted) throw new Error("Transfer cancelled");
      if (!isChannelReady(channel)) throw new Error("DataChannel closed");

      const encrypted = await encryptChunk(
        encryptionKey,
        chunk.data,
        nonceManager,
        chunk.index,
      );

      const serialized = serializeChunk(encrypted);
      await sendDataWithBackpressure(channel, serialized);

      bytesTransferred += chunk.size;
      const elapsed = (Date.now() - startTime) / 1000;
      const speed = elapsed > 0 ? bytesTransferred / elapsed : 0;
      const remaining = totalBytes - bytesTransferred;
      const eta = speed > 0 ? remaining / speed : 0;

      onProgress({
        fileIndex,
        fileTotal: files.length,
        fileName: file.name,
        chunkIndex: chunk.index,
        chunkTotal,
        bytesTransferred,
        totalBytes,
        percent: totalBytes > 0 ? (bytesTransferred / totalBytes) * 100 : 0,
        speed,
        speedFormatted: formatSize(speed) + "/s",
        eta,
        etaFormatted: formatETA(eta),
      });
    }

    // Send file end marker
    const fileEnd = {
      type: "file-end",
      fileIndex,
    };
    await sendDataWithBackpressure(channel, new TextEncoder().encode(JSON.stringify(fileEnd)).buffer);
  }

  // Send transfer complete marker
  const transferEnd = {
    type: "transfer-complete",
    timestamp: Date.now(),
  };
  await sendDataWithBackpressure(channel, new TextEncoder().encode(JSON.stringify(transferEnd)).buffer);

  // Clean up nonce manager
  nonceManager.destroy();
}