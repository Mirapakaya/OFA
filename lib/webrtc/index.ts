// OFA WebRTC Module
//
// Manages RTCPeerConnection and DataChannel for direct file transfer.
// Includes backpressure, connection timeout, and cleanup.

// Connection management
export {
  createPeerConnection,
  createOfferAndSignal,
  acceptOfferAndSignal,
  closePeerConnection,
  monitorConnectionTimeout,
  getConnectionState,
} from "./connection";
export type { ConnectionConfig, ConnectionState, ConnectionStateCallback } from "./connection";

// DataChannel management
export {
  createSendChannel,
  handleIncomingChannel,
  sendDataWithBackpressure,
  closeChannel,
  isChannelReady,
} from "./data-channel";
export type { DataChannelEvents } from "./data-channel";