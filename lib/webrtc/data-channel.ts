// OFA DataChannel Manager
//
// Manages RTCDataChannel for file transfer.
// Implements backpressure using bufferedAmount monitoring.
// Never blindly sends huge buffers — waits when the buffer is full.

/** Threshold at which we pause sending (16 MB) */
const BUFFER_HIGH_WATERMARK = 16 * 1024 * 1024;

/** Threshold at which we resume sending (4 MB) */
const BUFFER_LOW_WATERMARK = 4 * 1024 * 1024;

export interface DataChannelEvents {
  onOpen: () => void;
  onClose: () => void;
  onMessage: (data: ArrayBuffer) => void;
  onError: (error: Error) => void;
}

/**
 * Create a DataChannel on the peer connection (sender side).
 * Configures binary type and event handlers.
 */
export function createSendChannel(
  pc: RTCPeerConnection,
  label: string,
  events: DataChannelEvents,
): RTCDataChannel {
  const channel = pc.createDataChannel(label, {
    ordered: true, // Guarantee chunk ordering
  });

  channel.binaryType = "arraybuffer";
  setupChannelEvents(channel, events);

  return channel;
}

/**
 * Handle incoming DataChannel (receiver side).
 * Called when the peer connection fires the ondatachannel event.
 */
export function handleIncomingChannel(
  pc: RTCPeerConnection,
  events: DataChannelEvents,
): void {
  pc.ondatachannel = (event) => {
    const channel = event.channel;
    channel.binaryType = "arraybuffer";
    setupChannelEvents(channel, events);
  };
}

function setupChannelEvents(
  channel: RTCDataChannel,
  events: DataChannelEvents,
): void {
  channel.onopen = () => {
    // Set buffer threshold for backpressure notification
    channel.bufferedAmountLowThreshold = BUFFER_LOW_WATERMARK;
    events.onOpen();
  };

  channel.onclose = () => {
    events.onClose();
  };

  channel.onmessage = (event) => {
    if (event.data instanceof ArrayBuffer) {
      events.onMessage(event.data);
    }
    // Ignore non-binary messages
  };

  channel.onerror = (event) => {
    const errorEvent = event as RTCErrorEvent;
    events.onError(new Error(errorEvent.error?.message ?? "DataChannel error"));
  };
}

/**
 * Send data over the DataChannel with backpressure awareness.
 * If the buffer is full, waits for it to drain before sending.
 * This prevents memory bloat from queuing too much data.
 */
export async function sendDataWithBackpressure(
  channel: RTCDataChannel,
  data: ArrayBuffer,
): Promise<void> {
  // Wait if buffer is above high watermark
  while (channel.bufferedAmount >= BUFFER_HIGH_WATERMARK) {
    await waitForBufferDrain(channel);
  }

  channel.send(data);
}

/**
 * Wait for the DataChannel buffer to drain below the low watermark.
 * Uses the bufferedamountlow event for efficient notification.
 */
function waitForBufferDrain(channel: RTCDataChannel): Promise<void> {
  return new Promise((resolve) => {
    if (channel.bufferedAmount < BUFFER_HIGH_WATERMARK) {
      resolve();
      return;
    }

    const handler = () => {
      if (channel.bufferedAmount < BUFFER_LOW_WATERMARK) {
        channel.removeEventListener("bufferedamountlow", handler);
        resolve();
      }
    };

    channel.addEventListener("bufferedamountlow", handler);
  });
}

/**
 * Fully close and clean up a DataChannel.
 */
export function closeChannel(channel: RTCDataChannel): void {
  channel.onopen = null;
  channel.onclose = null;
  channel.onmessage = null;
  channel.onerror = null;

  try {
    channel.close();
  } catch {
    // May already be closed
  }
}

/**
 * Check if a DataChannel is ready for sending.
 */
export function isChannelReady(channel: RTCDataChannel): boolean {
  return channel.readyState === "open";
}