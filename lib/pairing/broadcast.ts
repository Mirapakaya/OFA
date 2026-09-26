// OFA Same-Device Pairing
//
// Uses BroadcastChannel API for zero-server pairing
// when sender and receiver are in the same browser.
// This enables instant file sharing between tabs on
// the same device without any network round-trip.

const CHANNEL_NAME = "ofa-pairing";

export interface LocalPairingMessage {
  type: "sender-ready" | "receiver-ready" | "pair";
  sessionId: string;
  timestamp: number;
}

export interface LocalPairingHandle {
  destroy: () => void;
}

/**
 * Create a sender-side local pairing listener.
 * Broadcasts that the sender is ready and listens
 * for a receiver on the same device.
 */
export function createSenderPairing(
  sessionId: string,
  onReceiverFound: (channel: BroadcastChannel, receiverSessionId: string) => void,
): LocalPairingHandle {
  const channel = new BroadcastChannel(CHANNEL_NAME);

  // Announce sender is ready
  const announce = () => {
    channel.postMessage({
      type: "sender-ready",
      sessionId,
      timestamp: Date.now(),
    } satisfies LocalPairingMessage);
  };

  // Announce immediately and periodically
  announce();
  const interval = setInterval(announce, 2000);

  // Listen for receiver
  channel.onmessage = (event: MessageEvent<LocalPairingMessage>) => {
    const msg = event.data;
    if (msg.type === "receiver-ready") {
      // Confirm pairing
      channel.postMessage({
        type: "pair",
        sessionId,
        timestamp: Date.now(),
      } satisfies LocalPairingMessage);
      onReceiverFound(channel, msg.sessionId);
      clearInterval(interval);
    }
  };

  return {
    destroy() {
      clearInterval(interval);
      channel.close();
    },
  };
}

/**
 * Create a receiver-side local pairing listener.
 * Listens for a sender on the same device and
 * responds when one is found.
 */
export function createReceiverPairing(
  onSenderFound: (channel: BroadcastChannel, senderSessionId: string) => void,
): LocalPairingHandle {
  const channel = new BroadcastChannel(CHANNEL_NAME);
  const receiverSessionId = crypto.randomUUID();

  // Listen for sender announcements
  channel.onmessage = (event: MessageEvent<LocalPairingMessage>) => {
    const msg = event.data;
    if (msg.type === "sender-ready") {
      // Respond that receiver is ready
      channel.postMessage({
        type: "receiver-ready",
        sessionId: receiverSessionId,
        timestamp: Date.now(),
      } satisfies LocalPairingMessage);
      onSenderFound(channel, msg.sessionId);
    } else if (msg.type === "pair" && msg.sessionId) {
      // Pairing confirmed by sender
      onSenderFound(channel, msg.sessionId);
    }
  };

  return {
    destroy() {
      channel.close();
    },
  };
}