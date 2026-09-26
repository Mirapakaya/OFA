import Link from "next/link";

export function Footer() {
  return (
    <footer className="footer">
      <div className="footer__inner">
        <Link href="/privacy">Privacy</Link>
        <Link href="/security">Security</Link>
      </div>
    </footer>
  );
}