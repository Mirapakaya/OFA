// OFA Server Signaling Transport
//
// Uses the OFA API routes for cross-device signaling.
// Signaling state is stored in @vercel/kv with automatic TTL.
// This is a deployment compromise: the "no database" principle
// applies to user data, not to the minimum ephemeral signaling
// state required for WebRTC peer discovery.
//
// Signaling state contains ONLY:
// - WebRTC offer/answer (SDP)
// - ICE candidates
// - Session coordination data
//
// It NEVER contains:
// - File contents
// - Encryption keys
// - Personal information
// - Persistent data
//
// All signaling state expires automatically after 5 minutes.

import type { SignalingTransport } from "./index";

const SIGNALING_API = "/api/signaling";
const POLL_INTERVAL_MS = 1000;

interface SessionState {
  offer?: RTCSessionDescriptionInit;
  answer?: RTCSessionDescriptionInit;
  iceCandidates: Record<string, RTCIceCandidateInit[]>;
  createdAt: number;
}

/**
 * Create a server-based signaling transport.
 * Uses polling to exchange WebRTC signaling data via the API.
 */
export function createServerSignalingTransport(
  sessionId: string,
): SignalingTransport {
  let offerCallback: ((offer: RTCSessionDescriptionInit) => void) | null = null;
  let answerCallback: ((answer: RTCSessionDescriptionInit) => void) | null = null;
  let iceCallback: ((candidate: RTCIceCandidateInit) => void) | null = null;
  let polling = false;
  let abortController: AbortController | null = null;

  async function fetchSession(): Promise<SessionState | null> {
    try {
      const res = await fetch(`${SIGNALING_API}/${sessionId}`);
      if (res.status === 404) return null;
      if (!res.ok) throw new Error(`Signaling fetch failed: ${res.status}`);
      return (await res.json()) as SessionState;
    } catch {
      return null;
    }
  }

  async function updateSession(data: Partial<SessionState>): Promise<void> {
    await fetch(`${SIGNALING_API}/${sessionId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  }

  async function startPolling(): Promise<void> {
    if (polling) return;
    polling = true;
    abortController = new AbortController();

    let lastOffer: string | null = null;
    let lastAnswer: string | null = null;
    let seenIceCandidates = new Set<string>();

    while (polling) {
      const state = await fetchSession();
      if (state) {
        // Check for new offer
        if (state.offer) {
          const offerKey = JSON.stringify(state.offer);
          if (offerKey !== lastOffer) {
            lastOffer = offerKey;
            offerCallback?.(state.offer);
          }
        }

        // Check for new answer
        if (state.answer) {
          const answerKey = JSON.stringify(state.answer);
          if (answerKey !== lastAnswer) {
            lastAnswer = answerKey;
            answerCallback?.(state.answer);
          }
        }

        // Check for new ICE candidates
        if (state.iceCandidates) {
          for (const [role, candidates] of Object.entries(state.iceCandidates)) {
            for (const candidate of candidates) {
              const key = `${role}:${candidate.candidate}`;
              if (!seenIceCandidates.has(key)) {
                seenIceCandidates.add(key);
                iceCallback?.(candidate);
              }
            }
          }
        }
      }

      // Wait before next poll
      await new Promise((resolve) => {
        const timer = setTimeout(resolve, POLL_INTERVAL_MS);
        abortController?.signal.addEventListener("abort", () => {
          clearTimeout(timer);
          resolve(undefined);
        }, { once: true });
      });
    }
  }

  function stopPolling(): void {
    polling = false;
    abortController?.abort();
    abortController = null;
  }

  return {
    async createSession(): Promise<string> {
      await updateSession({
        iceCandidates: {},
        createdAt: Date.now(),
      });
      startPolling();
      return sessionId;
    },

    async joinSession(sid: string): Promise<void> {
      // Start polling for the session
      startPolling();
    },

    async sendOffer(sid: string, offer: RTCSessionDescriptionInit): Promise<void> {
      await updateSession({ offer });
    },

    async sendAnswer(sid: string, answer: RTCSessionDescriptionInit): Promise<void> {
      await updateSession({ answer });
    },

    async sendIceCandidate(sid: string, candidate: RTCIceCandidateInit): Promise<void> {
      // Append ICE candidate to the session
      await fetch(`${SIGNALING_API}/${sessionId}/ice`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(candidate),
      });
    },

    onOffer(callback: (offer: RTCSessionDescriptionInit) => void): void {
      offerCallback = callback;
    },

    onAnswer(callback: (answer: RTCSessionDescriptionInit) => void): void {
      answerCallback = callback;
    },

    onIceCandidate(callback: (candidate: RTCIceCandidateInit) => void): void {
      iceCallback = callback;
    },

    close(): void {
      stopPolling();
      // Attempt to delete signaling state from server
      fetch(`${SIGNALING_API}/${sessionId}`, {
        method: "DELETE",
      }).catch(() => {
        // Best effort — ignore failures
      });
      offerCallback = null;
      answerCallback = null;
      iceCallback = null;
    },
  };
}