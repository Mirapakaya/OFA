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
            <Link href="/" className="header__logo">
              OFA
            </Link>
            <ul className="header__nav" role="list">
              <li>
                <Link href="/send">Send</Link>
              </li>
              <li>
                <Link href="/receive">Receive</Link>
              </li>
              <li>
                <Link href="/about">About</Link>
              </li>
            </ul>
          </nav>
        </header>
        <main id="main" className="main">
          {children}
        </main>
        <footer className="footer">
          <nav aria-label="Footer navigation">
            <Link href="/privacy">Privacy</Link>
            <Link href="/security">Security</Link>
          </nav>
          <p className="footer__note">
            No accounts. No storage. No tracking.
          </p>
        </footer>
      </body>
    </html>
  );
}
