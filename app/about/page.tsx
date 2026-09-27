import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "About",
};

export default function AboutPage() {
  return (
    <div className="content-page">
      <h1>About OFA</h1>

      <p>
        OFA is a web application for sending files directly between devices.
        Files are encrypted in the sender&apos;s browser and decrypted in the
        receiver&apos;s browser. No server ever sees the file contents.
      </p>

      <section>
        <h2>How it works</h2>
        <ol>
          <li>You select files on the sending device.</li>
          <li>OFA generates a secure link.</li>
          <li>You open the link on the receiving device.</li>
          <li>Both devices negotiate an encrypted peer connection.</li>
          <li>Files are transferred directly between the two browsers.</li>
          <li>After the transfer, the session is gone. Nothing is stored.</li>
        </ol>
      </section>

      <section>
        <h2>Design principles</h2>
        <ul>
          <li>No accounts &mdash; just open and use.</li>
          <li>No permanent storage &mdash; files exist only during the transfer.</li>
          <li>No tracking &mdash; no analytics, no cookies, no identifiers.</li>
          <li>End-to-end encryption &mdash; AES-256-GCM with ephemeral keys.</li>
          <li>Direct transfer &mdash; files go device to device, not through a server.</li>
        </ul>
      </section>

      <section>
        <h2>Limitations</h2>
        <ul>
          <li>Both devices must be online at the same time.</li>
          <li>The pairing link expires after 5 minutes.</li>
          <li>Very large files may be slow over poor network connections.</li>
          <li>Some corporate or institutional networks block WebRTC traffic.</li>
        </ul>
      </section>

      <section>
        <h2>Source code</h2>
        <p>
          OFA is open source. The code is available at{" "}
          <a
            href="https://github.com/altercpre/OFA"
            target="_blank"
            rel="noopener noreferrer"
          >
            github.com/altercpre/OFA
          </a>.
        </p>
      </section>
    </div>
  );
}
