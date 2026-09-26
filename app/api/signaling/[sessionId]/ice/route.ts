// OFA Signaling — ICE Candidates
//
// Appends ICE candidates to the session state.
// Candidates are stored in the sender or receiver array
// depending on the role header.

import { kv } from "@vercel/kv";

const SESSION_TTL = 300;
const MAX_ICE_CANDIDATES_PER_ROLE = 50;
const VALID_ROLES = new Set(["sender", "receiver"]);

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

function isValidSessionId(id: string): boolean {
  return /^[a-f0-9]{64}$/.test(id);
}

/** Validate the X-Role header value */
function isValidRole(role: string): role is "sender" | "receiver" {
  return VALID_ROLES.has(role);
}

/** POST — Append an ICE candidate to the session */
export async function POST(
  request: Request,
  { params }: { params: Promise<{ sessionId: string }> },
) {
  const { sessionId } = await params;

  if (!isValidSessionId(sessionId)) {
    return new Response("Invalid session ID", { status: 400 });
  }

  const key = sessionKey(sessionId);
  const existing = await kv.get<SessionState>(key);
  if (!existing) {
    return new Response("Session not found", { status: 404 });
  }

  const roleHeader = request.headers.get("X-Role") ?? "";
  if (!isValidRole(roleHeader)) {
    return new Response("Invalid role: must be 'sender' or 'receiver'", { status: 400 });
  }

  const candidate = await request.json() as RTCIceCandidateInit;

  // Basic candidate format validation
  if (typeof candidate.candidate === "string" && !candidate.candidate.startsWith("candidate:")) {
    return new Response("Invalid ICE candidate format", { status: 400 });
  }

  const candidates = existing.iceCandidates ?? {};
  const roleCandidates = candidates[roleHeader] ?? [];

  // Cap ICE candidates per role to prevent abuse
  if (roleCandidates.length >= MAX_ICE_CANDIDATES_PER_ROLE) {
    return new Response("ICE candidate limit reached", { status: 429 });
  }

  roleCandidates.push(candidate);
  candidates[roleHeader] = roleCandidates;

  const updated: SessionState = {
    ...existing,
    iceCandidates: candidates,
  };

  await kv.set(key, updated, { ex: SESSION_TTL });

  return Response.json({ ok: true });
}