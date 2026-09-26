// OFA Cryptography Module
//
// Provides browser-side encryption for file transfers.
// All cryptographic operations use well-established primitives.
// No custom cryptography.

// Key generation and ECDH agreement
export { generateKeyPair, performKeyAgreement, destroyKeyPair } from "./keys";
export type { KeyPair } from "./keys";

// Key derivation (HKDF-SHA-256)
export { deriveKey, deriveSessionKeys, deriveVerificationPhrase, HKDF_LABELS } from "./kdf";
export type { HkdfLabel } from "./kdf";

// AES-256-GCM encryption/decryption
export { encryptChunk, decryptChunk, serializeChunk, deserializeChunk, PROTOCOL_VERSION } from "./cipher";
export type { EncryptedChunk } from "./cipher";

// Nonce management
export { NonceManager, generateNonce } from "./nonce";