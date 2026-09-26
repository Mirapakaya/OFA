# OFA — Architecture

## System Overview

```
                OFA WEBSITE

            ┌─────────────────┐
            │   Next.js UI    │
            │   Multi-page    │
            └────────┬────────┘
                     │
         Browser cryptography
                     │
      ┌──────────────┴──────────────┐
      │                             │
      ▼                             ▼
Local file handling          Ephemeral pairing
      │                             │
      ▼                             ▼
Web Crypto / crypto             Signaling
      │                             │
      └──────────────┬──────────────┘
                     │
                     ▼
             WebRTC DataChannel
                     │
              Encrypted chunks
                     │
                     ▼
               Other browser
```

## Layers

1. **Presentation** — Pages, layout, forms, drag-and-drop, progress, accessibility
2. **Pairing** — Session creation, joining, QR/links, same-device pairing
3. **Signaling** — Offer, answer, ICE candidates only
4. **Cryptography** — Key generation, ECDH/X25519, HKDF, AES-GCM, nonce management
5. **Transfer Engine** — File slicing, chunking, encryption, sending, backpressure, reconstruction
6. **WebRTC** — RTCPeerConnection, DataChannel, ICE, timeout, cleanup

## Session Model

Temporary state only: sessionId, protocolVersion, createdAt, expiresAt, peerConnectionState, signalingState, ephemeralCryptoState, transferState.

No database. No persistent identity.

## Session Lifetime

created → waiting → joined → connected → transferring → completed → destroyed

## Link Design

`https://ofa.example/s/<ephemeral-session>#<secret>`

Fragment handled client-side, never transmitted to server.

## SignalingTransport Abstraction

Signaling is isolated behind `SignalingTransport` interface so OFA remains transport-agnostic.