// OFA Key Derivation
//
// Uses HKDF with SHA-256 to derive session encryption keys
// from the ECDH shared secret.
// Domain separation ensures keys for different purposes
// are cryptographically independent.
// The salt should be the session ID bytes to provide
// cross-session key separation.

/** HKDF domain separation labels */
export const HKDF_LABELS = {
  /** Main encryption key for file data */
  ENCRYPTION_KEY: "ofa-encryption-key-v1",
  /** Key for file metadata (filenames, sizes, types) */
  METADATA_KEY: "ofa-metadata-key-v1",
  /** Key for protocol control messages */
  CONTROL_KEY: "ofa-verification-key-v1",
  /** Key for verification phrases */
  VERIFICATION_KEY: "ofa-verification-key-v1",
} as const;

export type HkdfLabel = (typeof HKDF_LABELS)[keyof typeof HKDF_LABELS];

/**
 * Derive an AES-256-GCM encryption key using HKDF-SHA-256.
 *
 * @param sharedSecret - Raw ECDH shared secret bits
 * @param label - Domain separation label
 * @param salt - Salt for HKDF extraction (use session ID bytes for cross-session separation)
 * @returns A CryptoKey ready for AES-GCM operations
 */
export async function deriveKey(
  sharedSecret: ArrayBuffer,
  label: HkdfLabel,
  salt?: Uint8Array,
): Promise<CryptoKey> {
  // Step 1: HKDF-Extract — PRK = HMAC-SHA-256(salt, IKM)
  const saltBytes = salt ?? new Uint8Array(32);

  const prk = await crypto.subtle.importKey(
    "raw",
    saltBytes,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );

  const prkBits = await crypto.subtle.sign("HMAC", prk, sharedSecret);

  // Step 2: HKDF-Expand — OKM = T(1) || T(2) || ...
  // For AES-256 we need exactly 32 bytes (one block of SHA-256 output)
  const prkKey = await crypto.subtle.importKey(
    "raw",
    prkBits,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );

  // T(1) = HMAC-SHA-256(PRK, info || 0x01)
  const info = new TextEncoder().encode(label);
  const t1Input = new Uint8Array(info.length + 1);
  t1Input.set(info);
  t1Input[info.length] = 1; // Counter byte

  const okm = await crypto.subtle.sign("HMAC", prkKey, t1Input);

  // Step 3: Import derived key material as AES-256-GCM key
  const encryptionKey = await crypto.subtle.importKey(
    "raw",
    okm,
    { name: "AES-GCM" },
    false, // NOT extractable — key cannot be exported
    ["encrypt", "decrypt"],
  );

  return encryptionKey;
}

/**
 * Derive a human-readable verification phrase from the shared secret.
 * Both peers should display the same phrase if they share the same secret.
 * Uses the same salt as the rest of the session keys for consistency.
 */
export async function deriveVerificationPhrase(
  sharedSecret: ArrayBuffer,
  salt?: Uint8Array,
): Promise<string> {
  // Derive raw bytes for the phrase using the verification HKDF label
  const saltBytes = salt ?? new Uint8Array(32);

  const prk = await crypto.subtle.importKey(
    "raw",
    saltBytes,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const prkBits = await crypto.subtle.sign("HMAC", prk, sharedSecret);
  const prkKey = await crypto.subtle.importKey(
    "raw",
    prkBits,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );

  const info = new TextEncoder().encode(HKDF_LABELS.VERIFICATION_KEY);
  const t1Input = new Uint8Array(info.length + 1);
  t1Input.set(info);
  t1Input[info.length] = 1;
  const phraseBytes = new Uint8Array(
    await crypto.subtle.sign("HMAC", prkKey, t1Input),
  );

  // Map bytes to word lists for a readable phrase
  const wordList = [
    "oak", "pine", "elm", "ash", "maple", "birch", "cedar", "willow",
    "stone", "river", "cloud", "wind", "rain", "snow", "lake", "hill",
    "hawk", "crow", "dove", "swan", "fox", "wolf", "bear", "deer",
    "gold", "iron", "jade", "ruby", "opal", "sage", "mint", "slate",
  ];

  const word1 = wordList[phraseBytes[0]! % wordList.length];
  const number = (phraseBytes[1]! % 99) + 1;
  const word2 = wordList[phraseBytes[2]! % wordList.length];

  return `${word1} — ${number} — ${word2}`;
}

/**
 * Derive the full set of session keys from a shared secret.
 * Returns keys for encryption, metadata, control, and verification.
 * All keys use the same salt for consistent derivation.
 */
export async function deriveSessionKeys(sharedSecret: ArrayBuffer, salt?: Uint8Array) {
  const [encryptionKey, metadataKey, controlKey, verificationKey] = await Promise.all([
    deriveKey(sharedSecret, HKDF_LABELS.ENCRYPTION_KEY, salt),
    deriveKey(sharedSecret, HKDF_LABELS.METADATA_KEY, salt),
    deriveKey(sharedSecret, HKDF_LABELS.CONTROL_KEY, salt),
    deriveKey(sharedSecret, HKDF_LABELS.VERIFICATION_KEY, salt),
  ]);

  return { encryptionKey, metadataKey, controlKey, verificationKey };
}