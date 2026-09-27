import type { Metadata, Viewport } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: {
    default: "OFA — Encrypted File Sharing",
    template: "%s — OFA",
  },
  description:
    "Send files directly between devices. End-to-end encrypted, no accounts, no storage, no tracking.",
  metadataBase: new URL("https://ofa-two.vercel.app"),
  openGraph: {
    title: "OFA — Encrypted File Sharing",
    description:
      "Send files directly between devices. End-to-end encrypted, no accounts, no storage, no tracking.",
    url: "https://ofa-two.vercel.app",
    siteName: "OFA",
    type: "website",
  },
  robots: {
    index: true,
    follow: true,
  },
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  themeColor: "#0d1c64",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>
        <a href="#main" className="skip-link">
          Skip to content
        </a>
        <header className="header" role="banner">
          <div className="header__inner">
            <Link href="/" className="header__logo" aria-label="OFA home">
              OFA
            </Link>
            <nav aria-label="Main navigation" className="header__nav">
              <div className="header__nav-links" role="list">
                <Link href="/send">Send</Link>
                <Link href="/receive">Receive</Link>
                <Link href="/about">About</Link>
              </div>
              <div className="header__cta">
                <Link href="/send" className="button primary">
                  Start transfer
                </Link>
              </div>
            </nav>
          </div>
        </header>
        <main id="main" className="main" role="main">
          {children}
        </main>
        <footer className="footer" role="contentinfo">
          <div className="footer-content">
            <nav className="columns" aria-label="Footer">
              <div className="column">
                <div className="column-title">Resources</div>
                <Link href="/privacy">Privacy</Link>
                <Link href="/security">Security</Link>
              </div>
              <div className="column">
                <div className="column-title">Community</div>
                <a
                  href="https://github.com/altercpre/OFA"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  GitHub
                </a>
              </div>
              <div className="column">
                <div className="column-title">Legal</div>
                <Link href="/privacy">Terms</Link>
              </div>
            </nav>
            <div className="credit">
              made by <a href="https://github.com/altercpre" target="_blank" rel="noopener noreferrer">altercpre</a>
            </div>
          </div>
        </footer>
      </body>
    </html>
  );
}
