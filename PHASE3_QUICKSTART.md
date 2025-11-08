# 🧠 Phase 3 — AI Detection Pipeline Quick Start

## 🎯 Overview

Phase 3 implements a complete text-based harmful content detection pipeline using:
- **Google Vision API** for OCR text extraction
- **Groq API** with `llama-3.3-70b-versatile` (GPT-OSS 20B) for classification
- **Rust backend** with detailed logging and benchmarks
- **React Native service** with intelligent caching

---

## 🚀 Quick Start

### 1. Install Dependencies

```bash
# Install React Native dependency for SHA-256 hashing
npm install react-native-sha256

# Build Rust backend
cd rust-backend
cargo build --release
```

### 2. Start Backend

```bash
cd rust-backend
cargo run
```

Expected output:
```
🚀 Starting Allot AI Detection Backend (Phase 3 - Ver 1.0)
🧠 Model: llama-3.3-70b-versatile (GPT-OSS 20B)
👁️  OCR: Google Vision API
🌐 Server listening on http://0.0.0.0:3000
📱 Device should connect to http://192.168.100.47:3000
📡 Ready to receive screen captures
```

### 3. Test Backend

```bash
curl http://192.168.100.47:3000/health
```

Expected response:
```json
{
  "status": "healthy",
  "message": "Allot AI Detection Backend is running",
  "uptime": "Running"
}
```

### 4. Integrate with React Native

```typescript
import { aiDetectionService } from '@/services/aiDetectionService';

// Configure detection
aiDetectionService.updateConfig({
  blockList: ['political', 'toxic', 'clickbait'],
  minConfidence: 0.80,
  enableCache: true,
  cacheExpiryHours: 24,
});

// Detect harmful content
const result = await aiDetectionService.detectHarmfulContent(base64Image);

console.log('Category:', result.category);
console.log('Confidence:', result.confidence);
console.log('Action:', result.action);
console.log('Detected Text:', result.detectedText);

// Check if should block
if (result.harmful && result.action === 'blur') {
  // Trigger blur overlay
  console.log('🚫 Content blocked!');
}

// Log telemetry
await aiDetectionService.logTelemetry(result, 'blocked');

// Get statistics
const stats = aiDetectionService.getStats();
console.log('Cache hit rate:', stats.cacheHitRate);
console.log('Avg processing time:', stats.avgProcessingTime);
```

---

## 📊 Pipeline Flow

```
1. Screen Capture (Base64)
   ↓
2. [Client] Google Vision API → Extract Text (OCR)
   ↓
3. [Client] Normalize Text → Generate SHA-256 Hash
   ↓
4. [Client] Check Cache → If Hit: Return Cached Result
   ↓
5. [Client] Send to Backend → POST /analyze
   ↓
6. [Backend] Google Vision API → Extract Text (OCR)
   ↓
7. [Backend] Groq API (llama-3.3-70b-versatile) → Classify
   ↓
8. [Backend] Return: Category + Confidence + Action
   ↓
9. [Client] Apply BlockList Filter
   ↓
10. [Client] Trigger Action: Blur / Scroll / Allow
    ↓
11. [Client] Log Telemetry
```

---

## 🔧 Configuration

### Backend Configuration

Edit `rust-backend/src/main.rs`:

```rust
struct Config {
    google_vision_api_key: String,
    groq_api_key: String,
    groq_model: String,
}

impl Config {
    fn from_env() -> Self {
        Self {
            google_vision_api_key: "YOUR_GOOGLE_API_KEY".to_string(),
            groq_api_key: "YOUR_GROQ_API_KEY".to_string(),
            groq_model: "llama-3.3-70b-versatile".to_string(),
        }
    }
}
```

### Client Configuration

```typescript
aiDetectionService.updateConfig({
  blockList: ['political', 'toxic', 'clickbait', 'nsfw'],
  minConfidence: 0.75,  // Lower = more sensitive
  enableCache: true,
  cacheExpiryHours: 24,
});
```

---

## 📈 Benchmark Logging

### Backend Logs

```
📸 [abc-123] Received screen capture: 720x1600
👁️  [abc-123] OCR complete: 245 chars extracted (850ms)
🔄 [abc-123] Text normalized: 245 -> 198 chars
🔑 [abc-123] Text hash: a3f5d8e2c1b4...
🧠 [abc-123] Classification complete: toxic (92.00% confidence) (1250ms)
✅ [abc-123] Analysis complete: toxic | Action: blur | Total: 2150ms (OCR: 850ms, LLM: 1250ms)
```

### Client Logs

```
🎯 [AI Detection] Starting pipeline...
👁️  [AI Detection] OCR complete: 245 chars (850ms)
🔄 [AI Detection] Text normalized: 245 -> 198 chars
❌ [AI Detection] Cache miss (1/1)
🧠 [AI Detection] Classification: toxic (0.92) - blur
🚫 [AI Detection] Content blocked: toxic
✅ [AI Detection] Pipeline complete: 2150ms (OCR: 850ms, LLM: 1250ms)
```

---

## 🧪 Testing

### Test with Sample Image

```typescript
// Capture screen
const base64Image = await ScreenCaptureModule.captureScreen();

// Detect content
const result = await aiDetectionService.detectHarmfulContent(base64Image);

console.log('Result:', result);
```

### Expected Response

```json
{
  "id": "abc-123-def-456",
  "category": "toxic",
  "confidence": 0.92,
  "harmful": true,
  "action": "blur",
  "detectedText": "This is some toxic content...",
  "riskFactors": ["offensive_language", "personal_attacks"],
  "recommendation": "Content blocked due to toxic language",
  "benchmark": {
    "ocrTimeMs": 850,
    "classificationTimeMs": 1250,
    "totalTimeMs": 2150,
    "textLength": 198,
    "cached": false
  },
  "timestamp": 1699459200000
}
```

---

## 🎯 Categories

The AI classifies content into these categories:

- **safe_content** - Normal, harmless content (action: `continue`)
- **political** - Political discussions, election content (action: `scroll`)
- **toxic** - Hate speech, harassment, offensive language (action: `blur`)
- **clickbait** - Sensational headlines, engagement bait (action: `scroll`)
- **advertisement** - Commercial content (action: `continue`)

---

## 🔒 Security Notes

1. **API Keys** - Store in environment variables in production
2. **Rate Limiting** - Implement rate limiting on backend
3. **Cache Security** - Cache is in-memory only (cleared on app restart)
4. **Privacy** - Images are not stored, only processed in real-time

---

## 🚀 Performance Optimization

### Current Benchmarks

- **OCR Time**: ~800-1000ms
- **Classification Time**: ~1000-1500ms
- **Total Time**: ~2000-2500ms
- **Cache Hit Rate**: Varies (typically 20-40% after warmup)

### Optimization Tips

1. **Enable Caching** - Reduces redundant API calls
2. **Adjust Capture Interval** - Balance between real-time and performance
3. **Batch Processing** - Process multiple frames together (future)
4. **Edge Deployment** - Deploy backend closer to users

---

## 🐛 Troubleshooting

### Backend won't start

```bash
# Check Rust installation
rustc --version

# Update dependencies
cd rust-backend
cargo update
cargo build
```

### API errors

```bash
# Check API keys are valid
curl "https://vision.googleapis.com/v1/images:annotate?key=YOUR_KEY"

# Check Groq API
curl -H "Authorization: Bearer YOUR_KEY" https://api.groq.com/openai/v1/models
```

### Cache not working

```typescript
// Clear cache and restart
aiDetectionService.clearCache();

// Check stats
console.log(aiDetectionService.getStats());
```

---

## 📚 Next Steps

1. **Integrate with UI** - Connect detection results to blur overlay
2. **Add Auto-Scroll** - Implement accessibility service for scrolling
3. **Optimize Performance** - Monitor benchmarks and optimize slow paths
4. **Add Ver 2.0 Features** - Visual-only detection (objects, scenes)

---

## 🎉 Success Criteria

✅ Backend starts and responds to health checks
✅ Client can send images and receive classifications
✅ Logging shows detailed benchmarks
✅ Cache reduces redundant API calls
✅ Detection results are accurate and actionable

**Phase 3 is complete when all criteria are met!** 🚀
