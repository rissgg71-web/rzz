# RZ Client BE — UDP proxy starter

Node.js UDP relay for a **fixed upstream** Minecraft Bedrock server. Bedrock uses RakNet over UDP, so this relays UDP datagrams while keeping a stable upstream socket per client.

## Important limits

- This is a transport relay only. It does **not** decode or modify RakNet/Bedrock packets.
- It does not add HUD, FPS boosts, gameplay modules, or bypass server rules.
- Compatibility with Bedrock **1.26.60+ is not yet verified**. Test against the exact game/server version and update protocol-aware code when Mojang changes it.
- For public use, deploy behind firewall/rate limits. Set `ALLOW_CLIENT_IP` if you want to restrict who can connect.

## Run

Requires Node.js 20+.

```bash
cd proxy
cp .env.example .env
# Edit UPSTREAM_HOST and UPSTREAM_PORT in .env
set -a
. ./.env
set +a
npm start
```

Open the chosen UDP listen port in your VPS firewall. In Minecraft, add the proxy IP/domain and `LISTEN_PORT` as the server address/port.

The upstream server sees the proxy's address, not the player's original public address. Some server features or hosting protections may behave differently.
