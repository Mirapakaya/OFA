// OFA Key Generation and Agreement
//
// Uses the Web Crypto API for all key operations.
// Generates fresh ephemeral key pairs per session — no persistent keys.
// Prefers X25519 where browser support is available, falls back to P-256 ECDH.
// Never reuses keys across sessions.

export interface KeyPair {
  publicKey: CryptoKey;
  privateKey: CryptoKey;
  /** Raw exported public key bytes for transmission to peer */
  publicKeyBytes: Uint8Array;
  /** The curve algorithm used */
  curve: "X25519" | "P-256";
}

/** Check if the browser supports X25519 via Web Crypto */
async function supportsX25519(): Promise<boolean> {
  try {
    await crypto.subtle.generateKey(
      { name: "X25519" } as AlgorithmIdentifier,
      true,
      ["deriveBits"],
    );
    return true;
  } catch {
    return false;
  }
}

/** Export a CryptoKey as raw bytes */
async function exportRawKey(key: CryptoKey): Promise<Uint8Array> {
  const raw = await crypto.subtle.exportKey("raw", key);
  return new Uint8Array(raw);
}

/** Import raw public key bytes */
async function importRawPublicKey(
  rawBytes: Uint8Array,
  curve: "X25519" | "P-256",
): Promise<CryptoKey> {
  const algorithm = curve === "X25519"
    ? { name: "X25519" } as AlgorithmIdentifier
    : { name: "ECDH", namedCurve: "P-256" };

  return crypto.subtle.importKey(
    "raw",
    rawBytes as Uint8Array<ArrayBuffer>,
    algorithm,
    true,
    [],
  );
}

/**
 * Generate a fresh ephemeral key pair for a session.
 * Prefers X25519 where available, falls back to P-256 ECDH.
 * Keys must be newly generated for each session — never reused.
 */
export async function generateKeyPair(): Promise<KeyPair> {
  const useX25519 = await supportsX25519();
  const curve = useX25519 ? "X25519" : "P-256";

  const algorithm = useX25519
    ? { name: "X25519" } as AlgorithmIdentifier
    : { name: "ECDH", namedCurve: "P-256" };

  const keyPair = await crypto.subtle.generateKey(
    algorithm,
    true,
    ["deriveBits"],
  );

  const publicKeyBytes = await exportRawKey(keyPair.publicKey);

  return {
    publicKey: keyPair.publicKey,
    privateKey: keyPair.privateKey,
    publicKeyBytes,
    curve,
  };
}

/**
 * Perform ECDH key agreement with a peer's public key.
 * Returns the raw shared secret bits.
 * The shared secret is NOT used directly as an encryption key —
 * it must be passed through HKDF first (see kdf.ts).
 */
export async function performKeyAgreement(
  localPrivateKey: CryptoKey,
  peerPublicKeyBytes: Uint8Array,
  curve: "X25519" | "P-256",
): Promise<ArrayBuffer> {
  const peerPublicKey = await importRawPublicKey(peerPublicKeyBytes, curve);

  const algorithm = curve === "X25519"
    ? { name: "X25519" } as AlgorithmIdentifier
    : { name: "ECDH" };

  const sharedSecret = await crypto.subtle.deriveBits(
    algorithm,
    localPrivateKey,
    256, // 256 bits
  );

  return sharedSecret;
}

/**
 * Destroy a key pair by zeroing references.
 * The CryptoKey objects become eligible for GC.
 * Call this when a session ends.
 */
export function destroyKeyPair(keyPair: KeyPair): void {
  // Web Crypto keys cannot be explicitly zeroed in memory,
  // but releasing references allows GC. In production,
  // keys are held in the browser's native crypto layer which
  // manages their lifecycle.
  // We null out the references to help GC.
  (keyPair as Record<string, unknown>).publicKey = null;
  (keyPair as Record<string, unknown>).privateKey = null;
  (keyPair as Record<string, unknown>).publicKeyBytes = null;
}
