// OFA Pairing Module
//
// Manages session creation, QR/link generation, and same-device pairing.

// Session management
export {
  createSendSession,
  parsePairingUrl,
  isSessionExpired,
  getSessionRemainingTime,
  generateSessionId,
  generateSecret,
  SESSION_TTL_MS,
} from "./session";
export type { PairingSession } from "./session";

// Same-device pairing via BroadcastChannel
export {
  createSenderPairing,
  createReceiverPairing,
} from "./broadcast";
export type { LocalPairingMessage, LocalPairingHandle } from "./broadcast";