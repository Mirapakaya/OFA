import Link from "next/link";

export default function HomePage() {
  return (
    <div className="home">
      <section className="hero">
        <h1 className="hero-title">Private file sharing.</h1>
        <p className="hero-subtitle">Nothing more.</p>
        <p className="hero-description">
          Select files. Connect directly. Transfer.
        </p>
        <div className="hero-actions">
          <Link href="/send" className="button button-primary">
            Send files
          </Link>
          <Link href="/receive" className="button button-secondary">
            Receive files
          </Link>
        </div>
      </section>

      <section className="features">
        <div className="feature-list">
          <div className="feature-item">
            <span className="feature-label">No account</span>
          </div>
          <div className="feature-item">
            <span className="feature-label">End-to-end encrypted</span>
          </div>
          <div className="feature-item">
            <span className="feature-label">No permanent file storage</span>
          </div>
        </div>
      </section>

      <section className="how-it-works">
        <h2 className="section-title">How it works</h2>
        <ol className="steps">
          <li className="step">
            <span className="step-number">1</span>
            <span className="step-text">Choose your files.</span>
          </li>
          <li className="step">
            <span className="step-number">2</span>
            <span className="step-text">Connect another browser.</span>
          </li>
          <li className="step">
            <span className="step-number">3</span>
            <span className="step-text">Transfer directly.</span>
          </li>
        </ol>
      </section>

      <nav className="home-nav">
        <Link href="/privacy">Privacy</Link>
        <Link href="/security">Security</Link>
        <Link href="/about">About</Link>
      </nav>
    </div>
  );
}