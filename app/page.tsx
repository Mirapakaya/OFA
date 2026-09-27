import Link from "next/link";

export default function HomePage() {
  return (
    <div className="home">
      <div className="home__content">
        {/* Hero */}
        <section className="hero">
          <h1 className="hero-title">Private file sharing.</h1>
          <p className="hero-subtitle">Nothing more.</p>
          <p className="hero-description">
            Select files. Connect directly. Transfer. No accounts, no storage, no tracking.
          </p>
          <div className="hero-actions">
            <Link href="/send" className="button primary">
              Send files
            </Link>
            <Link href="/receive" className="button secondary">
              Receive files
            </Link>
          </div>
        </section>

        {/* Feature Cards */}
        <section className="features">
          <div className="features__grid">
            <div className="feature-card">
              <div className="feature-card__icon">&#x1f512;</div>
              <h3 className="feature-card__title">End-to-end encrypted</h3>
              <p className="feature-card__desc">
                AES-256-GCM encryption with ephemeral keys. No one &mdash; not even OFA &mdash; can read your files.
              </p>
            </div>
            <div className="feature-card">
              <div className="feature-card__icon">&#x26a1;</div>
              <h3 className="feature-card__title">Direct transfer</h3>
              <p className="feature-card__desc">
                Files go device-to-device over WebRTC. They never pass through OFA servers.
              </p>
            </div>
            <div className="feature-card">
              <div className="feature-card__icon">&#x1f6e1;&#xfe0f;</div>
              <h3 className="feature-card__title">No account needed</h3>
              <p className="feature-card__desc">
                No sign-ups, no cookies, no analytics. Open a link and transfer. That&#x2019;s it.
              </p>
            </div>
          </div>
        </section>

        {/* Alternating Feature Sections */}
        <section className="feature-section">
          <div className="feature-section__text">
            <p className="feature-section__tag">Encryption</p>
            <h2 className="feature-section__heading">Your files, your keys</h2>
            <p className="feature-section__body">
              Each transfer generates fresh ECDH key pairs that are destroyed after the session ends.
              The encryption secret lives only in the pairing link &mdash; and browsers never send URL fragments to servers.
            </p>
          </div>
          <div className="feature-section__media">&#x1f510;</div>
        </section>

        <section className="feature-section feature-section--reverse">
          <div className="feature-section__text">
            <p className="feature-section__tag">Verification</p>
            <h2 className="feature-section__heading">Confirm it&#x2019;s really them</h2>
            <p className="feature-section__body">
              After key agreement, both devices show the same verification phrase.
              If the phrases match, the connection is secure. If they don&#x2019;t, abort.
            </p>
          </div>
          <div className="feature-section__media">&#x2705;</div>
        </section>

        {/* How it works */}
        <section className="how-it-works">
          <h2 className="section-title">How it works</h2>
          <ol className="steps">
            <li className="step">
              <span className="step-number">1</span>
              <span>Choose your files on the sending device.</span>
            </li>
            <li className="step">
              <span className="step-number">2</span>
              <span>Open the secure link on the receiving device.</span>
            </li>
            <li className="step">
              <span className="step-number">3</span>
              <span>Both devices connect directly and transfer.</span>
            </li>
            <li className="step">
              <span className="step-number">4</span>
              <span>Done. The session expires. Nothing is stored.</span>
            </li>
          </ol>
        </section>

        {/* FAQ */}
        <section className="faq-section">
          <h2>Questions</h2>
          <div className="faq-list">
            <details>
              <summary>Where are my files stored?</summary>
              <p>
                Nowhere permanently. Files are encrypted in the sender&#x2019;s browser and sent
                directly to the receiver. After the transfer completes, no copy exists anywhere.
              </p>
            </details>
            <details>
              <summary>Can OFA read my files?</summary>
              <p>
                No. The encryption key is derived from a secret that only the two devices know.
                OFA&#x2019;s signaling server only helps devices find each other &mdash; it never
                sees file content.
              </p>
            </details>
            <details>
              <summary>What happens if the connection drops?</summary>
              <p>
                The transfer session ends. You&#x2019;ll need to start a new transfer. Partially
                received files are not saved &mdash; this is by design.
              </p>
            </details>
            <details>
              <summary>Does OFA work on mobile?</summary>
              <p>
                Yes. OFA works in any modern browser that supports WebRTC, including mobile
                Chrome, Firefox, and Safari.
              </p>
            </details>
            <details>
              <summary>What&#x2019;s the maximum file size?</summary>
              <p>
                There&#x2019;s no hard limit. Very large files work but may be slow on poor
                connections. The transfer happens in chunks, so memory usage stays reasonable.
              </p>
            </details>
          </div>
        </section>

        {/* CTA */}
        <section className="cta-section">
          <h2 className="cta-section__heading">Ready to send?</h2>
          <p className="cta-section__body">
            No account. No storage. No tracking. Just secure, private file sharing.
          </p>
          <div className="hero-actions">
            <Link href="/send" className="button primary">
              Send files
            </Link>
            <Link href="/receive" className="button secondary">
              Receive files
            </Link>
          </div>
        </section>
      </div>
    </div>
  );
}
