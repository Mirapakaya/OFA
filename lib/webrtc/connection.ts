// OFA WebRTC Connection Manager
//
// Creates and manages RTCPeerConnection with signaling integration.
// Handles offer/answer exchange, ICE candidate gathering,
// connection timeout, disconnect, and full cleanup.

import type { SignalingTransport } from "@/lib/signaling";

export interface ConnectionConfig {
  iceServers?: RTCIceServer[];
  connectionTimeoutMs?: number;
}

const DEFAULT_ICE_SERVERS: RTCIceServer[] = [
  { urls: "stun:stun.l.google.com:19302" },
  { urls: "stun:stun1.l.google.com:19302" },
];

const DEFAULT_TIMEOUT_MS = 30_000; // 30 seconds

export interface ConnectionState {
  status: "new" | "connecting" | "connected" | "disconnected" | "failed" | "closed";
  iceConnectionState: RTCIceConnectionState;
  signalingState: RTCSignalingState;
}

export type ConnectionStateCallback = (state: ConnectionState) => void;

/**
 * Create an RTCPeerConnection with event handling.
 * Returns the connection and a cleanup function.
 */
export function createPeerConnection(
  onStateChange: ConnectionStateCallback,
  config: ConnectionConfig = {},
): RTCPeerConnection {
  const iceServers = config.iceServers ?? DEFAULT_ICE_SERVERS;
  const pc = new RTCPeerConnection({ iceServers });

  pc.oniceconnectionstatechange = () => {
    onStateChange(getConnectionState(pc));
  };

  pc.onsignalingstatechange = () => {
    onStateChange(getConnectionState(pc));
  };

  pc.onicecandidate = () => {
    // ICE candidates are handled separately via the signaling transport
    // This event fires automatically during ICE gathering
  };

  pc.onconnectionstatechange = () => {
    onStateChange(getConnectionState(pc));
  };

  return pc;
}

/**
 * Act as the sender: create offer, set local description,
 * send via signaling, and wait for answer.
 */
export async function createOfferAndSignal(
  pc: RTCPeerConnection,
  signaling: SignalingTransport,
  sessionId: string,
): Promise<void> {
  // Create DataChannel before offer (required for sender)
  // The channel is created by the caller before this function

  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);

  // Wait for ICE gathering to complete, then send the full offer
  // (including ICE candidates) via signaling
  await waitForIceGathering(pc);

  const fullOffer = pc.localDescription!;
  await signaling.sendOffer(sessionId, fullOffer);

  // Set up ICE candidate forwarding (for any late candidates)
  pc.onicecandidate = (event) => {
    if (event.candidate) {
      signaling.sendIceCandidate(sessionId, event.candidate.toJSON());
    }
  };

  // Listen for answer from receiver
  signaling.onAnswer(async (answer) => {
    await pc.setRemoteDescription(new RTCSessionDescription(answer));
  });

  // Listen for ICE candidates from receiver
  signaling.onIceCandidate(async (candidate) => {
    await pc.addIceCandidate(new RTCIceCandidate(candidate));
  });
}

/**
 * Act as the receiver: wait for offer, create answer,
 * set local description, send via signaling.
 */
export async function acceptOfferAndSignal(
  pc: RTCPeerConnection,
  signaling: SignalingTransport,
  sessionId: string,
): Promise<void> {
  // Listen for offer from sender
  signaling.onOffer(async (offer) => {
    await pc.setRemoteDescription(new RTCSessionDescription(offer));

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);

    // Wait for ICE gathering, then send full answer
    await waitForIceGathering(pc);

    const fullAnswer = pc.localDescription!;
    await signaling.sendAnswer(sessionId, fullAnswer);

    // Forward late ICE candidates
    pc.onicecandidate = (event) => {
      if (event.candidate) {
        signaling.sendIceCandidate(sessionId, event.candidate.toJSON());
      }
    };
  });

  // Listen for ICE candidates from sender
  signaling.onIceCandidate(async (candidate) => {
    await pc.addIceCandidate(new RTCIceCandidate(candidate));
  });
}

/**
 * Wait for ICE gathering to complete.
 * Falls back to a timeout if gathering takes too long.
 */
async function waitForIceGathering(
  pc: RTCPeerConnection,
  timeoutMs: number = 5000,
): Promise<void> {
  if (pc.iceGatheringState === "complete") return;

  return new Promise((resolve) => {
    const timer = setTimeout(resolve, timeoutMs);

    pc.onicegatheringstatechange = () => {
      if (pc.iceGatheringState === "complete") {
        clearTimeout(timer);
        resolve();
      }
    };
  });
}

/**
 * Get current connection state from an RTCPeerConnection.
 */
export function getConnectionState(pc: RTCPeerConnection): ConnectionState {
  return {
    status: pc.connectionState ?? "new",
    iceConnectionState: pc.iceConnectionState,
    signalingState: pc.signalingState,
  };
}

/**
 * Fully close and clean up an RTCPeerConnection.
 * Closes all data channels, removes event listeners,
 * and closes the peer connection.
 */
export function closePeerConnection(pc: RTCPeerConnection): void {
  // Close all data channels
  const channels = pc.getTransceivers?.() ?? [];
  for (const transceiver of channels) {
    try {
      transceiver.stop();
    } catch {
      // Ignore — channel may already be closed
    }
  }

  // Remove all event handlers
  pc.onicecandidate = null;
  pc.oniceconnectionstatechange = null;
  pc.onsignalingstatechange = null;
  pc.onconnectionstatechange = null;
  pc.ondatachannel = null;
  pc.onnegotiationneeded = null;
  pc.onicegatheringstatechange = null;

  // Close the connection
  try {
    pc.close();
  } catch {
    // Ignore — may already be closed
  }
}

/**
 * Monitor connection with timeout.
 * Returns a cleanup function that clears the timeout.
 */
export function monitorConnectionTimeout(
  pc: RTCPeerConnection,
  timeoutMs: number = DEFAULT_TIMEOUT_MS,
  onTimeout: () => void,
): () => void {
  const timer = setTimeout(() => {
    if (
      pc.connectionState !== "connected"
    ) {
      onTimeout();
    }
  }, timeoutMs);

  return () => clearTimeout(timer);
}