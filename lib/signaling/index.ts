// OFA Signaling Module
//
// Handles ephemeral session creation, joining, and SDP/ICE exchange.
// The signaling layer carries NO file contents and NO encryption keys.
// All signaling is temporary and discarded after peer connection.

export interface SignalingTransport {
  createSession(): Promise<string>;
  joinSession(): Promise<void>;
  sendOffer(sessionId: string, offer: RTCSessionDescriptionInit): Promise<void>;
  sendAnswer(sessionId: string, answer: RTCSessionDescriptionInit): Promise<void>;
  sendIceCandidate(sessionId: string, candidate: RTCIceCandidateInit): Promise<void>;
  onOffer(callback: (offer: RTCSessionDescriptionInit) => void): void;
  onAnswer(callback: (answer: RTCSessionDescriptionInit) => void): void;
  onIceCandidate(callback: (candidate: RTCIceCandidateInit) => void): void;
  close(): void;
}

// Local (same-browser) signaling via BroadcastChannel
export { createLocalSignalingTransport } from "./local-transport";

// Server (cross-device) signaling via API polling
export { createServerSignalingTransport } from "./server-transport";