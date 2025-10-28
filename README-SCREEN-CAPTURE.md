# 📸 Allot Screen Capture System

## Phase 2 Implementation Complete ✅

The screen capture system is now fully implemented and ready for testing!

## 🏗️ Architecture

### Native Android Components
- **ScreenCaptureModule.kt** - Main native module handling MediaProjection API
- **ScreenCaptureService.kt** - Foreground service for background operation
- **ScreenCapturePackage.kt** - React Native bridge registration

### React Native Components
- **screen-capture.tsx** - Full-featured UI for screen capture management
- **capture tab** - Quick access from main navigation

### Backend
- **Rust Axum Server** - High-performance analysis backend
- **Real-time processing** - Simulates AI analysis with 2.5s delay
- **REST API** - `/analyze` endpoint for screen data

## 🚀 How to Use

### 1. Start the Backend
```bash
cd rust-backend
cargo run
```
Server will start on `http://localhost:3000`

### 2. Launch the App
```bash
./build-and-install.sh
```

### 3. Grant Permissions
1. Go to **Permissions** tab
2. Grant **Screen Capture** permission
3. Grant **Notifications** permission (for foreground service)

### 4. Start Screen Capture
1. Go to **Capture** tab → **Open Screen Capture**
2. Tap **🔐 Request Permission** (if not already granted)
3. Tap **🎬 Start Capture**
4. Check notification bar - you should see "Allot Screen Analysis" notification

### 5. Monitor Captures
- **Real-time stats** - Total captures, interval, status
- **Live preview** - See the last captured screenshot
- **Backend integration** - Tap **🚀 Send to Backend** to test AI analysis

## ⚙️ Configuration

### Capture Intervals
- **100ms** - 10 FPS (default, real-time)
- **500ms** - 2 FPS (balanced)
- **1000ms** - 1 FPS (battery saving)
- **2000ms** - 0.5 FPS (minimal)

### Backend Response Format
```json
{
  "id": "uuid",
  "status": "completed",
  "analysis": {
    "category": "safe_content|political_content|toxic_content|clickbait|advertisement",
    "confidence": 0.95,
    "harmful": false,
    "action": "continue|scroll|blur",
    "details": {
      "detected_text": ["Educational content", "News article"],
      "content_type": "news",
      "risk_factors": [],
      "recommendation": "Content appears safe to view"
    }
  },
  "processing_time_ms": 2500,
  "timestamp": 1698765432000
}
```

## 🔧 Technical Details

### MediaProjection Behavior
- **Permission is ephemeral** - Must be re-granted each app session
- **System dialog** - Android shows permission dialog every time
- **Foreground service** - Keeps capture active in background
- **Automatic cleanup** - Resources released when stopping

### Performance Optimizations
- **JPEG compression** - 80% quality for smaller file sizes
- **Cache management** - Keeps only last 10 screenshots
- **Base64 encoding** - Efficient transfer to React Native
- **Background processing** - Non-blocking UI updates

### File Locations
- **Screenshots cache**: `/data/data/com.allot/cache/screenshots/`
- **Notification channel**: `screen_capture_service`
- **Service type**: `mediaProjection`

## 🧪 Testing Scenarios

### 1. Permission Flow
- Request permission → Grant → Start capture
- Deny permission → Show error → Retry

### 2. Capture Loop
- Start capture → See live stats updating
- Change interval → Verify capture rate changes
- Stop capture → Verify cleanup

### 3. Backend Integration
- Capture screen → Send to backend → Receive analysis
- Test different content types → Verify AI responses
- Network error handling → Graceful fallbacks

### 4. Background Operation
- Start capture → Minimize app → Check notification
- Return to app → Verify capture still running
- Kill app → Service should stop gracefully

## 🐛 Known Issues & Solutions

### Issue: Permission Dialog Every Session
**Expected Behavior** - MediaProjection permissions are not persistent
**Solution** - User must grant permission each time app starts

### Issue: Capture Stops When App Killed
**Expected Behavior** - Foreground service should keep running
**Check** - Notification should remain visible

### Issue: Backend Connection Failed
**Solution** - Ensure Rust server is running on localhost:3000
**Alternative** - Update backend URL in screen-capture.tsx

## 📊 Performance Metrics

### Capture Performance
- **Image size**: ~50-200KB per screenshot (JPEG 80%)
- **Capture rate**: 100ms intervals = 10 FPS
- **Memory usage**: ~10MB for image processing
- **Battery impact**: Moderate (foreground service + screen capture)

### Backend Performance
- **Processing time**: 2.5s (simulated AI analysis)
- **Response size**: ~1KB JSON
- **Concurrent requests**: Supported via Rust async

## 🔄 Next Steps (Phase 3)

1. **Real AI Integration** - Replace simulated analysis with actual ML models
2. **Content Detection** - OCR, image classification, harmful content detection
3. **Action System** - Auto-scroll, blur overlay, content blocking
4. **User Configuration** - Customizable filtering rules
5. **Performance Optimization** - Reduce processing time, improve battery life

## 🎯 Success Criteria ✅

- [x] MediaProjection API integration
- [x] Real-time screen capture (100ms intervals)
- [x] Foreground service for background operation
- [x] Base64 image encoding and transfer
- [x] React Native bridge with event emission
- [x] Complete UI for capture management
- [x] Rust backend with analysis simulation
- [x] Permission management integration
- [x] Live statistics and monitoring
- [x] Backend communication testing

**Phase 2 is complete and ready for Phase 3 AI integration!** 🚀