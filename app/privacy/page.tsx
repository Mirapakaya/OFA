import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Privacy",
};

export default function PrivacyPage() {
  return (
    <div className="content-page">
      <h1>Privacy</h1>

      <section>
        <h2>What OFA does not do</h2>
        <ul>
          <li>No accounts or sign-ups</li>
          <li>No cookies</li>
          <li>No analytics or tracking</li>
          <li>No advertising</li>
          <li>No permanent file storage</li>
          <li>No database</li>
        </ul>
      </section>

      <section>
        <h2>What happens to your files</h2>
        <p>
          Files are encrypted in your browser and sent directly to the
          receiving device. They do not pass through OFA servers. The encrypted
          data travels over a WebRTC peer connection between the two devices.
        </p>
        <p>
          No copy of your files is stored anywhere after the transfer completes.
        </p>
      </section>

      <section>
        <h2>What OFA stores temporarily</h2>
        <p>
          When you send files, OFA creates a short-lived session so the
          receiving device can find your device. This session contains only the
          information needed to establish a direct connection (SDP offer and
          ICE candidates). It is deleted automatically after 5 minutes.
        </p>
        <p>
          The cryptographic secret used for encryption is embedded in the
          pairing link after the # symbol. Browsers never send URL fragments
          to servers, so this secret never reaches OFA infrastructure.
        </p>
      </section>

      <section>
        <h2>Network requests</h2>
        <p>OFA makes network requests only to:</p>
        <ul>
          <li>The OFA signaling endpoint &mdash; to exchange connection information
            during pairing (deleted after 5 minutes)</li>
          <li>STUN servers &mdash; standard WebRTC infrastructure to help devices
            find each other&apos;s network address</li>
        </ul>
        <p>
          No other external requests are made. No third-party scripts are
          loaded.
        </p>
      </section>

      <section>
        <h2>Same-device transfers</h2>
        <p>
          When both devices are in the same browser (different tabs), OFA uses
          the BroadcastChannel API instead of the network. No server requests
          are made during same-device transfers.
        </p>
      </section>

      <section>
        <h2>Infrastructure</h2>
        <p>
          OFA runs on Vercel. Vercel may log HTTP requests at the
          infrastructure level as part of normal service operation. OFA does
          not control these infrastructure-level logs. We do not claim
          &ldquo;zero logs&rdquo; because we cannot guarantee what
          infrastructure providers log.
        </p>
      </section>
    </div>
  );
}
