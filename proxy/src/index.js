'use strict';

const dgram = require('node:dgram');
const dns = require('node:dns').promises;

const listenHost = process.env.LISTEN_HOST || '0.0.0.0';
const listenPort = Number(process.env.LISTEN_PORT || 19132);
const upstreamHost = process.env.UPSTREAM_HOST;
const upstreamPort = Number(process.env.UPSTREAM_PORT || 19132);
const allowClientIp = process.env.ALLOW_CLIENT_IP || '';
const MAX_PACKET_BYTES = 1500;
const CLIENT_IDLE_MS = 120_000;

if (!upstreamHost) {
  console.error('Missing UPSTREAM_HOST. Copy .env.example to .env and configure it.');
  process.exit(1);
}
for (const [name, value] of Object.entries({ listenPort, upstreamPort })) {
  if (!Number.isInteger(value) || value < 1 || value > 65535) {
    throw new Error(`${name} must be a valid UDP port`);
  }
}

const server = dgram.createSocket('udp4');
const clients = new Map();
let upstreamAddress;

function clientKey(rinfo) {
  return `${rinfo.address}:${rinfo.port}`;
}

function closeClient(key) {
  const entry = clients.get(key);
  if (!entry) return;
  clearTimeout(entry.timer);
  try { entry.upstream.close(); } catch {}
  clients.delete(key);
}

function touchClient(key, entry) {
  clearTimeout(entry.timer);
  entry.timer = setTimeout(() => closeClient(key), CLIENT_IDLE_MS);
  entry.timer.unref?.();
}

server.on('message', (message, rinfo) => {
  if (allowClientIp && rinfo.address !== allowClientIp) return;
  if (message.length > MAX_PACKET_BYTES) return;

  const key = clientKey(rinfo);
  let entry = clients.get(key);

  if (!entry) {
    const upstream = dgram.createSocket('udp4');
    entry = { upstream, address: rinfo.address, port: rinfo.port, timer: null };
    clients.set(key, entry);

    upstream.on('message', (response) => {
      if (response.length <= MAX_PACKET_BYTES) {
        server.send(response, entry.port, entry.address);
      }
    });
    upstream.on('error', (err) => {
      console.error(`Upstream socket error for ${key}:`, err.message);
      closeClient(key);
    });
  }

  touchClient(key, entry);
  entry.upstream.send(message, upstreamPort, upstreamAddress, (err) => {
    if (err) console.error(`Forward to upstream failed for ${key}:`, err.message);
  });
});

server.on('error', (err) => {
  console.error('Proxy socket error:', err);
  process.exitCode = 1;
  server.close();
});

(async () => {
  const resolved = await dns.lookup(upstreamHost, { family: 4 });
  upstreamAddress = resolved.address;
  server.bind(listenPort, listenHost, () => {
    console.log(`RZ UDP relay listening on ${listenHost}:${listenPort}`);
    console.log(`Forwarding to ${upstreamHost} (${upstreamAddress}):${upstreamPort}`);
    console.log('This is a UDP relay, not yet a packet-modifying Bedrock protocol client.');
  });
})().catch((err) => {
  console.error('Could not resolve UPSTREAM_HOST:', err.message);
  process.exit(1);
});

process.on('SIGINT', () => {
  for (const key of clients.keys()) closeClient(key);
  server.close(() => process.exit(0));
});
