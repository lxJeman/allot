# Quick Start: Standalone APK Build

## 🚀 Fast Track (3 Steps)

### Step 1: Start Backend
```bash
cd rust-backend
cargo run
```
Keep this running. You should see:
```
🌐 Server listening on http://0.0.0.0:3000
```

### Step 2: Start Ngrok (New Terminal)
```bash
npm run ngrok
```
Copy the HTTPS URL (e.g., `https://abc123.ngrok-free.app`)

### Step 3: Build APK (New Terminal)
```bash
./build-standalone.sh https://abc123.ngrok-free.app
```

Done! Your APK is at: `android/app/build/outputs/apk/release/app-release.apk`

---

## 📱 Install & Test

```bash
adb install -r android/app/build/outputs/apk/release/app-release.apk
```

The app will now work WITHOUT Metro server and can access your backend via ngrok!

---

## 🔄 Alternative: Local Network Only

If your phone is on the same WiFi as your computer:

```bash
npm run build:local
```

This uses `http://192.168.100.55:3000` (no ngrok needed)

---

## 🐛 Troubleshooting

**"ngrok: command not found"**
```bash
# Install ngrok
snap install ngrok  # Linux
brew install ngrok  # macOS
```

**Build fails**
```bash
# Clean and retry
cd android
./gradlew clean
cd ..
./build-standalone.sh https://your-url.ngrok-free.app
```

**App can't reach backend**
- Check ngrok is still running
- Check backend is running on port 3000
- Try opening the ngrok URL in your phone's browser

---

## 📊 What Changed

✅ Backend URL is now configurable (no hardcoded IPs)  
✅ Standalone APK doesn't need Metro server  
✅ Ngrok makes backend accessible from anywhere  
✅ Build scripts automate the entire process  

See `README-BUILD.md` for detailed documentation.
