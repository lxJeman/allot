# Background Capture Troubleshooting Guide

## Issues Fixed

### 1. Service Not Auto-Starting Capture Loop ✅
**Problem:** Service was running but not automatically starting the capture loop
**Fix:** Added auto-start of capture loop in `onStartCommand()`

### 2. Missing App Detection Logs ✅
**Problem:** No logs showing which app is currently open
**Fix:** Enhanced logging to show current app every 3 seconds with detailed status

### 3. Accessibility Service Dependency ✅
**Problem:** Capture stopped working when accessibility service wasn't available
**Fix:** Made capture work even without accessibility service (for testing)

## Current Status

### Service Enhancements ✅
```kotlin
// Auto-start capture loop when service starts
if (!isCaptureLoopActive) {
    Log.d(TAG, "🎬 Auto-starting capture loop for background processing")
    startCaptureLoop()
}

// Enhanced app monitoring logs every 3 seconds
Log.i(TAG, "🎯 MONITORING: TikTok | Capture: ON | Processing: READY")
Log.i(TAG, "📱 CURRENT APP: Chrome (not monitored) | Service: ACTIVE")
```

### Fallback Capture Mode ✅
```kotlin
// If accessibility service is not available, capture anyway
val shouldCapture = if (accessibilityService != null) {
    isMonitoredApp
} else {
    Log.w(TAG, "⚠️ Capturing without app detection (accessibility service unavailable)")
    true
}
```

## Testing Instructions

### 1. Install Updated APK
```bash
adb install android/app/build/outputs/apk/debug/app-debug.apk
```

### 2. Enable Accessibility Service
1. Go to **Android Settings > Accessibility**
2. Find **Allot** in the list
3. Enable the accessibility service
4. Grant permissions

### 3. Start Screen Capture
1. Open Allot app
2. Grant screen capture permission
3. Start capture
4. **Close the app** (important for testing background)

### 4. Monitor Logs
```bash
adb logcat | grep -E "(ScreenCaptureService|AllotAccessibility)"
```

### Expected Logs

#### Service Starting
```
✅ ═══════════════════════════════════════
✅ SCREEN CAPTURE SERVICE CREATED
✅ ═══════════════════════════════════════
✅ Service instance set: true
✅ Backend URL: http://192.168.171.18:3000/analyze
✅ Native backend: ENABLED
✅ ═══════════════════════════════════════

🚀 ScreenCaptureService started
🎬 Auto-starting capture loop for background processing
🎬 Starting native capture loop (500ms interval)
```

#### App Detection (Every 3 seconds)
```
📱 CURRENT APP: Chrome (not monitored) | Service: ACTIVE
🎯 MONITORING: TikTok | Capture: ON | Processing: READY
📱 CURRENT APP: Allot (Our App) (not monitored) | Service: ACTIVE
```

#### Background Processing
```
📸 [TikTok] Frame captured: 1080x2400
📊 [TikTok] Analysis complete (450ms): safe_content (95%)
✅ [TikTok] Content safe
```

## Troubleshooting Steps

### If No App Detection Logs
1. **Check Accessibility Service:**
   ```bash
   adb shell settings get secure enabled_accessibility_services
   ```
   Should include: `com.allot/.AllotAccessibilityService`

2. **Enable Accessibility Service:**
   - Settings > Accessibility > Allot > Enable
   - Grant all permissions

3. **Expected Warning if Disabled:**
   ```
   ⚠️ ACCESSIBILITY SERVICE NOT AVAILABLE - Enable in Android Settings > Accessibility > Allot
   ```

### If Background Capture Stops
1. **Check Service Status:**
   ```bash
   adb shell dumpsys activity services | grep ScreenCaptureService
   ```

2. **Check for Service Logs:**
   ```
   🚀 ScreenCaptureService started
   🎬 Auto-starting capture loop for background processing
   ```

3. **Check Wake Lock:**
   ```
   🔋 WakeLock acquired
   ```

### If Capture Only Works in App
1. **Verify Service is Running:**
   - Look for: `🎬 Starting native capture loop`
   - Should continue even when app is closed

2. **Check Backend Connection:**
   ```bash
   curl http://192.168.171.18:3000/health
   ```

3. **Verify Native Backend Mode:**
   - Look for: `🌐 Native backend: ENABLED`

## Expected Behavior

### When App is Open
- ✅ React Native capture works
- ✅ Service also runs in parallel
- ✅ Logs show current app detection

### When App is Closed
- ✅ Service continues running
- ✅ Native capture loop continues
- ✅ Frames sent directly to backend
- ✅ App detection continues (if accessibility enabled)

### When TikTok is Open
- ✅ Logs show: `🎯 MONITORING: TikTok | Capture: ON`
- ✅ Frames captured with `[TikTok]` prefix
- ✅ Analysis results logged

## Common Issues

### "Accessibility service not available"
**Solution:** Enable in Android Settings > Accessibility > Allot

### "Service not capturing in background"
**Check:** Look for auto-start log: `🎬 Auto-starting capture loop for background processing`

### "No app detection"
**Fallback:** Service will capture anyway but log: `⚠️ Capturing without app detection`

## Debug Commands

### Check Service Status
```bash
adb shell dumpsys activity services | grep -A 10 ScreenCaptureService
```

### Check Accessibility Services
```bash
adb shell settings get secure enabled_accessibility_services
```

### Monitor Real-time Logs
```bash
adb logcat -c && adb logcat | grep -E "(ScreenCapture|Accessibility|Allot)"
```

### Test Backend Connection
```bash
curl -X POST http://192.168.171.18:3000/analyze \
  -H "Content-Type: application/json" \
  -d '{"image":"test","width":100,"height":100,"timestamp":1234567890}'
```

## Success Indicators

- ✅ Service starts automatically with capture loop
- ✅ App detection logs every 3 seconds
- ✅ Background capture continues when app is closed
- ✅ TikTok detection works specifically
- ✅ Frames processed and sent to backend

The background capture should now work properly! 🎉