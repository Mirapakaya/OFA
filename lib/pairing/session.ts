// OFA Pairing Session
//
// Creates and manages ephemeral pairing sessions.
// Session IDs are high-entropy random identifiers.
// Cryptographic secrets are placed in URL fragments
// so they are never transmitted to the server.

/** Session lifetime before expiration (5 minutes) */
export const SESSION_TTL_MS = 5 * 60 * 1000;

/** Generate cryptographically random session ID (32 bytes, hex-encoded) */
export function generateSessionId(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(32));
  return Array.from(bytes)
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

/** Generate cryptographically random secret (32 bytes, hex-encoded) */
export function generateSecret(): string {
  const bytes = crypto.getRandomValues(new Uint8Array(32));
  return Array.from(bytes)
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

export interface PairingSession {
  sessionId: string;
  secret: string;
  createdAt: number;
  expiresAt: number;
  url: string;
}

/**
 * Create a new pairing session with a secure link.
 * The session ID is in the URL path (sent to server for signaling).
 * The secret is in the URL fragment (never sent to server).
 * The fragment is used to derive additional cryptographic context.
 */
export function createSendSession(baseUrl: string): PairingSession {
  const sessionId = generateSessionId();
  const secret = generateSecret();
  const now = Date.now();

  // URL format: /s/<session>#<secret>
  // The fragment (#secret) is handled client-side and never sent to the server
  const url = `${baseUrl}/s/${sessionId}#${secret}`;

  return {
    sessionId,
    secret,
    createdAt: now,
    expiresAt: now + SESSION_TTL_MS,
    url,
  };
}

/**
 * Parse a pairing URL to extract session ID and secret.
 * Returns null if the URL format is invalid.
 */
export function parsePairingUrl(url: string): {
  sessionId: string;
  secret: string;
} | null {
  try {
    const parsed = new URL(url);
    const pathParts = parsed.pathname.split("/").filter(Boolean);

    // Expected: /s/<sessionId>
    if (pathParts.length < 2 || pathParts[0] !== "s") {
      return null;
    }

    const sessionId = pathParts[1];
    const secret = parsed.hash.slice(1); // Remove leading #

    if (!sessionId || !/^[a-f0-9]{64}$/.test(sessionId) || !secret || secret.length < 32) {
      return null;
    }

    return { sessionId, secret };
  } catch {
    return null;
  }
}

/**
 * Check if a session has expired.
 */
export function isSessionExpired(session: { expiresAt: number }): boolean {
  return Date.now() >= session.expiresAt;
}

/**
 * Get remaining time in seconds before session expires.
 */
export function getSessionRemainingTime(session: { expiresAt: number }): number {
  return Math.max(0, Math.ceil((session.expiresAt - Date.now()) / 1000));
}