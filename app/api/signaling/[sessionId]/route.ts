// OFA Signaling API
//
// Ephemeral signaling endpoint for WebRTC peer discovery.
// Uses @vercel/kv for short-lived session state.
// All data auto-expires after 5 minutes.
// State is deleted immediately after peer connection when possible.
//
// This carries ONLY WebRTC offer/answer/ICE data.
// It NEVER carries file contents, encryption keys, or personal data.

import { kv } from "@vercel/kv";

const SESSION_TTL = 300; // 5 minutes in seconds

interface SessionState {
  offer?: RTCSessionDescriptionInit;
  answer?: RTCSessionDescriptionInit;
  iceCandidates: {
    sender?: RTCIceCandidateInit[];
    receiver?: RTCIceCandidateInit[];
  };
  createdAt: number;
}

function sessionKey(id: string): string {
  return `ofa:signaling:${id}`;
}

/** Validate session ID format — must be 64 hex characters */
function isValidSessionId(id: string): boolean {
  return /^[a-f0-9]{64}$/.test(id);
}

/** Validate Origin/Referer header to prevent CSRF */
function validateOrigin(request: Request): boolean {
  const origin = request.headers.get("Origin");
  const referer = request.headers.get("Referer");
  
  // If neither header is present, allow (some browsers strip these)
  if (!origin && !referer) return true;
  
  const expectedHost = request.headers.get("Host") ?? new URL(request.url).host;
  
  if (origin) {
    try {
      const url = new URL(origin);
      return url.host === expectedHost;
    } catch {
      return false;
    }
  }
  
  if (referer) {
    try {
      const url = new URL(referer);
      return url.host === expectedHost;
    } catch {
      return false;
    }
  }
  
  return true;
}

/** GET — Read signaling state for a session */
export async function GET(
  request: Request,
  { params }: { params: Promise<{ sessionId: string }> },
) {
  if (!validateOrigin(request)) return Response.json({ error: "Forbidden" }, { status: 403 });

  const { sessionId } = await params;

  if (!isValidSessionId(sessionId)) {
    return new Response("Invalid session ID", { status: 400 });
  }

  const state = await kv.get<SessionState>(sessionKey(sessionId));
  if (!state) {
    return new Response("Session not found", { status: 404 });
  }

  return Response.json(state);
}

/** POST — Create or update signaling state */
export async function POST(
  request: Request,
  { params }: { params: Promise<{ sessionId: string }> },
) {
  if (!validateOrigin(request)) return Response.json({ error: "Forbidden" }, { status: 403 });

  const { sessionId } = await params;

  if (!isValidSessionId(sessionId)) {
    return new Response("Invalid session ID", { status: 400 });
  }

  const key = sessionKey(sessionId);
  const existing = await kv.get<SessionState>(key);
  const body = await request.json() as Partial<SessionState>;

  const state: SessionState = {
    ...existing ?? { iceCandidates: {}, createdAt: Date.now() },
    ...body,
    iceCandidates: {
      ...existing?.iceCandidates ?? {},
      ...body.iceCandidates ?? {},
    },
  };

  await kv.set(key, state, { ex: SESSION_TTL });

  return Response.json({ ok: true });
}

/** DELETE — Remove signaling state (cleanup after connection) */
export async function DELETE(
  request: Request,
  { params }: { params: Promise<{ sessionId: string }> },
) {
  if (!validateOrigin(request)) return Response.json({ error: "Forbidden" }, { status: 403 });

  const { sessionId } = await params;

  if (!isValidSessionId(sessionId)) {
    return new Response("Invalid session ID", { status: 400 });
  }

  await kv.del(sessionKey(sessionId));

  return Response.json({ ok: true });
}