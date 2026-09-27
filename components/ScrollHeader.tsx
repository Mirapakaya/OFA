"use client";

import { useEffect } from "react";

export function ScrollHeader() {
  useEffect(() => {
    const header = document.querySelector(".header");
    if (!header) return;

    const onScroll = () => {
      header.classList.toggle("scrolled", window.scrollY > 20);
    };

    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return null;
}
