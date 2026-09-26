# OFA

Private file sharing. Nothing more.

OFA is a web tool for direct, encrypted file sharing between people. Select files, connect to another browser, transfer them directly, and leave nothing behind.

## Principles

- No account
- No database
- No cookies
- No analytics
- No tracking
- No permanent file storage
- Files encrypted in browser before transfer
- Temporary signaling only
- Session destruction after completion

## Stack

- Next.js · React · TypeScript
- Web Crypto API · WebRTC
- Vercel deployment

## Development

```bash
npm install
npm run dev
```

## Architecture

See [architecture.md](./architecture.md) for the system overview and [rules.md](./rules.md) for engineering rules.

## Phases

See [phases.md](./phases.md) for implementation progress.