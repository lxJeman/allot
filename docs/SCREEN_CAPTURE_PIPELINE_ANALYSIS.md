# 🔍 Screen Capture Pipeline - Complete Analysis

## 📋 Overview

The screen capture system has multiple layers that work together. Here's how I traced the real pipeline from frontend UI to backend processing.

## 🎯 Complete Data Flow

```
[User Taps Button] 
    ↓
[React Native UI] 
    ↓
[Native Android Modules] 
    ↓
[Screen Capture Service] 
    ↓
[ML Kit Text Extraction] 
    ↓
[Rust Backend Analysis] 
    ↓
[Results Back to UI]
```

## 🔧 How I Found the Real Pipeline

### 1. **Started from UI Layer** (`app/screen-capture.tsx`)
- Found the main capture button: `startCapture()` function
- Traced calls to `ScreenCaptureModule.startSequentialCapture()`
- Discovered the capture loop logic and `shouldProcessCapture()` filtering

### 2. **Followed Native Module Calls**
- `ScreenCaptureModule` → `android/app/src/main/java/com/allot/ScreenCaptureModule.kt`
- `AppDetectionModule` → `android/app/src/main/java/com/allot/AppDetectionModule.kt`
- `SmartDetectionModule` → `android/app/src/main/java/com/allot/SmartDetectionModule.kt`

### 3. **Traced Service Architecture**
- Found `ScreenCaptureService.kt` - the main background service
- Discovered `LocalTextExtractionService.kt` - handles ML Kit processing
- Located `AllotAccessibilityService.kt` - monitors app changes

### 4. **Mapped Backend Integration**
- Found Rust backend calls in logs: `http://192.168.100.55:3000/analyze`
- Traced text extraction → backend analysis → result caching

## 📱 Frontend Layer Details

### Main UI Component: `app/screen-capture.tsx`

**Key Functions:**
```typescript
// Main capture control
startCapture() → ScreenCaptureModule.startSequentialCapture()

// Smart filtering logic  
shouldProcessCapture() → AppDetectionModule.isMonitoredApp()

// Processing pipeline
processCapture() → SmartDetectionModule.extractTextFromImage()
```

**State Management:**
```typescript
interface CaptureStats {
  totalCaptures: number;
  isCapturing: boolean;
  currentApp: string;
  isMonitoredApp: boolean;
  capturesSkipped: number;
  // ... more stats
}
```

**UI Controls:**
- Smart Capture Toggle (ON/OFF)
- Capture Interval (100ms, 500ms, 1000ms, 2000ms)
- Start/Stop Capture buttons
- App Detection Status display

## 🔧 Native Android Layer

### 1. **ScreenCaptureModule.kt** - Main Interface
```kotlin
@ReactMethod
fun startSequentialCapture(interval: Int, promise: Promise)

@ReactMethod  
fun captureNextFrame(promise: Promise)

@ReactMethod
fun isCapturing(promise: Promise)
```

**What it does:**
- Manages MediaProjection for screen recording
- Creates VirtualDisplay for frame capture
- Handles ImageReader for bitmap extraction
- Provides React Native bridge methods

### 2. **HttpBridgeModule.kt** - NEW: Native HTTP Client
```kotlin
@ReactMethod
fun post(url: String, headers: ReadableMap?, body: String, timeoutMs: Int, promise: Promise)

@ReactMethod
fun get(url: String, headers: ReadableMap?, timeoutMs: Int, promise: Promise)
```

**What it does:**
- Replaces React Native's broken fetch API
- Uses Android's reliable HttpURLConnection
- Fast 2s timeouts (vs 34s hangs)
- Proper error handling and response parsing
- **Performance**: 366ms responses ✅ vs 34+ second hangs ❌

### 2. **ScreenCaptureService.kt** - Background Processing
```kotlin
class ScreenCaptureService : Service() {
    // Manages continuous capture loop
    // Handles frame processing
    // Integrates with LocalTextExtractionService
}
```

**Key Features:**
- Foreground service for background operation
- Frame rate control and optimization
- Memory management for bitmaps
- Integration with ML Kit processing

### 3. **AppDetectionModule.kt** - Smart Filtering
```kotlin
@ReactMethod
fun getCurrentApp(promise: Promise)

@ReactMethod
fun isMonitoredApp(promise: Promise)

@ReactMethod
fun isAccessibilityServiceEnabled(promise: Promise)
```

**Integration Points:**
- Connects to `AllotAccessibilityService`
- Provides real-time app detection
- Enables smart capture filtering

### 4. **SmartDetectionModule.kt** - ML Processing
```kotlin
@ReactMethod
fun extractTextFromImage(base64Image: String, promise: Promise)
```

**Processing Pipeline:**
- Converts base64 → Bitmap
- Runs Google ML Kit text recognition
- Extracts text regions and confidence scores
- Sends results to backend for analysis

## 🤖 Backend Integration

### Local ML Kit Processing
```kotlin
// In SmartDetectionModule.kt
val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
recognizer.process(inputImage)
    .addOnSuccessListener { visionText ->
        // Extract text and send to backend
    }
```

### Backend Communication - UPDATED WITH NATIVE HTTP BRIDGE
```typescript
// NEW: Native HTTP Bridge (replaces broken React Native fetch)
const { nativeHttpClient } = await import('./nativeHttpBridge');

const response = await nativeHttpClient.post('http://192.168.100.55:3000/analyze', {
  body: {
    extracted_text: extractedText,
    source: 'local_ml_kit',
    timestamp: Date.now(),
  },
  timeout: 2000, // Fast 2s timeout vs 34s hangs
});

// Results: 366ms response time ✅ (vs 34+ second hangs ❌)
```

**Legacy Kotlin Backend (still used by ScreenCaptureService):**
```kotlin
// HTTP POST to backend
val url = "http://192.168.100.55:3000/analyze"
val payload = JSONObject().apply {
    put("text", extractedText)
    put("source", "local_ml_kit")
    put("timestamp", System.currentTimeMillis())
}
```

**Backend Response:**
```json
{
  "category": "safe_content",
  "confidence": 99.0,
  "harmful": false,
  "action": "CONTINUE",
  "recommendation": "No war-related content detected"
}
```

## 🔄 Complete Processing Flow

### 1. **Capture Initiation**
```
User taps "Start Capture" 
→ startCapture() in React Native
→ ScreenCaptureModule.startSequentialCapture()
→ Creates MediaProjection + VirtualDisplay
→ Starts capture loop with specified interval
```

### 2. **Frame Processing Loop**
```
Timer triggers capture
→ captureNextFrame() 
→ shouldProcessCapture() checks app detection
→ If monitored app: processCapture()
→ If not monitored: skip and continue loop
```

### 3. **Smart Filtering Decision**
```
shouldProcessCapture()
→ AppDetectionModule.isMonitoredApp()
→ AllotAccessibilityService.isCurrentAppMonitored()
→ Checks current app against MONITORED_APPS list
→ Returns true/false for processing decision
```

### 4. **Text Extraction Pipeline - UPDATED WITH NATIVE HTTP**
```
processCapture()
→ SmartDetectionModule.extractTextFromImage()
→ ML Kit text recognition
→ Extract text regions + confidence
→ NATIVE HTTP CLIENT sends to Rust backend (366ms ✅)
→ Return results to React Native (no more 34s hangs ❌)
```

### 5. **Result Handling**
```
Backend analysis complete
→ Update UI stats (totalTextExtractions, etc.)
→ Log results for debugging
→ Continue capture loop or stop based on user action
```

## 🎛️ Key Integration Points for New Features

### 1. **Adding New UI Controls**
**Location:** `app/screen-capture.tsx`
```typescript
// Add new state
const [newFeature, setNewFeature] = useState(false);

// Add new UI element
<TouchableOpacity onPress={() => setNewFeature(!newFeature)}>
  <Text>New Feature: {newFeature ? 'ON' : 'OFF'}</Text>
</TouchableOpacity>
```

### 2. **Adding New Native Methods**
**Location:** `android/app/src/main/java/com/allot/ScreenCaptureModule.kt`
```kotlin
@ReactMethod
fun newFeatureMethod(param: String, promise: Promise) {
    try {
        // Your new functionality
        promise.resolve(result)
    } catch (e: Exception) {
        promise.reject("ERROR", e.message)
    }
}
```

### 3. **Extending Processing Pipeline**
**Location:** `android/app/src/main/java/com/allot/SmartDetectionModule.kt`
```kotlin
// Add new processing step
private fun processWithNewFeature(bitmap: Bitmap): ProcessingResult {
    // Your new processing logic
    return ProcessingResult(...)
}
```

### 4. **Adding New Backend Endpoints**
**Current backend:** `http://192.168.100.55:3000/analyze`
```kotlin
// Add new endpoint call
val newEndpointUrl = "http://192.168.100.55:3000/new-feature"
// Send different payload format
```

### 5. **Extending App Detection**
**Location:** `android/app/src/main/java/com/allot/AllotAccessibilityService.kt`
```kotlin
// Add new monitored apps
private val MONITORED_APPS = setOf(
    "com.zhiliaoapp.musically",    // TikTok
    "com.instagram.android",       // Instagram
    "com.your.new.app"            // New app to monitor
)

// Add new detection logic
fun detectSpecialContent(): Boolean {
    // Your new detection logic
}
```

## 🚀 Common Extension Patterns

### 1. **Adding New Capture Modes**
- Extend `startCapture()` with mode parameter
- Add mode-specific processing in `processCapture()`
- Update UI to show mode selection

### 2. **Adding New Filters**
- Extend `shouldProcessCapture()` with additional checks
- Add new filtering criteria (time-based, content-based, etc.)
- Update UI to show filter status

### 3. **Adding New Analysis Types**
- Extend ML Kit processing with additional recognizers
- Add new backend endpoints for different analysis
- Update result handling for new data types

### 4. **Adding New Data Sources**
- Extend beyond screen capture (camera, files, etc.)
- Add new input methods to processing pipeline
- Update UI for new data source selection

## 📊 Performance Monitoring Points

### Key Metrics to Track:
- Capture interval timing
- ML Kit processing time
- Backend response time
- Memory usage for bitmaps
- App detection accuracy
- Skip rate for non-monitored apps

### Logging Integration Points:
- React Native: `console.log()` for UI events
- Android: `Log.d(TAG, message)` for native events
- Backend: Server logs for analysis results

This pipeline analysis gives you the complete picture of how everything connects, making it easy to add new features at any layer! 🎉