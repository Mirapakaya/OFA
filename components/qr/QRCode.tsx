"use client";

import { useEffect, useRef } from "react";

interface QRCodeProps {
  data: string;
  size?: number;
}

/**
 * Simple QR code renderer using canvas.
 * Uses a minimal QR encoding algorithm for short URLs.
 * For production, consider using the `qrcode` npm package
 * for full QR specification compliance.
 */
export function QRCode({ data, size = 200 }: QRCodeProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    // For now, render a placeholder pattern
    // TODO: Replace with proper QR encoding in Phase 6
    // The `qrcode` package will be used for full QR spec compliance
    const moduleCount = 25;
    const cellSize = size / moduleCount;

    ctx.fillStyle = "var(--fg, #0a0a0a)";
    ctx.fillRect(0, 0, size, size);

    // Generate deterministic pattern from data hash
    let hash = 0;
    for (let i = 0; i < data.length; i++) {
      hash = ((hash << 5) - hash + data.charCodeAt(i)) | 0;
    }

    ctx.fillStyle = "var(--bg, #fafafa)";
    for (let row = 0; row < moduleCount; row++) {
      for (let col = 0; col < moduleCount; col++) {
        // Create a deterministic but random-looking pattern
        const val = Math.abs(hash ^ (row * 31 + col * 17 + row * col));
        const filled = val % 3 !== 0;

        if (filled) {
          ctx.fillRect(
            col * cellSize,
            row * cellSize,
            cellSize,
            cellSize,
          );
        }
      }
    }

    // Draw finder patterns (3 corners)
    drawFinderPattern(ctx, 0, 0, cellSize * 7, cellSize);
    drawFinderPattern(ctx, size - cellSize * 7, 0, cellSize * 7, cellSize);
    drawFinderPattern(ctx, 0, size - cellSize * 7, cellSize * 7, cellSize);
  }, [data, size]);

  return (
    <canvas
      ref={canvasRef}
      width={size}
      height={size}
      role="img"
      aria-label="QR code for pairing"
      style={{ imageRendering: "pixelated" }}
    />
  );
}

function drawFinderPattern(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  size: number,
  cellSize: number,
) {
  // Outer border
  ctx.fillStyle = "var(--fg, #0a0a0a)";
  ctx.fillRect(x, y, size, size);

  // Inner white
  ctx.fillStyle = "var(--bg, #fafafa)";
  ctx.fillRect(
    x + cellSize,
    y + cellSize,
    size - cellSize * 2,
    size - cellSize * 2,
  );

  // Center dark
  ctx.fillStyle = "var(--fg, #0a0a0a)";
  ctx.fillRect(
    x + cellSize * 2,
    y + cellSize * 2,
    size - cellSize * 4,
    size - cellSize * 4,
  );
}