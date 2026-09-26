// OFA Local Signaling Transport
//
// Uses BroadcastChannel for same-browser signaling.
// No server required. Works when both OFA pages
// are open in the same browser context.

import type { SignalingTransport } from "./index";

const SIGNALING_CHANNEL = "ofa-signaling";

interface SignalingMessage {
  type: "offer" | "answer" | "ice-candidate";
  sessionId: string;
  payload: unknown;
}

/**
 * Create a local signaling transport using BroadcastChannel.
 * Only works for same-browser pairing.
 * The sessionId is used to filter messages for a specific session.
 */
export function createLocalSignalingTransport(
  sessionId: string,
): SignalingTransport {
  const channel = new BroadcastChannel(SIGNALING_CHANNEL);
  let offerCallback: ((offer: RTCSessionDescriptionInit) => void) | null = null;
  let answerCallback: ((answer: RTCSessionDescriptionInit) => void) | null = null;
  let iceCallback: ((candidate: RTCIceCandidateInit) => void) | null = null;

  channel.onmessage = (event: MessageEvent<SignalingMessage>) => {
    const msg = event.data;
    if (msg.sessionId !== sessionId) return;

    switch (msg.type) {
      case "offer":
        offerCallback?.(msg.payload as RTCSessionDescriptionInit);
        break;
      case "answer":
        answerCallback?.(msg.payload as RTCSessionDescriptionInit);
        break;
      case "ice-candidate":
        iceCallback?.(msg.payload as RTCIceCandidateInit);
        break;
    }
  };

  return {
    async createSession(): Promise<string> {
      // Local transport doesn't need server-side session creation
      return sessionId;
    },

    async joinSession(): Promise<void> {
      // No-op for local transport
    },

    async sendOffer(_sessionId: string, offer: RTCSessionDescriptionInit): Promise<void> {
      const msg: SignalingMessage = {
        type: "offer",
        sessionId,
        payload: offer,
      };
      channel.postMessage(msg);
    },

    async sendAnswer(_sessionId: string, answer: RTCSessionDescriptionInit): Promise<void> {
      const msg: SignalingMessage = {
        type: "answer",
        sessionId,
        payload: answer,
      };
      channel.postMessage(msg);
    },

    async sendIceCandidate(_sessionId: string, candidate: RTCIceCandidateInit): Promise<void> {
      const msg: SignalingMessage = {
        type: "ice-candidate",
        sessionId,
        payload: candidate,
      };
      channel.postMessage(msg);
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
      channel.close();
      offerCallback = null;
      answerCallback = null;
      iceCallback = null;
    },
  };
}