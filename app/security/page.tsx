import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Security",
};

export default function SecurityPage() {
  return (
    <div className="content-page">
      <h1>Security</h1>

      <section>
        <h2>Encryption</h2>
        <p>
          Files are encrypted with AES-256-GCM before leaving your browser.
          Each chunk of each file is encrypted separately with a unique nonce
          and authenticated with an additional data field binding the chunk
          index and protocol version.
        </p>
        <p>
          If a single chunk&apos;s authentication tag fails verification, the
          entire transfer is rejected. This prevents undetected tampering.
        </p>
      </section>

      <section>
        <h2>Key agreement</h2>
        <p>
          The sender and receiver derive a shared encryption key using ECDH.
          X25519 is used when the browser supports it; P-256 is the fallback.
          The shared secret is fed through HKDF-SHA-256 to derive separate
          keys for encryption, metadata, control messages, and verification.
        </p>
        <p>
          Ephemeral key pairs are generated fresh for each transfer and
          destroyed after the session ends. Key material is not persisted.
        </p>
      </section>

      <section>
        <h2>Key separation</h2>
        <p>
          A single ECDH shared secret is not used directly. HKDF derives four
          independent keys with different domain labels:
        </p>
        <ul>
          <li><strong>Encryption key</strong> &mdash; encrypts file chunks</li>
          <li><strong>Metadata key</strong> &mdash; encrypts file metadata</li>
          <li><strong>Control key</strong> &mdash; encrypts protocol messages</li>
          <li><strong>Verification key</strong> &mdash; derives a short verification
            phrase</li>
        </ul>
        <p>
          Compromising one key does not expose data protected by the others.
        </p>
      </section>

      <section>
        <h2>Verification phrase</h2>
        <p>
          After key agreement, both devices derive a short verification phrase
          (for example, &ldquo;oak &mdash; 27 &mdash; stone&rdquo;). If both devices
          show the same phrase, the key exchange has not been tampered with.
        </p>
        <p>
          This is an optional step. If you trust the pairing channel (for
          example, you sent the link over an already-secure channel), you can
          skip verification. If you want to be certain, compare the phrases on
          both devices.
        </p>
      </section>

      <section>
        <h2>Nonce management</h2>
        <p>
          Each encrypted chunk uses a unique 12-byte nonce. The nonce is
          composed of a 4-byte random prefix and an 8-byte counter. The
          random prefix ensures that nonce collisions are infeasible even if
          the counter resets. The counter increments monotonically. If the
          counter approaches exhaustion, the transfer is aborted.
        </p>
      </section>

      <section>
        <h2>What OFA does not protect against</h2>
        <ul>
          <li>
            <strong>Compromised devices</strong> &mdash; If malware is running on
            your device, it can access files before encryption or after
            decryption.
          </li>
          <li>
            <strong>Active signaling compromise</strong> &mdash; If the signaling
            server is compromised, an attacker could facilitate a
            man-in-the-middle attack. The verification phrase is the defense:
            if the phrases do not match, abort the transfer.
          </li>
          <li>
            <strong>Network metadata</strong> &mdash; The fact that a transfer
            occurred between two IP addresses is visible to network observers.
            The content is encrypted.
          </li>
        </ul>
      </section>

      <section>
        <h2>Reporting a vulnerability</h2>
        <p>
          If you find a security issue, please report it responsibly. Open a
          GitHub issue on the Mirapakaya/OFA repository with details.
        </p>
      </section>
    </div>
  );
}
