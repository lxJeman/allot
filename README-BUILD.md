# Building Standalone APK for Allot

This guide explains how to build a standalone APK that doesn't require the Metro dev server.

## Quick Start

### Option 1: Local Backend (Same Network)

If your phone and computer are on the same WiFi:

```bash
npm run build:local
```

This builds an APK configured to use `http://192.168.100.55:3000`

### Option 2: Remote Backend (Using ngrok)

For testing on different networks or sharing with others:

1. **Start the Rust backend:**
```bash
cd rust-backend
cargo run
```

2. **In a new terminal, start ngrok:**
```bash
npm run ngrok
```

3. **Copy the ngrok URL** (looks like `https://abc123.ngrok-free.app`)

4. **In a third terminal, build with ngrok URL:**
```bash
./build-standalone.sh https://abc123.ngrok-free.app
```

## What Gets Built

- **Standalone APK**: No Metro server required
- **Release build**: Optimized and minified
- **Configured backend**: URL is baked into the APK
- **Location**: `android/app/build/outputs/apk/release/app-release.apk`

## Installation

The build script will offer to install automatically if a device is connected.

Manual installation:
```bash
adb install -r android/app/build/outputs/apk/release/app-release.apk
```

## Rebuilding with Different Backend

Just run the build script again with a new URL:

```bash
./build-standalone.sh https://new-url.ngrok-free.app
```

## Troubleshooting

### Build fails with "SDK not found"
Make sure Android SDK is installed and `ANDROID_HOME` is set:
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### APK crashes on startup
Check that the backend URL is accessible from your phone:
- For local URLs: Phone must be on same WiFi
- For ngrok URLs: Check ngrok is still running

### "App not installed" error
Uninstall the old version first:
```bash
adb uninstall com.allot.app
```

## Development vs Standalone

| Feature | Development (Metro) | Standalone APK |
|---------|-------------------|----------------|
| Hot reload | ✅ Yes | ❌ No |
| Metro required | ✅ Yes | ❌ No |
| Build time | Fast | Slower |
| File size | Smaller | Larger |
| Use case | Active development | Testing/Demo |

## Backend Configuration

The app checks for backend URL in this order:
1. Environment variable `BACKEND_URL`
2. `app.json` extra config
3. Default: `http://192.168.100.55:3000`

## Ngrok Tips

- Free tier gives you a random URL each time
- Paid tier allows custom domains
- Tunnel stays open until you press Ctrl+C
- Backend must be running before starting ngrok

## Next Steps

After building:
1. Install APK on device
2. Grant all required permissions (accessibility, overlay, etc.)
3. Start monitoring in target apps (TikTok, etc.)
4. Backend will receive classification requests via ngrok
