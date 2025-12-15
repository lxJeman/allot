# Smart Content Detection System Design

## Overview

The Smart Content Detection System is an enhanced harmful content detection solution that eliminates the "sticky" detection problem through intelligent visual similarity detection, temporal awareness, and adaptive analysis. The system reduces API calls by 80-90% while maintaining high detection accuracy and providing a smooth user experience.

## Architecture

### High-Level Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  Screen Capture │───▶│ Smart Detection  │───▶│ Action Handler  │
│    Service      │    │     Engine       │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │ Backend Services │
                    │ (OCR + LLM)      │
                    └──────────────────┘
```

### Smart Detection Engine Components

```
┌─────────────────────────────────────────────────────────────┐
│                Smart Detection Engine                        │
├─────────────────┬─────────────────┬─────────────────────────┤
│ Frame           │ Similarity      │ Motion Detection        │
│ Fingerprinting  │ Analyzer        │ Engine                  │
├─────────────────┼─────────────────┼─────────────────────────┤
│ Pre-filter      │ State Machine   │ Performance Monitor     │
│ Pipeline        │                 │                         │
└─────────────────┴─────────────────┴─────────────────────────┘
```

## Components and Interfaces

### 1. Frame Fingerprinting Service

**Purpose**: Generate lightweight visual fingerprints for fast similarity comparison

**Interface**:
```kotlin
interface FrameFingerprintService {
    fun generateFingerprint(bitmap: Bitmap): FrameFingerprint
    fun calculateSimilarity(fp1: FrameFingerprint, fp2: FrameFingerprint): Float
    fun isSignificantChange(current: FrameFingerprint, previous: FrameFingerprint): Boolean
}

data class FrameFingerprint(
    val hash: Long,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val textDensity: Float = 0f
)
```

**Implementation Details**:
- Uses difference hash (dHash) algorithm for perceptual hashing
- Resizes images to 8x8 grayscale for consistent 64-bit hashes
- Calculates Hamming distance for similarity comparison
- Processes frames in under 5ms using optimized bitmap operations

### 2. Similarity Analyzer

**Purpose**: Compare current frame against recent frames to detect duplicate content

**Interface**:
```kotlin
interface SimilarityAnalyzer {
    fun addFrame(fingerprint: FrameFingerprint)
    fun findSimilarFrames(fingerprint: FrameFingerprint, threshold: Float): List<FrameMatch>
    fun clearOldFrames(maxAge: Long)
    fun getBufferStats(): BufferStats
}

data class FrameMatch(
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val ageMs: Long
)

data class BufferStats(
    val totalFrames: Int,
    val oldestFrameAge: Long,
    val averageSimilarity: Float,
    val memoryUsage: Long
)
```

**Implementation Details**:
- Maintains circular buffer of last 15 frame fingerprints
- Uses efficient bit manipulation for Hamming distance calculation
- Automatically expires frames older than 30 seconds
- Thread-safe operations for concurrent access

### 3. Motion Detection Engine

**Purpose**: Detect significant visual changes between consecutive frames

**Interface**:
```kotlin
interface MotionDetectionEngine {
    fun detectMotion(current: Bitmap, previous: Bitmap): MotionResult
    fun calculatePixelDifference(current: Bitmap, previous: Bitmap): Float
    fun isSignificantMotion(difference: Float): Boolean
}

data class MotionResult(
    val pixelDifference: Float,
    val isSignificantMotion: Boolean,
    val motionRegions: List<Rect>,
    val processingTimeMs: Long
)
```

**Implementation Details**:
- Downscales images to 64x64 for fast pixel comparison
- Uses RGB difference calculation with optimized algorithms
- Identifies motion regions for zone-based analysis
- Completes processing in under 2ms per frame comparison

### 4. Content State Machine

**Purpose**: Track detection state and adapt behavior based on context

**Interface**:
```kotlin
interface ContentStateMachine {
    fun processFrame(context: FrameContext): StateDecision
    fun getCurrentState(): DetectionState
    fun forceStateTransition(newState: DetectionState, reason: String)
    fun getStateHistory(): List<StateTransition>
}

enum class DetectionState {
    SCANNING,           // Normal monitoring
    HARMFUL_DETECTED,   // Just found harmful content
    SCROLLING_AWAY,     // User moving past harmful content
    COOLDOWN,          // Waiting before next analysis
    SAFE_CONTENT       // Confirmed safe content area
}

data class StateDecision(
    val shouldAnalyze: Boolean,
    val analysisFrequency: Float,
    val confidenceThreshold: Float,
    val reason: String
)

data class FrameContext(
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val motionDetected: Boolean,
    val timeSinceLastAnalysis: Long,
    val timeSinceLastHarmful: Long
)
```

**Implementation Details**:
- Implements finite state machine with configurable transitions
- Adjusts analysis frequency based on current state
- Maintains state history for debugging and optimization
- Provides clear reasoning for all decisions

### 5. Pre-filter Pipeline

**Purpose**: Multi-stage filtering to reduce expensive analysis calls

**Interface**:
```kotlin
interface PreFilterPipeline {
    fun shouldAnalyze(frame: CapturedFrame): FilterDecision
    fun addFilterStage(stage: FilterStage)
    fun getFilterStats(): FilterStats
}

interface FilterStage {
    fun filter(frame: CapturedFrame, context: FilterContext): FilterResult
    val name: String
    val priority: Int
}

data class FilterDecision(
    val shouldProceed: Boolean,
    val reason: String,
    val stageResults: List<FilterResult>,
    val processingTimeMs: Long
)

data class FilterResult(
    val stageName: String,
    val passed: Boolean,
    val confidence: Float,
    val processingTimeMs: Long,
    val metadata: Map<String, Any>
)
```

**Implementation Details**:
- Configurable pipeline with pluggable filter stages
- Early termination when any stage fails
- Detailed logging of filter decisions
- Performance monitoring for each stage

### 6. Smart Detection Coordinator

**Purpose**: Main orchestrator that coordinates all components

**Interface**:
```kotlin
interface SmartDetectionCoordinator {
    fun processFrame(frame: CapturedFrame): DetectionDecision
    fun updateConfiguration(config: DetectionConfig)
    fun getPerformanceMetrics(): PerformanceMetrics
    fun enableDebugMode(enabled: Boolean)
}

data class DetectionDecision(
    val shouldAnalyze: Boolean,
    val skipReason: String?,
    val analysisType: AnalysisType,
    val estimatedCost: Float,
    val processingTimeMs: Long
)

enum class AnalysisType {
    FULL_ANALYSIS,      // OCR + LLM
    FAST_ANALYSIS,      // On-device only
    SKIP_ANALYSIS,      // No analysis needed
    CACHED_RESULT       // Use previous result
}
```

## Data Models

### Configuration Model

```kotlin
data class DetectionConfig(
    // Similarity Detection
    val similarityThreshold: Float = 0.90f,
    val bufferSize: Int = 15,
    val frameExpiryMs: Long = 30_000L,
    
    // Motion Detection
    val motionThreshold: Float = 0.25f,
    val motionRegionSize: Int = 64,
    
    // State Machine
    val cooldownPeriodMs: Long = 3_000L,
    val extendedCooldownMs: Long = 2_000L,
    val safeContentThresholdMs: Long = 10_000L,
    
    // Performance
    val maxProcessingTimeMs: Long = 5L,
    val maxMemoryUsageMB: Int = 10,
    val apiCallReductionTarget: Float = 0.80f,
    
    // Debugging
    val enableVerboseLogging: Boolean = false,
    val enablePerformanceMetrics: Boolean = true,
    val logStateTransitions: Boolean = true
)
```

### Performance Metrics Model

```kotlin
data class PerformanceMetrics(
    // API Call Reduction
    val totalFramesProcessed: Long,
    val framesAnalyzed: Long,
    val framesSkipped: Long,
    val apiCallReduction: Float,
    
    // Processing Performance
    val averageProcessingTimeMs: Float,
    val maxProcessingTimeMs: Long,
    val fingerprintGenerationTimeMs: Float,
    val similarityCheckTimeMs: Float,
    
    // Memory Usage
    val currentMemoryUsageMB: Float,
    val maxMemoryUsageMB: Float,
    val bufferUtilization: Float,
    
    // Detection Accuracy
    val similarityMatchRate: Float,
    val motionDetectionAccuracy: Float,
    val falsePositiveRate: Float,
    val falseNegativeRate: Float,
    
    // State Machine
    val stateTransitions: Map<DetectionState, Int>,
    val averageStateTime: Map<DetectionState, Long>,
    val cooldownEffectiveness: Float
)
```

## Error Handling

### Error Categories

1. **Performance Errors**
   - Processing time exceeds thresholds
   - Memory usage exceeds limits
   - API rate limiting

2. **Data Errors**
   - Invalid frame data
   - Corrupted fingerprints
   - Missing configuration

3. **Integration Errors**
   - Backend communication failures
   - State synchronization issues
   - Component initialization failures

### Error Handling Strategy

```kotlin
sealed class DetectionError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class PerformanceError(message: String) : DetectionError(message)
    class DataError(message: String, cause: Throwable? = null) : DetectionError(message, cause)
    class IntegrationError(message: String, cause: Throwable? = null) : DetectionError(message, cause)
}

interface ErrorHandler {
    fun handleError(error: DetectionError): ErrorRecoveryAction
    fun reportError(error: DetectionError, context: Map<String, Any>)
}

enum class ErrorRecoveryAction {
    RETRY,              // Retry the operation
    FALLBACK,           // Use fallback mechanism
    SKIP,               // Skip current frame
    RESET,              // Reset component state
    ESCALATE            // Report to higher level
}
```

## Testing Strategy

### Unit Testing

1. **Frame Fingerprinting**
   - Hash generation consistency
   - Similarity calculation accuracy
   - Performance benchmarks

2. **Motion Detection**
   - Pixel difference calculation
   - Motion threshold accuracy
   - Region detection precision

3. **State Machine**
   - State transition logic
   - Decision making accuracy
   - Configuration handling

### Integration Testing

1. **End-to-End Flow**
   - Complete detection pipeline
   - Performance under load
   - Memory usage patterns

2. **Component Integration**
   - Service communication
   - Error propagation
   - Configuration updates

### Performance Testing

1. **Throughput Testing**
   - Frames per second processing
   - Concurrent frame handling
   - Memory usage scaling

2. **Latency Testing**
   - Processing time distribution
   - Worst-case scenarios
   - Resource contention

### Real-World Testing

1. **Content Scenarios**
   - Various social media apps
   - Different content types
   - User interaction patterns

2. **Device Testing**
   - Different Android versions
   - Various device specifications
   - Battery impact assessment

### 7. Local Text Extraction System

**Purpose**: Replace Google Vision API with on-device text extraction for faster, more private, and cost-effective text detection

**Interface**:
```kotlin
interface LocalTextExtractor {
    fun extractText(bitmap: Bitmap): TextExtractionResult
    fun extractTextWithRegions(bitmap: Bitmap): List<TextRegion>
    fun calculateTextDensity(bitmap: Bitmap): Float
    fun isTextPresent(bitmap: Bitmap, threshold: Float = 0.1f): Boolean
}

data class TextExtractionResult(
    val extractedText: String,
    val confidence: Float,
    val textRegions: List<TextRegion>,
    val textDensity: Float,
    val processingTimeMs: Long
)

data class TextRegion(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val textType: TextType
)

enum class TextType {
    CONTENT_TEXT,    // Main content (posts, comments, etc.)
    UI_ELEMENT,      // Interface elements (buttons, labels)
    METADATA,        // Timestamps, usernames, etc.
    UNKNOWN
}
```

**Implementation Details**:
- Uses Android ML Kit Text Recognition API for on-device processing
- Implements intelligent region-of-interest detection to focus on content areas
- Provides text type classification to filter out UI noise
- Includes confidence scoring and quality validation
- Optimized preprocessing pipeline for social media content patterns
- Caching system to avoid re-processing similar frames

**Performance Targets**:
- Text extraction: <50ms per frame (vs 200-500ms for Google Vision API calls)
- Accuracy: ≥95% match with Google Vision API for social media content
- Memory usage: <5MB additional overhead
- No network dependency or API costs

**Benefits**:
- **Speed**: 4-10x faster than API calls (no network latency)
- **Privacy**: All processing happens on-device
- **Cost**: Zero API costs after implementation
- **Reliability**: No dependency on network connectivity
- **Accuracy**: Tuned specifically for social media content patterns

## Implementation Considerations

### Performance Optimizations

1. **Memory Management**
   - Object pooling for frequent allocations
   - Efficient bitmap handling
   - Garbage collection optimization

2. **CPU Optimization**
   - Native code for critical paths
   - Vectorized operations where possible
   - Background thread processing

3. **I/O Optimization**
   - Asynchronous operations
   - Batch processing
   - Connection pooling

### Scalability Considerations

1. **Configuration Management**
   - Runtime configuration updates
   - A/B testing support
   - Feature flags

2. **Monitoring Integration**
   - Metrics collection
   - Performance dashboards
   - Alert systems

3. **Deployment Strategy**
   - Gradual rollout
   - Rollback mechanisms
   - Version compatibility

This design provides a comprehensive foundation for implementing the Smart Content Detection System that will solve your "sticky" detection problem while dramatically improving performance and reducing costs.