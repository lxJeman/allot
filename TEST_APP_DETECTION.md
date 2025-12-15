# Test App Detection - Quick Guide

## Install & Test

### 1. Install Updated APK
```bash
adb install android/app/build/outputs/apk/debug/app-debug.apk
```

### 2. Monitor Service Logs
```bash
adb logcat -c && adb logcat | grep -E "(ScreenCaptureService|AllotAccessibility)"
```

### 3. Test Steps
1. **Open Allot app**
2. **Start screen capture** (you should see service logs)
3. **Switch to TikTok** (you should see app detection)
4. **Switch to Chrome** (you should see different app)
5. **Switch back to Allot** (you should see "Our App")

## Expected Logs

### When Service Starts
```
✅ ═══════════════════════════════════════
✅ SCREEN CAPTURE SERVICE CREATED
✅ ═══════════════════════════════════════
🚀 ScreenCaptureService started
🔄 Starting background processing...
🎬 Auto-starting capture loop for background processing
```

### App Detection (Every 3 seconds)
```
📱 CURRENT APP: Allot (Our App) (not monitored) | Service: ACTIVE
📱 CURRENT APP: Chrome (not monitored) | Service: ACTIVE
🎯 MONITORING: TikTok | Capture: ON | Processing: READY
📱 CURRENT APP: Instagram (not monitored) | Service: ACTIVE
```

### If Accessibility Service Not Working
```
⚠️ ACCESSIBILITY SERVICE NOT AVAILABLE - Enable in Android Settings > Accessibility > Allot
```

## Troubleshooting

### If No Service Logs
1. **Check if service is running:**
   ```bash
   adb shell dumpsys activity services | grep ScreenCaptureService
   ```

2. **Check if app is installed:**
   ```bash
   adb shell pm list packages | grep allot
   ```

### If No App Detection
1. **Check accessibility service:**
   ```bash
   adb shell settings get secure enabled_accessibility_services
   ```
   Should include: `com.allot/.AllotAccessibilityService`

2. **Re-enable accessibility:**
   - Settings > Accessibility > Allot > Toggle OFF then ON

### If Service Stops
1. **Check battery optimization:**
   - Settings > Apps > Allot > Battery > Don't optimize

2. **Check background app limits:**
   - Settings > Apps > Allot > Battery > Background Activity > Allow

## What You Should See

### ✅ Working Correctly
- Service creation logs when starting capture
- App detection logs every 3 seconds
- Different apps detected: "TikTok", "Chrome", "Allot (Our App)"
- Service continues when switching apps

### ❌ Not Working
- No service logs = Service not starting
- No app detection = Accessibility service issue
- Service stops = Battery optimization killing it

## Quick Test Commands

### Start Fresh Test
```bash
# Clear logs and start monitoring
adb logcat -c && adb logcat | grep -E "(ScreenCapture|Accessibility|🎯|📱)"
```

### Check Service Status
```bash
adb shell dumpsys activity services | grep -A 5 ScreenCaptureService
```

### Check Accessibility
```bash
adb shell settings get secure enabled_accessibility_services
```

The key is to see the **continuous app detection logs every 3 seconds**. If you see those, the system is working correctly!