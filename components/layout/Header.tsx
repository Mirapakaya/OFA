import Link from "next/link";

export function Header() {
  return (
    <header className="header">
      <div className="header__inner">
        <Link href="/" className="header__logo">OFA</Link>
        <nav className="header__nav">
          <Link href="/send">Send</Link>
          <Link href="/receive">Receive</Link>
          <Link href="/about">About</Link>
        </nav>
      </div>
    </header>
  );
}