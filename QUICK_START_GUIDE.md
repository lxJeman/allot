# 🚀 Allot - Quick Start Guide

## Phase 3 & 4 Complete - Ready for Testing!

---

## ⚡ Quick Start (5 Minutes)

### 1. Start Backend (Terminal 1)

```bash
cd rust-backend
cargo run
```

Wait for:
```
🚀 Starting Allot AI Detection Backend
🌐 Server listening on http://0.0.0.0:3000
```

### 2. Build & Install App (Terminal 2)

```bash
# Build
./android/gradlew -p android assembleDebug

# Install
adb install android/app/build/outputs/apk/debug/app-debug.apk
```

### 3. Setup App (On Device)

1. Open Allot app
2. Go to **Permissions** screen
3. Grant **Screen Capture** permission
4. Enable **Accessibility Service**
5. Enable **Notifications** (optional)

### 4. Start Monitoring

1. Go to **Phase 4 Demo** screen
2. Tap **"Start Monitoring"**
3. Open **TikTok**
4. Scroll through feed
5. Watch for harmful content detection!

---

## 📱 What It Does

**When you scroll TikTok**:
1. 📸 Captures screenshot
2. 🔍 Extracts text (OCR)
3. 🧠 Analyzes content (AI)
4. ⚠️ Shows warning if harmful
5. ⏭️ Auto-scrolls past it

**Smart Features**:
- Only monitors social media (TikTok, Instagram, etc.)
- 90% cost reduction
- Real-time statistics
- Token usage tracking

---

## 🎯 Key Screens

### Permissions Screen
- Grant all required permissions
- Test features individually

### Phase 4 Demo Screen
- Start/stop monitoring
- View current app
- See statistics
- Toggle auto-scroll

---

## 📊 What You'll See

### App Status
- 🟢 **"TikTok ✓ Monitored"** - Active monitoring
- ⚪ **"Chrome Not Monitored"** - Paused

### Statistics
- **Total Analyzed**: Screenshots processed
- **Harmful Detected**: Bad content found
- **Auto-Scrolled**: Times scrolled
- **Avg Time**: Processing speed

### Last Detection
- **Category**: toxic, political, clickbait, etc.
- **Confidence**: AI confidence score
- **Action**: continue, scroll, or blur
- **Tokens Used**: API cost

---

## 🔧 Configuration

### Backend URL
Default: `http://192.168.100.47:3000`

Change in `phase4-demo.tsx`:
```typescript
const BACKEND_URL = 'http://YOUR_IP:3000';
```

### Monitored Apps
Edit `AllotAccessibilityService.kt`:
```kotlin
private val MONITORED_APPS = setOf(
    "com.zhiliaoapp.musically", // TikTok
    "com.instagram.android",     // Instagram
    // Add more...
)
```

---

## 🐛 Troubleshooting

### Backend Not Responding
```bash
# Test health check
curl http://192.168.100.47:3000/health

# Should return:
{"status":"healthy","message":"Allot AI Detection Backend is running"}
```

### App Detection Not Working
1. Enable accessibility service in Android settings
2. Restart app
3. Check logs: `adb logcat | grep Allot`

### Auto-Scroll Not Working
1. Check Android version (N+ required)
2. Enable accessibility service
3. Toggle auto-scroll ON in Phase 4 Demo

---

## 📈 Performance

- **App Detection**: <1ms (instant)
- **Screenshot**: ~10ms
- **Backend Analysis**: 2-2.5 seconds
- **Auto-Scroll**: 300ms (smooth)

---

## 🎯 Testing Checklist

- [ ] Backend running
- [ ] App installed
- [ ] Permissions granted
- [ ] Monitoring started
- [ ] TikTok opened
- [ ] Content detected
- [ ] Auto-scroll working
- [ ] Statistics updating

---

## 📚 Documentation

- **PHASE3_QUICKSTART.md** - Phase 3 details
- **PHASE4_COMPLETE.md** - Phase 4 details
- **PHASE3_AND_4_FINAL_SUMMARY.md** - Complete overview

---

## 🎉 Success!

When everything works, you'll see:

1. **TikTok opens** → "TikTok ✓ Monitored"
2. **Scroll feed** → Screenshots captured
3. **Harmful content** → Warning overlay appears
4. **Auto-scroll** → Skips to next video
5. **Statistics** → Numbers update

**You're now protecting yourself from harmful content!** 🛡️

---

## 🚀 Next Steps

1. Test with different apps (Instagram, Facebook)
2. Adjust auto-scroll settings
3. View detection history
4. Monitor token usage
5. Customize block list (Phase 5)

---

**Need help?** Check the full documentation or logs:
```bash
# Backend logs
cd rust-backend && cargo run

# Android logs
adb logcat | grep Allot
```
