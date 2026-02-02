# 🚀 Adding New Features to Screen Capture - Practical Guide

## 🎯 Quick Reference for Feature Development

Based on the pipeline analysis, here are the most common patterns for adding new features:

## 📱 Pattern 1: Adding New UI Controls

### Example: Add "Night Mode" Toggle

**Step 1: Update React Native State**
```typescript
// In app/screen-capture.tsx
const [nightModeEnabled, setNightModeEnabled] = useState(false);
```

**Step 2: Add UI Control**
```typescript
<TouchableOpacity
  style={[styles.button, { backgroundColor: nightModeEnabled ? '#4CAF50' : '#9E9E9E' }]}
  onPress={() => setNightModeEnabled(!nightModeEnabled)}
>
  <Text style={styles.buttonText}>
    {nightModeEnabled ? '🌙 Night Mode: ON' : '☀️ Night Mode: OFF'}
  </Text>
</TouchableOpacity>
```

**Step 3: Use in Processing**
```typescript
const processCapture = async () => {
  // Pass night mode to native processing
  const result = await SmartDetectionModule.extractTextFromImage(
    base64Image, 
    nightModeEnabled
  );
};
```

## 🔧 Pattern 2: Adding New Native Methods

### Example: Add Image Enhancement Feature

**Step 1: Add Native Method**
```kotlin
// In android/app/src/main/java/com/allot/SmartDetectionModule.kt
@ReactMethod
fun enhanceImageForOCR(base64Image: String, enhancementLevel: Int, promise: Promise) {
    try {
        val bitmap = base64ToBitmap(base64Image)
        val enhancedBitmap = applyImageEnhancement(bitmap, enhancementLevel)
        val result = bitmapToBase64(enhancedBitmap)
        promise.resolve(result)
    } catch (e: Exception) {
        Log.e(TAG, "Error enhancing image", e)
        promise.reject("ENHANCEMENT_ERROR", e.message)
    }
}

private fun applyImageEnhancement(bitmap: Bitmap, level: Int): Bitmap {
    // Your image enhancement logic here
    // Example: contrast, brightness, sharpening
    return enhancedBitmap
}
```

**Step 2: Call from React Native**
```typescript
// In app/screen-capture.tsx
const enhanceAndProcess = async (base64Image: string) => {
  try {
    const enhanced = await SmartDetectionModule.enhanceImageForOCR(base64Image, 2);
    const result = await SmartDetectionModule.extractTextFromImage(enhanced);
    return result;
  } catch (error) {
    console.error('Enhancement failed:', error);
    return null;
  }
};
```

## ⚡ Pattern 2.5: Using Native HTTP Bridge (NEW - RECOMMENDED)

### Example: Add Real-time Content Moderation

**Step 1: Import Native HTTP Client**
```typescript
// In your React Native component or service
const { nativeHttpClient } = await import('../services/nativeHttpBridge');
```

**Step 2: Make Fast HTTP Calls**
```typescript
const moderateContent = async (text: string) => {
  try {
    const response = await nativeHttpClient.post('http://192.168.100.55:3000/moderate', {
      body: {
        content: text,
        strictness: 'high',
        timestamp: Date.now(),
      },
      timeout: 1500, // Even faster timeout for real-time
    });
    
    if (response.success) {
      const result = JSON.parse(response.data!);
      console.log(`✅ Moderation complete in ${response.responseTime}ms`);
      return result;
    } else {
      throw new Error(`Moderation failed: ${response.error}`);
    }
  } catch (error) {
    console.error('❌ Moderation error:', error);
    return { safe: true, confidence: 0 }; // Fail-safe
  }
};
```

**Step 3: Integrate with Processing Pipeline**
```typescript
const processCapture = async (captureData: CaptureData) => {
  // Extract text first
  const textResult = await SmartDetectionModule.extractTextFromImage(captureData.base64);
  
  // Then moderate using native HTTP (fast!)
  const moderationResult = await moderateContent(textResult.extractedText);
  
  return {
    text: textResult,
    moderation: moderationResult,
    processingTime: `${Date.now() - captureData.timestamp}ms`,
  };
};
```

**Why Use Native HTTP Bridge:**
- **Performance**: 366ms vs 34+ second hangs
- **Reliability**: Never hangs or times out improperly
- **Simplicity**: No Promise.race workarounds needed
- **Error Handling**: Proper HTTP status codes

## 🎛️ Pattern 3: Extending App Detection

### Example: Add Content-Based Detection

**Step 1: Extend Accessibility Service**
```kotlin
// In android/app/src/main/java/com/allot/AllotAccessibilityService.kt
fun detectContentType(): String {
    try {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            // Analyze UI elements for content type
            val hasVideoPlayer = findVideoPlayerElements(rootNode)
            val hasTextInput = findTextInputElements(rootNode)
            
            return when {
                hasVideoPlayer -> "video_content"
                hasTextInput -> "text_content"
                else -> "unknown_content"
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error detecting content type", e)
    }
    return "unknown_content"
}

private fun findVideoPlayerElements(node: AccessibilityNodeInfo): Boolean {
    // Look for video player UI elements
    return node.className?.contains("VideoView") == true ||
           node.contentDescription?.contains("video", ignoreCase = true) == true
}
```

**Step 2: Add Bridge Method**
```kotlin
// In android/app/src/main/java/com/allot/AppDetectionModule.kt
@ReactMethod
fun getContentType(promise: Promise) {
    try {
        val service = AllotAccessibilityService.getInstance()
        val contentType = service?.detectContentType() ?: "unknown"
        promise.resolve(contentType)
    } catch (e: Exception) {
        promise.reject("ERROR", e.message)
    }
}
```

**Step 3: Use in Smart Capture Logic**
```typescript
// In app/screen-capture.tsx
const shouldProcessCapture = async (): Promise<boolean> => {
  if (!smartCaptureEnabled) return true;
  
  const isMonitored = await AppDetectionModule.isMonitoredApp();
  const contentType = await AppDetectionModule.getContentType();
  
  // Enhanced logic: only process video content in monitored apps
  return isMonitored && contentType === 'video_content';
};
```

## 🔄 Pattern 4: Adding New Processing Modes

### Example: Add "Batch Processing" Mode

**Step 1: Add Mode State**
```typescript
// In app/screen-capture.tsx
type ProcessingMode = 'realtime' | 'batch' | 'selective';
const [processingMode, setProcessingMode] = useState<ProcessingMode>('realtime');
const [batchQueue, setBatchQueue] = useState<CaptureData[]>([]);
```

**Step 2: Modify Capture Logic**
```typescript
const processCapture = async (captureData: CaptureData) => {
  switch (processingMode) {
    case 'realtime':
      return await processImmediately(captureData);
      
    case 'batch':
      setBatchQueue(prev => [...prev, captureData]);
      if (batchQueue.length >= 5) {
        return await processBatch(batchQueue);
      }
      return null;
      
    case 'selective':
      const shouldProcess = await shouldProcessCapture();
      return shouldProcess ? await processImmediately(captureData) : null;
  }
};

const processBatch = async (captures: CaptureData[]) => {
  // Process multiple captures together
  const results = await Promise.all(
    captures.map(capture => SmartDetectionModule.extractTextFromImage(capture.base64))
  );
  setBatchQueue([]); // Clear queue
  return results;
};
```

## 🌐 Pattern 5: Adding New Backend Endpoints - UPDATED WITH NATIVE HTTP

### Example: Add Image Classification Endpoint

**Step 1: Use Native HTTP Bridge (RECOMMENDED)**
```typescript
// NEW: Use native HTTP client instead of broken React Native fetch
const { nativeHttpClient } = await import('../services/nativeHttpBridge');

const classifyImage = async (base64Image: string) => {
  try {
    const response = await nativeHttpClient.post('http://192.168.100.55:3000/classify', {
      body: {
        image: base64Image,
        task: 'classification',
        timestamp: Date.now(),
      },
      timeout: 2000, // Fast 2s timeout
    });
    
    if (response.success) {
      return JSON.parse(response.data!);
    } else {
      throw new Error(`HTTP ${response.status}: ${response.error}`);
    }
  } catch (error) {
    console.error('Classification failed:', error);
    throw error;
  }
};
```

**Step 2: Alternative - Add Native Kotlin Method (if needed)**
```kotlin
// In android/app/src/main/java/com/allot/SmartDetectionModule.kt
private fun classifyImage(bitmap: Bitmap): JSONObject? {
    return try {
        val base64 = bitmapToBase64(bitmap)
        val payload = JSONObject().apply {
            put("image", base64)
            put("task", "classification")
            put("timestamp", System.currentTimeMillis())
        }
        
        val response = sendToBackend("http://192.168.100.55:3000/classify", payload)
        JSONObject(response)
    } catch (e: Exception) {
        Log.e(TAG, "Error classifying image", e)
        null
    }
}

@ReactMethod
fun classifyScreenContent(base64Image: String, promise: Promise) {
    try {
        val bitmap = base64ToBitmap(base64Image)
        val result = classifyImage(bitmap)
        promise.resolve(result?.toString())
    } catch (e: Exception) {
        promise.reject("CLASSIFICATION_ERROR", e.message)
    }
}
```

**Step 3: Use in Processing Pipeline**
```typescript
// In app/screen-capture.tsx
const processCapture = async (captureData: CaptureData) => {
  // OPTION 1: Use native HTTP bridge (RECOMMENDED - 366ms responses)
  const classificationResult = await classifyImage(captureData.base64);
  
  // OPTION 2: Use native module (if you need Kotlin-specific processing)
  // const classificationResult = await SmartDetectionModule.classifyScreenContent(captureData.base64);
  
  // Run both text extraction and classification
  const [textResult] = await Promise.all([
    SmartDetectionModule.extractTextFromImage(captureData.base64),
    // classificationResult already completed above
  ]);
  
  return {
    text: textResult,
    classification: classificationResult,
    timestamp: captureData.timestamp
  };
};
```

**Performance Comparison:**
- **Native HTTP Bridge**: 366ms responses ✅
- **React Native fetch**: 34+ second hangs ❌
- **Native Kotlin HTTP**: ~300ms responses ✅

## 📊 Pattern 6: Adding New Metrics and Analytics

### Example: Add Performance Tracking

**Step 1: Extend Stats Interface**
```typescript
// In app/screen-capture.tsx
interface CaptureStats {
  // Existing stats...
  
  // New performance metrics
  averageProcessingTime: number;
  peakMemoryUsage: number;
  errorRate: number;
  featureUsageStats: {
    nightMode: number;
    batchMode: number;
    enhancementUsed: number;
  };
}
```

**Step 2: Track Metrics During Processing**
```typescript
const processCapture = async (captureData: CaptureData) => {
  const startTime = performance.now();
  
  try {
    const result = await SmartDetectionModule.extractTextFromImage(captureData.base64);
    
    // Update success metrics
    const processingTime = performance.now() - startTime;
    setStats(prev => ({
      ...prev,
      averageProcessingTime: (prev.averageProcessingTime + processingTime) / 2,
      successfulClassifications: prev.successfulClassifications + 1
    }));
    
    return result;
  } catch (error) {
    // Update error metrics
    setStats(prev => ({
      ...prev,
      errorRate: (prev.errorRate * prev.totalCaptures + 1) / (prev.totalCaptures + 1)
    }));
    throw error;
  }
};
```

## 🔧 Development Workflow for New Features

### 1. **Planning Phase**
```bash
# 1. Identify which layer needs changes
# 2. Check existing patterns in codebase
# 3. Plan data flow between layers
```

### 2. **Development Phase**
```bash
# 1. Start with React Native UI changes
# 2. Add native Android methods if needed
# 3. Test with simple logging first
# 4. Add backend integration last
```

### 3. **Testing Phase**
```bash
# Build and test
./quick-build.sh

# Monitor logs for your feature
adb logcat | grep "YourFeatureTag"

# Test different scenarios
./test-your-new-feature.sh
```

### 4. **Integration Phase**
```bash
# Test with existing features
# Check performance impact
# Verify smart capture still works
# Test on different apps
```

## 🎯 Quick Feature Ideas You Can Implement

### Easy (UI Only):
- Dark/Light theme toggle
- Different capture intervals
- Export captured data
- Statistics dashboard

### Medium (UI + Native HTTP):
- **Real-time content filtering** (using native HTTP bridge)
- **Multi-endpoint analysis** (sentiment, toxicity, etc.)
- **Custom backend integrations** (your own APIs)
- **A/B testing different models** (switch endpoints dynamically)

### Medium (UI + Native):
- Image filters before OCR
- Different text extraction modes
- Custom app monitoring lists
- Capture scheduling

### Advanced (Full Pipeline):
- Real-time content analysis
- Multi-language OCR
- Custom ML models
- Advanced app behavior detection

### Advanced (Native HTTP + Backend):
- **Distributed processing** (multiple backend servers)
- **Real-time model switching** (GPT-4, Claude, local models)
- **Custom training data collection** (send samples to your training pipeline)
- **Multi-modal analysis** (text + image + context)

## 📝 Code Templates

I've created reusable templates for common patterns. Just copy and modify:

- **New UI Control Template**: See Pattern 1 above
- **New Native Method Template**: See Pattern 2 above  
- **Native HTTP Bridge Template**: See Pattern 2.5 above ⚡
- **New Detection Logic Template**: See Pattern 3 above
- **New Processing Mode Template**: See Pattern 4 above
- **Backend Integration Template**: See Pattern 5 above

### 🚀 Native HTTP Bridge Quick Start Template

```typescript
// 1. Import the bridge
const { nativeHttpClient } = await import('../services/nativeHttpBridge');

// 2. Make your API call
const response = await nativeHttpClient.post('YOUR_ENDPOINT', {
  body: { your: 'data' },
  timeout: 2000, // Fast timeout
});

// 3. Handle the response
if (response.success) {
  const data = JSON.parse(response.data!);
  console.log(`✅ Success in ${response.responseTime}ms:`, data);
} else {
  console.error(`❌ Error: ${response.error}`);
}
```

**Performance Guarantee**: 366ms responses vs 34+ second hangs with React Native fetch!

This guide gives you everything you need to extend the screen capture system! 🚀