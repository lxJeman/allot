# 🔧 Screen Capture Debugging Guide

## 🎉 Progress So Far
✅ **Permission flow working** - Confirmed by logs: `Permission result received: {"granted": true, "resultCode": -1}`
✅ **ActivityEventListener fixed** - Module now receives activity results properly
✅ **Robust implementation** - Added proper threading, error handling, and lifecycle management

## 🔍 What I Fixed in This Version

### 1. Proper Threading
- **Background thread** for MediaProjection creation (avoids main thread blocking)
- **Main thread** for UI operations (VirtualDisplay setup)
- **Background thread** for image processing (prevents ANR)

### 2. Correct Lifecycle Order
```kotlin
1. Start foreground service FIRST
2. Wait 500ms for service to initialize
3. Create MediaProjection with stored Intent
4. Set up VirtualDisplay on main thread
5. Start capture loop
```

### 3. Enhanced Error Handling
- **SecurityException** handling for permission issues
- **IllegalStateException** for invalid dimensions/surfaces
- **Detailed logging** with emojis for easy tracking
- **Proper cleanup** on any failure

### 4. Validation Checks
- Validate screen dimensions > 0
- Check ImageReader surface creation
- Verify VirtualDisplay creation
- Confirm MediaProjection is not null

## 🧪 Testing Steps

### 1. Test Permission Flow
```
1. Open app → Capture tab → Screen Capture
2. Tap "🔐 Request Permission"
3. Grant permission in system dialog
4. Should see: "Permission Granted!" alert
```

### 2. Test Screen Capture Start
```
1. Tap "🎬 Start Capture"
2. Watch logs for detailed progress:
   - 🎬 Starting screen capture...
   - 🔧 Setting up screen capture on background thread...
   - 🚀 Starting foreground service...
   - 📱 Creating MediaProjection...
   - ✅ MediaProjection created successfully
   - 🖼️ Setting up image capture...
   - 📐 Screen dimensions: WxH, density: D
   - 📷 Creating ImageReader...
   - 🖥️ Creating VirtualDisplay...
   - ✅ VirtualDisplay created successfully
   - ✅ Image capture setup completed
   - ✅ Screen capture started successfully
```

### 3. Expected Results
- ✅ **No crash** - App should remain stable
- ✅ **Foreground notification** - "Allot Screen Analysis" in notification bar
- ✅ **Live stats** - UI should show "CAPTURING" status
- ✅ **Screenshot events** - Should see captured images in preview

## 🚨 If It Still Crashes - Debugging Commands

### Get Detailed Crash Logs
```bash
# Clear logs and start fresh
adb logcat -c

# Start logging with filters for our app
adb logcat | grep -E "(ScreenCapture|MediaProjection|VirtualDisplay|AllotScreenCapture|FATAL)"

# Or get full crash details
adb logcat | grep -E "(AndroidRuntime|FATAL|ScreenCaptureModule)"
```

### Check Specific Issues
```bash
# Check for SecurityException
adb logcat | grep -i "security"

# Check for MediaProjection issues  
adb logcat | grep -i "mediaprojection"

# Check for VirtualDisplay issues
adb logcat | grep -i "virtualdisplay"

# Check for ImageReader issues
adb logcat | grep -i "imagereader"
```

## 🔍 Common Crash Causes & Solutions

### 1. SecurityException
**Cause:** Missing permissions or service not started properly
**Solution:** Check manifest permissions, ensure foreground service starts first

### 2. IllegalStateException: Invalid dimensions
**Cause:** Screen dimensions are 0x0 or negative
**Solution:** Added validation checks in setupImageCapture()

### 3. MediaProjection creation fails
**Cause:** Invalid Intent data or wrong resultCode
**Solution:** Using stored Intent directly, proper resultCode handling

### 4. VirtualDisplay creation fails
**Cause:** MediaProjection is null or ImageReader surface invalid
**Solution:** Added null checks and surface validation

### 5. Threading issues
**Cause:** Creating MediaProjection on main thread
**Solution:** Background thread for heavy operations, main thread for UI

## 📊 Success Indicators

### Logs You Should See:
```
D/ScreenCaptureModule: 🎬 Starting screen capture...
D/ScreenCaptureModule: 🔧 Setting up screen capture on background thread...
D/ScreenCaptureModule: 🚀 Starting foreground service...
D/ScreenCaptureModule: 📱 Creating MediaProjection with resultCode: -1
D/ScreenCaptureModule: ✅ MediaProjection created successfully
D/ScreenCaptureModule: 🖼️ Setting up image capture...
D/ScreenCaptureModule: 📐 Screen dimensions: 1080x2400, density: 440
D/ScreenCaptureModule: 📷 Creating ImageReader...
D/ScreenCaptureModule: 🖥️ Creating VirtualDisplay...
D/ScreenCaptureModule: ✅ VirtualDisplay created successfully
D/ScreenCaptureModule: ✅ Image capture setup completed
D/ScreenCaptureModule: ✅ Screen capture started successfully
```

### UI Indicators:
- ✅ Status shows "CAPTURING"
- ✅ Notification appears in status bar
- ✅ No crash or error alerts
- ✅ Live preview shows captured screenshots

## 🎯 Next Steps

If the crash is fixed:
1. **Test live capture** - Should see screenshots in preview
2. **Test backend integration** - Send captures to Rust server
3. **Test stop/start cycle** - Verify cleanup works properly
4. **Test app backgrounding** - Ensure service continues running

If it still crashes:
1. **Run the debug commands above**
2. **Share the exact crash logs**
3. **Note which step fails** (service start, MediaProjection, VirtualDisplay, etc.)

The robust implementation should handle all common crash scenarios. The detailed logging will help identify any remaining issues!