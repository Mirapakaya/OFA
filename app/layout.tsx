import type { Metadata, Viewport } from "next";
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
  themeColor: "#0a0a0a",
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
        <header className="header">
          <nav aria-label="Main navigation">
            <a href="/" className="header__logo">
              OFA
            </a>
            <ul className="header__nav" role="list">
              <li>
                <a href="/send">Send</a>
              </li>
              <li>
                <a href="/receive">Receive</a>
              </li>
              <li>
                <a href="/about">About</a>
              </li>
            </ul>
          </nav>
        </header>
        <main id="main" className="main">
          {children}
        </main>
        <footer className="footer">
          <nav aria-label="Footer navigation">
            <a href="/privacy">Privacy</a>
            <a href="/security">Security</a>
          </nav>
          <p className="footer__note">
            No accounts. No storage. No tracking.
          </p>
        </footer>
      </body>
    </html>
  );
}