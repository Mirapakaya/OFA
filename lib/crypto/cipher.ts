// OFA Cipher — AES-256-GCM
//
// Authenticated encryption for file chunks.
// Each chunk is independently encrypted and authenticated.
// The receiver validates the authentication tag before accepting a chunk.
// Nonces are never reused with the same key (see nonce.ts).

import { NonceManager } from "./nonce";

export interface EncryptedChunk {
  /** Chunk index for ordering */
  index: number;
  /** 12-byte AES-GCM nonce */
  nonce: Uint8Array;
  /** Encrypted data (ciphertext + authentication tag appended by AES-GCM) */
  ciphertext: ArrayBuffer;
  /** Additional authenticated data: chunk index + protocol version */
  aad: Uint8Array;
}

export const PROTOCOL_VERSION = 1;

/**
 * Build additional authenticated data (AAD) for a chunk.
 * AAD binds the chunk index and protocol version to the ciphertext,
 * preventing chunk reordering or protocol downgrade attacks.
 */
function buildAAD(chunkIndex: number): Uint8Array {
  const aad = new Uint8Array(8);
  const view = new DataView(aad.buffer);
  view.setUint32(0, PROTOCOL_VERSION, false); // big-endian
  view.setUint32(4, chunkIndex, false);       // big-endian
  return aad;
}

/**
 * Encrypt a file chunk with AES-256-GCM.
 *
 * Security properties:
 * - Each chunk gets a unique nonce (see NonceManager)
 * - AAD binds chunk index + protocol version to the ciphertext
 * - Authentication tag prevents tampering
 * - Invalid chunks are rejected by the receiver
 */
export async function encryptChunk(
  key: CryptoKey,
  plaintext: ArrayBuffer,
  nonceManager: NonceManager,
  chunkIndex: number,
): Promise<EncryptedChunk> {
  const nonce = nonceManager.nextNonce();
  const aad = buildAAD(chunkIndex);

  const ciphertext = await crypto.subtle.encrypt(
    {
      name: "AES-GCM",
      iv: nonce as Uint8Array<ArrayBuffer>,
      additionalData: aad as Uint8Array<ArrayBuffer>,
      tagLength: 128, // 128-bit authentication tag
    },
    key,
    plaintext,
  );

  return {
    index: chunkIndex,
    nonce,
    ciphertext,
    aad,
  };
}

/**
 * Decrypt and authenticate a file chunk.
 *
 * Throws if:
 * - The authentication tag is invalid (tampered data)
 * - The chunk index doesn't match the AAD
 * - The protocol version doesn't match
 *
 * The caller must handle the error and reject the chunk.
 */
export async function decryptChunk(
  key: CryptoKey,
  chunk: EncryptedChunk,
): Promise<ArrayBuffer> {
  // Verify the AAD matches expected values before decrypting
  const expectedAAD = buildAAD(chunk.index);
  if (chunk.aad.length !== expectedAAD.length) {
    throw new Error(`AAD length mismatch: expected ${expectedAAD.length}, got ${chunk.aad.length}`);
  }
  for (let i = 0; i < expectedAAD.length; i++) {
    if (chunk.aad[i] !== expectedAAD[i]) {
      throw new Error("AAD mismatch — possible chunk reordering or tampering");
    }
  }

  try {
    const plaintext = await crypto.subtle.decrypt(
      {
        name: "AES-GCM",
        iv: chunk.nonce as Uint8Array<ArrayBuffer>,
        additionalData: chunk.aad as Uint8Array<ArrayBuffer>,
        tagLength: 128,
      },
      key,
      chunk.ciphertext,
    );
    return plaintext;
  } catch {
    throw new Error(`Decryption failed for chunk ${chunk.index} — invalid authentication tag`);
  }
}

/**
 * Serialize an encrypted chunk for transmission over DataChannel.
 * Format: [4 bytes index] [12 bytes nonce] [8 bytes AAD] [rest: ciphertext]
 */
export function serializeChunk(chunk: EncryptedChunk): ArrayBuffer {
  const indexBytes = new Uint8Array(4);
  new DataView(indexBytes.buffer).setUint32(0, chunk.index, false);

  const total = 4 + 12 + 8 + chunk.ciphertext.byteLength;
  const buffer = new Uint8Array(total);
  buffer.set(indexBytes, 0);
  buffer.set(chunk.nonce, 4);
  buffer.set(chunk.aad, 16);
  buffer.set(new Uint8Array(chunk.ciphertext), 24);

  return buffer.buffer as ArrayBuffer;
}

/**
 * Deserialize a chunk received over DataChannel.
 * Validates format before returning.
 */
export function deserializeChunk(data: ArrayBuffer): EncryptedChunk {
  if (data.byteLength < 24) {
    throw new Error("Chunk data too short — minimum 24 bytes header");
  }

  const view = new DataView(data);
  const index = view.getUint32(0, false);
  const nonce = new Uint8Array(data, 4, 12);
  const aad = new Uint8Array(data, 16, 8);
  const ciphertext = data.slice(24);

  return { index, nonce, ciphertext, aad };
}
