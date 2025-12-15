# Visual Similarity Detection System Spec

## Overview
Implement a fast, accurate visual similarity detection system to prevent redundant analysis of identical or nearly identical content frames, solving the "too aggressive" harmful content detection issue.

## Problem Statement
Current system re-analyzes the same content repeatedly, causing:
- Excessive API costs (OCR + LLM calls)
- Poor user experience (stuck in harmful content loops)
- Slow performance (800ms+ per frame analysis)
- False positives on already-scrolled content

## Solution Architecture

### Core Components

#### 1. Perceptual Hash Generator
**Purpose**: Generate fast, collision-resistant visual fingerprints
**Input**: Bitmap frame (720x1600)
**Output**: 64-bit perceptual hash
**Performance**: <5ms per frame

**Implementation Details**:
- Use difference hash (dHash) algorithm
- Resize image to 9x8 grayscale
- Compare adjacent pixels to create binary hash
- Convert to 64-bit long for storage

#### 2. Frame Buffer Manager
**Purpose**: Maintain rolling buffer of recent frame hashes
**Storage**: Circular buffer with timestamps
**Capacity**: 15 frames (configurable)
**Cleanup**: Auto-expire entries >30 seconds old

**Data Structure**:
```kotlin
data class FrameFingerprint(
    val hash: Long,
    val timestamp: Long,
    val isHarmful: Boolean = false,
    val confidence: Float = 0f
)
```

#### 3. Similarity Analyzer
**Purpose**: Compare current frame against recent frames
**Algorithm**: Hamming distance calculation
**Threshold**: 90% similarity (6 bits difference max)
**Performance**: <1ms per comparison

#### 4. Motion Detection Engine
**Purpose**: Detect significant visual changes between frames
**Method**: Pixel difference calculation on downscaled images
**Threshold**: 25% pixel change indicates new content
**Fallback**: Force analysis if motion detected regardless of similarity

#### 5. Smart Decision Engine
**Purpose**: Decide whether to proceed with expensive analysis
**Logic**: Multi-factor decision making
**Factors**: Visual similarity, motion detection, time elapsed, harmful content history

## Implementation Phases

### Phase 1: Core Hash Generation
**Duration**: 2-3 hours
**Deliverables**:
- [ ] Perceptual hash utility class
- [ ] Bitmap preprocessing functions
- [ ] Hash generation performance tests
- [ ] Unit tests for hash consistency

**Files to Create/Modify**:
- `android/app/src/main/java/com/allot/utils/PerceptualHash.kt`
- `android/app/src/main/java/com/allot/utils/ImageUtils.kt`

### Phase 2: Frame Buffer System
**Duration**: 2-3 hours
**Deliverables**:
- [ ] Circular buffer implementation
- [ ] Thread-safe operations
- [ ] Automatic cleanup mechanisms
- [ ] Memory usage optimization

**Files to Create/Modify**:
- `android/app/src/main/java/com/allot/detection/FrameBuffer.kt`
- `android/app/src/main/java/com/allot/detection/FrameFingerprint.kt`

### Phase 3: Similarity Analysis
**Duration**: 2-3 hours
**Deliverables**:
- [ ] Hamming distance calculator
- [ ] Similarity threshold configuration
- [ ] Performance benchmarking
- [ ] False positive/negative testing

**Files to Create/Modify**:
- `android/app/src/main/java/com/allot/detection/SimilarityAnalyzer.kt`

### Phase 4: Motion Detection
**Duration**: 2-3 hours
**Deliverables**:
- [ ] Frame difference calculation
- [ ] Motion threshold tuning
- [ ] Performance optimization
- [ ] Integration with similarity system

**Files to Create/Modify**:
- `android/app/src/main/java/com/allot/detection/MotionDetector.kt`

### Phase 5: Smart Decision Engine
**Duration**: 3-4 hours
**Deliverables**:
- [ ] Multi-factor decision logic
- [ ] Configurable thresholds
- [ ] Logging and debugging
- [ ] Performance metrics

**Files to Create/Modify**:
- `android/app/src/main/java/com/allot/detection/SmartDecisionEngine.kt`

### Phase 6: Service Integration
**Duration**: 2-3 hours
**Deliverables**:
- [ ] Integration with ScreenCaptureService
- [ ] Backward compatibility
- [ ] Configuration management
- [ ] Error handling

**Files to Modify**:
- `android/app/src/main/java/com/allot/ScreenCaptureService.kt`

### Phase 7: Testing & Optimization
**Duration**: 3-4 hours
**Deliverables**:
- [ ] End-to-end testing
- [ ] Performance benchmarking
- [ ] Memory usage analysis
- [ ] Real-world scenario testing

## Technical Specifications

### Performance Requirements
- Hash generation: <5ms per frame
- Similarity comparison: <1ms per check
- Memory usage: <10MB for frame buffer
- CPU usage: <5% additional overhead

### Accuracy Requirements
- False positive rate: <2% (incorrectly skipping new content)
- False negative rate: <5% (incorrectly re-analyzing same content)
- Detection accuracy: >95% for identical content
- Motion detection: >90% accuracy for significant changes

### Configuration Parameters
```kotlin
object SimilarityConfig {
    const val BUFFER_SIZE = 15
    const val SIMILARITY_THRESHOLD = 0.90f
    const val MOTION_THRESHOLD = 0.25f
    const val EXPIRY_TIME_MS = 30_000L
    const val HASH_SIZE = 64
    const val PREVIEW_SIZE = 64 // 8x8 for hash generation
}
```

## Integration Points

### With Existing System
1. **ScreenCaptureService**: Add similarity check before expensive analysis
2. **Backend Communication**: Reduce API calls by 80-90%
3. **Harmful Content Handler**: Enhanced with similarity context
4. **Performance Monitoring**: Add metrics for similarity system

### Data Flow
```
Frame Captured → Generate Hash → Check Similarity → Motion Detection → Decision Engine → Analysis/Skip
```

## Success Metrics

### Performance Improvements
- 80-90% reduction in API calls
- 70-80% reduction in processing time
- 60-70% reduction in costs
- <100ms decision time per frame

### Accuracy Improvements
- Eliminate harmful content loops
- Reduce false positives by 90%
- Maintain detection accuracy >95%
- Improve user experience significantly

## Risk Mitigation

### Technical Risks
- **Hash Collisions**: Use 64-bit hashes, monitor collision rates
- **Memory Leaks**: Implement proper cleanup, memory monitoring
- **Performance Degradation**: Continuous benchmarking, optimization
- **False Negatives**: Configurable thresholds, fallback mechanisms

### Fallback Strategies
- Time-based analysis (force analysis every 30 seconds)
- Motion-triggered analysis (bypass similarity on high motion)
- Manual override (accessibility service integration)
- Gradual rollout (A/B testing with current system)

## Testing Strategy

### Unit Tests
- Hash generation consistency
- Similarity calculation accuracy
- Buffer management operations
- Motion detection algorithms

### Integration Tests
- End-to-end similarity detection
- Performance under load
- Memory usage patterns
- Real-world content scenarios

### Performance Tests
- Hash generation speed
- Memory consumption
- CPU usage impact
- Battery life impact

## Deployment Plan

### Phase 1: Development Environment
- Implement core components
- Unit testing
- Basic integration testing

### Phase 2: Staging Environment
- Full system integration
- Performance testing
- Real content testing

### Phase 3: Production Rollout
- Gradual rollout (10% → 50% → 100%)
- Monitoring and metrics
- Performance optimization
- Bug fixes and improvements

## Monitoring & Metrics

### Key Performance Indicators
- API call reduction percentage
- Processing time improvement
- Memory usage
- CPU usage
- Battery impact
- User experience metrics

### Logging Strategy
- Hash generation performance
- Similarity match rates
- Motion detection accuracy
- Decision engine choices
- Error rates and types

## Future Enhancements

### Phase 2 Features
- Machine learning similarity models
- Content-aware zone detection
- Adaptive threshold adjustment
- Cross-app similarity detection

### Advanced Features
- Semantic similarity (beyond visual)
- Predictive content analysis
- User behavior integration
- Cloud-based similarity matching

---

## Implementation Checklist

### Prerequisites
- [ ] Review current ScreenCaptureService architecture
- [ ] Set up development environment
- [ ] Create test content dataset
- [ ] Define performance benchmarks

### Development Phases
- [ ] Phase 1: Core Hash Generation
- [ ] Phase 2: Frame Buffer System
- [ ] Phase 3: Similarity Analysis
- [ ] Phase 4: Motion Detection
- [ ] Phase 5: Smart Decision Engine
- [ ] Phase 6: Service Integration
- [ ] Phase 7: Testing & Optimization

### Quality Assurance
- [ ] Unit test coverage >90%
- [ ] Integration test suite
- [ ] Performance benchmarking
- [ ] Memory leak testing
- [ ] Real-world scenario testing

### Documentation
- [ ] API documentation
- [ ] Configuration guide
- [ ] Troubleshooting guide
- [ ] Performance tuning guide

This spec provides a comprehensive roadmap for implementing the visual similarity detection system. Each phase is designed to be manageable and testable independently, allowing for iterative development and validation.