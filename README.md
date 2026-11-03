# RZ Client BE

Starter project for an Android companion APK and a separate Node.js Bedrock UDP relay.

## Project layout

- `app/` — minimal Android APK (Kotlin)
- `proxy/` — Node.js UDP relay with a fixed Bedrock upstream
- `.github/workflows/android.yml` — builds a debug APK on pushes to `main` or manually

## Build APK

Push this repository to GitHub. Open **Actions → Build Android APK**. When the workflow finishes, download the `RZ-Client-BE-debug` artifact and extract `app-debug.apk`.

## Current status

This is a clean starter scaffold, not a complete Lunar Client replacement. The APK is only a companion shell. The Node.js process runs separately on a Node-capable VPS/server. The proxy currently forwards UDP traffic and does not parse Bedrock protocol packets or inject client-side features.

Target game versions: Bedrock 1.26.60+, pending protocol compatibility testing.
