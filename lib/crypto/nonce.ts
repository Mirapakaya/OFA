// OFA Nonce Management
//
// AES-GCM requires a unique nonce for every encryption operation
// with the same key. Reusing a nonce with the same key completely
// breaks AES-GCM confidentiality and authenticity.
//
// This module manages nonces to guarantee uniqueness.
// Each session gets a fresh NonceManager. Nonces are 12 bytes (96 bits)
// as required by AES-GCM. The first 4 bytes are a random per-session
// prefix, the remaining 8 bytes are a counter. This allows 2^64 chunks
// per session before the counter wraps — far more than any practical
// file transfer requires.

export class NonceManager {
  private prefix: Uint8Array;
  private counter: bigint;
  private exhausted: boolean;

  constructor() {
    // 4-byte random prefix ensures different sessions don't produce
    // overlapping nonces even if counters start at 0
    this.prefix = crypto.getRandomValues(new Uint8Array(4));
    this.counter = 0n;
    this.exhausted = false;
  }

  /**
   * Generate the next unique nonce.
   * Throws if the nonce space is exhausted (extremely unlikely).
   */
  nextNonce(): Uint8Array {
    if (this.exhausted) {
      throw new Error("Nonce space exhausted — this session must be terminated");
    }

    const nonce = new Uint8Array(12);

    // Copy 4-byte prefix
    nonce.set(this.prefix, 0);

    // Copy 8-byte counter (big-endian)
    const view = new DataView(nonce.buffer, nonce.byteOffset, 12);
    view.setBigUint64(4, this.counter, false);

    this.counter += 1n;

    // Check for exhaustion (2^64 nonces per prefix)
    if (this.counter === 0n) {
      // Counter wrapped around — must not continue
      this.exhausted = true;
    }

    return nonce;
  }

  /**
   * Get the current counter value (for diagnostics).
   */
  getCounter(): bigint {
    return this.counter;
  }

  /**
   * Destroy the nonce manager.
   * After calling this, the manager must not be used again.
   */
  destroy(): void {
    this.exhausted = true;
    this.prefix = new Uint8Array(4); // Zero out
    this.counter = 0n;
  }
}

/**
 * Generate a single random nonce for one-off operations.
 * Use NonceManager for repeated encryption within a session.
 */
export function generateNonce(): Uint8Array {
  return crypto.getRandomValues(new Uint8Array(12));
}