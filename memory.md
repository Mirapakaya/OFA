# OFA — Project Memory

## Identity

OFA is a web-only privacy-first encrypted file-transfer product.

## Core Requirements

- No accounts, no auth, no database, no cookies
- No analytics, no advertising, no tracking
- No permanent file storage, no application-level user data collection
- Browser-side encryption, temporary signaling, WebRTC transfer
- Multi-page website (not SPA), Vercel deployment
- Drag and drop, mobile + desktop first-class

## Technical Stack

Next.js · React · TypeScript · Web Crypto API · WebRTC · Vercel

## Cryptography

Ephemeral key agreement (X25519 / P-256 ECDH) · HKDF-SHA-256 · AES-256-GCM
Cryptographically secure random nonces · Authenticated encryption per chunk

## Product Philosophy

OFA does one thing: move files between people without turning the service into a permanent copy of those files.

Intentionally limited. No profiles, social feeds, accounts, likes, history, storage, subscriptions, advertising, recommendations, AI features.

## Change Control

Any future feature must be checked against:
1. Does it require identity?
2. Does it require persistent storage?
3. Does it require tracking?
4. Does it expose plaintext?
5. Does it increase metadata?
6. Does it complicate the threat model?
7. Does it require a database?
8. Does it change the no-account model?
9. Does it make the UI noisier?

A feature violating a core privacy principle is an architectural change, not a quiet addition.