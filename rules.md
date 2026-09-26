# OFA — Engineering Rules

1. **Privacy by default** — Never add telemetry, analytics, tracking, ads, or profiling.
2. **No authentication** — Never introduce account creation, login, OAuth, email/phone verification.
3. **No database** — Never add persistent storage.
4. **No file storage** — Never upload files to permanent server-side storage.
5. **Browser-first cryptography** — Plaintext stays in sender browser until transmitted over encrypted peer channel.
6. **No custom cryptography** — Use established primitives and audited libraries.
7. **No persistent identity** — Every transfer is a fresh anonymous session.
8. **No cookies** — Do not set application cookies.
9. **No hidden state** — Do not secretly persist identifiers or tracking information.
10. **No fake security** — Never describe a feature as stronger than it actually is.
11. **No fake zero-log claim** — Do not claim control over infrastructure logs.
12. **No plaintext signaling secrets** — Keep secrets client-side via URL fragment.
13. **No technical UI** — Users never see SDP, ICE, JSON, or protocol details.
14. **No giant client component** — Separate routes and responsibilities.
15. **Strong typing** — Strict TypeScript. No `any`.
16. **Small modules** — Separate crypto, signaling, pairing, transport, transfer, UI.
17. **Deterministic state machines** — No dozens of unrelated booleans for connection state.
18. **Explicit cleanup** — Every temporary resource has cleanup.
19. **No memory leaks** — Revoke object URLs after downloads. Release completed transfer references.
20. **Large-file support** — Never assume entire file fits in memory.
21. **Abort support** — Users must be able to stop active transfers.
22. **Expiration** — Unclaimed sessions expire automatically.
23. **Error messages** — Normal human language, not error codes.
24. **No AI writing** — Plain human language only.
25. **No unnecessary dependencies** — Prefer browser APIs where secure and mature.
26. **Security comments** — Security-sensitive code includes invariant comments.
27. **Never silently downgrade security** — Fail clearly if a capability is unavailable.
28. **Never send plaintext as fallback** — No HTTPS upload, plain HTTP, or server-side file fallback.
29. **Test adversarial cases** — Corrupted chunks, replays, reorders, duplicates, malformed signaling, disconnects, expired sessions, invalid crypto.
30. **Keep documentation synchronized** — Update docs when architecture changes.