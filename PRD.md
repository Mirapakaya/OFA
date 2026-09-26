# OFA — Product Requirements Document

## Product

OFA is a privacy-first web platform for direct, encrypted file sharing between people.

Core idea: Select files, connect to another person, transfer them directly, and leave nothing behind.

## Non-Negotiable Principles

1. No account
2. No authentication
3. No database
4. No application-level logs
5. No analytics
6. No advertising
7. No cookies
8. No tracking
9. No permanent uploads
10. No permanent file storage
11. No file readable by OFA servers
12. Files encrypted in browser before transmission
13. Prefer browser-to-browser WebRTC data transfer
14. Temporary signaling only
15. Memory-only session state wherever possible
16. Session destruction after completion, cancellation, expiration, or disconnect
17. No unnecessary third-party services
18. No artificial product language
19. No AI-looking visual design
20. No single-page application architecture
21. Every important action must work with drag and drop
22. Mobile and desktop browsers must both be first-class
23. Deployable on Vercel

## Pairing

- **Mode A — Same browser/device**: Instant pairing via BroadcastChannel / window messaging
- **Mode B — Another device**: QR code or secure link containing ephemeral session info, with cryptographic secret in URL fragment

## Security

- Ephemeral key agreement (X25519 where available, P-256 ECDH otherwise)
- HKDF-SHA-256 key derivation
- AES-256-GCM file encryption
- Cryptographically secure random nonces
- Authenticated encryption per chunk
- No plaintext signaling secrets
- No custom cryptography

## File Pipeline

User selects file → Browser reads → Generate key material → Encrypt locally → Split into chunks → Send over WebRTC → Receiver authenticates/decrypts → Reconstruct → Download

## Metadata Minimization

Do not collect: name, email, phone, account ID, IP, device fingerprint, browser fingerprint, advertising ID, location, contact list, clipboard, camera, microphone, browsing history, referral profile, user behavior, transfer history.

## Acceptance Criteria

See full specification in memory.md.