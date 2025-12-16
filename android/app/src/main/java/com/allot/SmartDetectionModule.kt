package com.allot

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.allot.detection.*
import com.facebook.react.bridge.*
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream

/**
 * SmartDetectionModule - React Native bridge for Smart Content Detection System
 * 
 * Exposes the enhanced motion analysis and threshold evaluation functionality.
 */
class SmartDetectionModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "SmartDetectionModule"
    }

    // Core detection components
    private val similarityAnalyzer by lazy { SimilarityAnalyzer() }
    private val performanceMonitor by lazy { PerformanceMonitor() }
    
    // Motion detection components with enhanced threshold evaluation
    private val motionEngine by lazy { MotionDetectionEngine() }
    private val motionThresholdEvaluator by lazy { MotionThresholdEvaluator() }
    private val motionAnalyzer by lazy { MotionAnalyzer(motionEngine, motionThresholdEvaluator) }
    
    // State machine components
    private val stateMachine by lazy { ContentStateMachine() }
    
    // Pre-filter pipeline components
    private val preFilterPipeline by lazy { PreFilterPipeline() }
    
    // Local text extraction components
    private val localTextExtractor by lazy { LocalTextExtractor() }
    
    // Coroutine scope for async operations
    private val moduleScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun getName(): String {
        return "SmartDetectionModule"
    }

    /**
     * Analyze motion with advanced threshold evaluation
     */
    @ReactMethod
    fun analyzeMotionAdvanced(
        base64Image1: String, 
        base64Image2: String, 
        contentType: String?, 
        promise: Promise
    ) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("advanced_motion_analysis")
            
            try {
                val bitmap1 = base64ToBitmap(base64Image1)
                val bitmap2 = base64ToBitmap(base64Image2)
                
                // Generate fingerprint for current frame
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap1,
                    timestamp = System.currentTimeMillis()
                )
                
                // Parse content type
                val parsedContentType = when (contentType?.uppercase()) {
                    "SOCIAL_MEDIA" -> MotionThresholdEvaluator.ContentType.SOCIAL_MEDIA
                    "VIDEO_CONTENT" -> MotionThresholdEvaluator.ContentType.VIDEO_CONTENT
                    "STATIC_CONTENT" -> MotionThresholdEvaluator.ContentType.STATIC_CONTENT
                    else -> MotionThresholdEvaluator.ContentType.UNKNOWN
                }
                
                // Perform advanced motion analysis
                val motionAnalysis = motionAnalyzer.analyzeMotion(
                    currentBitmap = bitmap1,
                    previousBitmap = bitmap2,
                    currentFingerprint = fingerprint,
                    contentType = parsedContentType
                )
                
                bitmap1.recycle()
                bitmap2.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    // Basic motion result
                    val motionResultMap = Arguments.createMap().apply {
                        putDouble("pixelDifference", motionAnalysis.currentMotion.pixelDifference.toDouble())
                        putBoolean("isSignificantMotion", motionAnalysis.currentMotion.isSignificantMotion)
                        putDouble("processingTimeMs", motionAnalysis.currentMotion.processingTimeMs.toDouble())
                        putInt("motionRegionsCount", motionAnalysis.currentMotion.motionRegions.size)
                    }
                    putMap("motionResult", motionResultMap)
                    
                    // Threshold evaluation
                    val thresholdEvalMap = Arguments.createMap().apply {
                        putDouble("rawMotionValue", motionAnalysis.thresholdEvaluation.rawMotionValue.toDouble())
                        putDouble("adjustedThreshold", motionAnalysis.thresholdEvaluation.adjustedThreshold.toDouble())
                        putString("motionSignificance", motionAnalysis.thresholdEvaluation.motionSignificance.name)
                        putString("contentType", motionAnalysis.thresholdEvaluation.contentType.name)
                        putDouble("motionVelocity", motionAnalysis.thresholdEvaluation.motionVelocity.toDouble())
                        putBoolean("isSignificantMotion", motionAnalysis.thresholdEvaluation.isSignificantMotion)
                        putDouble("confidenceScore", (motionAnalysis.thresholdEvaluation.confidenceScore * 100).toDouble())
                        putDouble("recommendedAnalysisFrequency", motionAnalysis.thresholdEvaluation.recommendedAnalysisFrequency.toDouble())
                        putBoolean("shouldForceAnalysis", motionAnalysis.thresholdEvaluation.shouldForceAnalysis)
                        putDouble("adaptiveAdjustment", motionAnalysis.thresholdEvaluation.adaptiveAdjustment.toDouble())
                    }
                    putMap("thresholdEvaluation", thresholdEvalMap)
                    
                    // Analysis recommendations
                    putDouble("motionVelocity", motionAnalysis.motionVelocity.toDouble())
                    putBoolean("isConsistentMotion", motionAnalysis.isConsistentMotion)
                    putBoolean("shouldForceAnalysis", motionAnalysis.shouldForceAnalysis)
                    putBoolean("shouldReduceFrequency", motionAnalysis.shouldReduceFrequency)
                    putString("analysisRecommendation", motionAnalysis.analysisRecommendation.name)
                    putDouble("adaptiveFrequency", motionAnalysis.adaptiveFrequency.toDouble())
                    putString("detectedContentType", motionAnalysis.contentType.name)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error in advanced motion analysis: ${e.message}", e)
                promise.reject("ADVANCED_MOTION_ERROR", e.message)
            }
        }
    }
    
    /**
     * Get enhanced motion statistics with threshold evaluation
     */
    @ReactMethod
    fun getEnhancedMotionStats(promise: Promise) {
        try {
            val enhancedStats = motionAnalyzer.getEnhancedMotionStats()
            
            val result = Arguments.createMap().apply {
                // Basic stats
                val basicStatsMap = Arguments.createMap().apply {
                    putDouble("totalFramesProcessed", enhancedStats.basicStats.totalFramesProcessed.toDouble())
                    putDouble("motionFramesDetected", enhancedStats.basicStats.motionFramesDetected.toDouble())
                    putDouble("motionDetectionRate", (enhancedStats.basicStats.motionDetectionRate * 100).toDouble())
                    putDouble("recentMotionRate", (enhancedStats.basicStats.recentMotionRate * 100).toDouble())
                    putDouble("averageProcessingTimeMs", enhancedStats.basicStats.averageProcessingTimeMs.toDouble())
                    putInt("historySize", enhancedStats.basicStats.historySize)
                }
                putMap("basicStats", basicStatsMap)
                
                // Evaluation stats
                val evaluationStatsMap = Arguments.createMap().apply {
                    putInt("totalEvaluations", enhancedStats.evaluationStats.totalEvaluations)
                    putString("detectedContentType", enhancedStats.evaluationStats.detectedContentType.name)
                    putDouble("contentTypeConfidence", (enhancedStats.evaluationStats.contentTypeConfidence * 100).toDouble())
                    putDouble("adaptiveAdjustment", enhancedStats.evaluationStats.adaptiveAdjustment.toDouble())
                    putDouble("averageMotionValue", enhancedStats.evaluationStats.averageMotionValue.toDouble())
                    putDouble("averageProcessingTime", enhancedStats.evaluationStats.averageProcessingTime.toDouble())
                    putInt("historySize", enhancedStats.evaluationStats.historySize)
                }
                putMap("evaluationStats", evaluationStatsMap)
                
                // Enhanced features
                putBoolean("adaptiveThresholdActive", enhancedStats.adaptiveThresholdActive)
                putBoolean("contentTypeDetected", enhancedStats.contentTypeDetected)
                putDouble("averageAdaptiveFrequency", enhancedStats.averageAdaptiveFrequency.toDouble())
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting enhanced motion stats: ${e.message}", e)
            promise.reject("ENHANCED_MOTION_STATS_ERROR", e.message)
        }
    }

    /**
     * Test basic motion detection between two images
     */
    @ReactMethod
    fun testMotionDetection(base64Image1: String, base64Image2: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("basic_motion_detection")
            
            try {
                val bitmap1 = base64ToBitmap(base64Image1)
                val bitmap2 = base64ToBitmap(base64Image2)
                
                val motionResult = motionEngine.detectMotion(bitmap1, bitmap2)
                
                bitmap1.recycle()
                bitmap2.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putDouble("pixelDifference", motionResult.pixelDifference.toDouble())
                    putBoolean("isSignificantMotion", motionResult.isSignificantMotion)
                    putDouble("processingTimeMs", motionResult.processingTimeMs.toDouble())
                    putInt("motionRegionsCount", motionResult.motionRegions.size)
                    
                    val regionsArray = Arguments.createArray()
                    motionResult.motionRegions.forEach { region ->
                        val regionMap = Arguments.createMap().apply {
                            putInt("left", region.left)
                            putInt("top", region.top)
                            putInt("right", region.right)
                            putInt("bottom", region.bottom)
                        }
                        regionsArray.pushMap(regionMap)
                    }
                    putArray("motionRegions", regionsArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error in basic motion detection: ${e.message}", e)
                promise.reject("BASIC_MOTION_ERROR", e.message)
            }
        }
    }
    
    /**
     * Generate hash for a base64 image
     */
    @ReactMethod
    fun generateHashFromBase64(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("generate_hash")
            
            try {
                // Decode base64 to bitmap
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate hash
                val (hash, processingTime) = PerceptualHashGenerator.generateHashWithTiming(bitmap)
                
                // Create fingerprint
                val fingerprint = FrameFingerprint(
                    hash = hash,
                    timestamp = System.currentTimeMillis(),
                    width = bitmap.width,
                    height = bitmap.height,
                    textDensity = 0f,
                    metadata = mapOf("source" to "react_native_test")
                )
                
                bitmap.recycle()
                timer.complete()
                
                // Return result
                val result = Arguments.createMap().apply {
                    putString("hash", hash.toString(16))
                    putDouble("processingTimeMs", processingTime.toDouble())
                    putString("fingerprint", fingerprint.toJson().toString())
                    putString("compactString", fingerprint.toCompactString())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error generating hash: ${e.message}", e)
                promise.reject("HASH_ERROR", e.message)
            }
        }
    }
    
    /**
     * Create a test image with specified color and pattern
     */
    @ReactMethod
    fun createTestImage(width: Int, height: Int, color: String, pattern: String?, promise: Promise) {
        moduleScope.launch {
            try {
                val bitmap = PerceptualHashGenerator.createTestBitmap(
                    width, 
                    height, 
                    when (pattern?.uppercase()) {
                        "GRADIENT" -> TestPattern.GRADIENT
                        "CHECKERBOARD" -> TestPattern.CHECKERBOARD
                        else -> TestPattern.SOLID
                    }
                )
                
                // Convert bitmap to base64
                val base64 = bitmapToBase64(bitmap)
                bitmap.recycle()
                
                val result = Arguments.createMap().apply {
                    putString("base64", base64)
                    putInt("width", width)
                    putInt("height", height)
                    putString("pattern", pattern ?: "SOLID")
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error creating test image: ${e.message}", e)
                promise.reject("CREATE_IMAGE_ERROR", e.message)
            }
        }
    }
    
    /**
     * Add frame to analyzer for similarity tracking
     */
    @ReactMethod
    fun addFrameToAnalyzer(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("add_frame_to_analyzer")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate fingerprint
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis()
                )
                
                // Add to similarity analyzer
                similarityAnalyzer.addFrame(fingerprint)
                
                bitmap.recycle()
                timer.complete()
                
                promise.resolve(true)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error adding frame to analyzer: ${e.message}", e)
                promise.reject("ADD_FRAME_ERROR", e.message)
            }
        }
    }
    
    /**
     * Analyze frame for similarity and processing decision
     */
    @ReactMethod
    fun analyzeFrame(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("analyze_frame")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate fingerprint
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis()
                )
                
                // Analyze with similarity analyzer
                val analysisResult = similarityAnalyzer.analyzeFrame(fingerprint)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putBoolean("shouldProcess", analysisResult.shouldProcess)
                    putString("reason", analysisResult.reason)
                    putDouble("confidence", analysisResult.confidence.toDouble())
                    putDouble("processingTimeMs", analysisResult.processingTimeMs.toDouble())
                    
                    analysisResult.mostSimilarMatch?.let { match ->
                        val matchMap = Arguments.createMap().apply {
                            putDouble("similarity", match.similarity.toDouble())
                            putString("hash", match.fingerprint.hash.toString(16))
                            putDouble("ageMs", (System.currentTimeMillis() - match.fingerprint.timestamp).toDouble())
                        }
                        putMap("mostSimilarMatch", matchMap)
                    }
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error analyzing frame: ${e.message}", e)
                promise.reject("ANALYZE_FRAME_ERROR", e.message)
            }
        }
    }
    
    /**
     * Find similar frames in buffer
     */
    @ReactMethod
    fun findSimilarFrames(base64Image: String, threshold: Double, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("find_similar_frames")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate fingerprint
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis()
                )
                
                // Find similar frames
                val similarFrames = similarityAnalyzer.findSimilarFrames(fingerprint, threshold.toFloat())
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putInt("matchCount", similarFrames.size)
                    putDouble("threshold", threshold)
                    
                    val matchesArray = Arguments.createArray()
                    similarFrames.forEach { match ->
                        val matchMap = Arguments.createMap().apply {
                            putDouble("similarity", match.similarity.toDouble())
                            putString("hash", match.fingerprint.hash.toString(16))
                            putDouble("ageMs", (System.currentTimeMillis() - match.fingerprint.timestamp).toDouble())
                        }
                        matchesArray.pushMap(matchMap)
                    }
                    putArray("matches", matchesArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error finding similar frames: ${e.message}", e)
                promise.reject("FIND_SIMILAR_ERROR", e.message)
            }
        }
    }
    
    /**
     * Get buffer statistics
     */
    @ReactMethod
    fun getBufferStats(promise: Promise) {
        try {
            val bufferStats = similarityAnalyzer.getBufferStats()
            
            val result = Arguments.createMap().apply {
                putInt("totalFrames", bufferStats.totalFrames)
                putInt("maxCapacity", bufferStats.maxCapacity)
                putDouble("utilization", (bufferStats.getUtilization() * 100).toDouble())
                putDouble("oldestFrameAge", bufferStats.oldestFrameAge.toDouble())
                putDouble("averageSimilarity", bufferStats.averageSimilarity.toDouble())
                putDouble("memoryUsageKB", (bufferStats.memoryUsage / 1024).toDouble())
                putDouble("totalAdded", bufferStats.totalAdded.toDouble())
                putDouble("totalExpired", bufferStats.totalExpired.toDouble())
                putDouble("totalSearches", bufferStats.totalSearches.toDouble())
                putDouble("averageSearchTimeMs", bufferStats.averageSearchTimeMs.toDouble())
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting buffer stats: ${e.message}", e)
            promise.reject("BUFFER_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Get performance metrics
     */
    @ReactMethod
    fun getPerformanceMetrics(promise: Promise) {
        try {
            val summary = performanceMonitor.getPerformanceSummary()
            val analyzerMetrics = similarityAnalyzer.getPerformanceMetrics()
            
            val result = Arguments.createMap().apply {
                putDouble("uptimeMs", summary.uptimeMs.toDouble())
                putDouble("totalOperations", summary.totalOperations.toDouble())
                putDouble("averageProcessingTimeMs", summary.averageProcessingTimeMs.toDouble())
                putDouble("alertCount", summary.alertCount.toDouble())
                putDouble("memoryUsageMB", summary.memoryUsageMB.toDouble())
                putDouble("analyzerTotalOperations", analyzerMetrics.totalOperations.toDouble())
                putDouble("analyzerAverageTimeMs", analyzerMetrics.averageProcessingTimeMs.toDouble())
                putBoolean("meetsPerformanceRequirements", summary.averageProcessingTimeMs < 10.0f)
                
                val operationsArray = Arguments.createArray()
                summary.operationSummaries.forEach { op ->
                    val opMap = Arguments.createMap().apply {
                        putString("operationName", op.operationName)
                        putDouble("totalExecutions", op.totalExecutions.toDouble())
                        putDouble("averageTimeMs", op.averageTimeMs.toDouble())
                        putDouble("successRate", op.successRate.toDouble())
                    }
                    operationsArray.pushMap(opMap)
                }
                putArray("operationSummaries", operationsArray)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting performance metrics: ${e.message}", e)
            promise.reject("PERFORMANCE_METRICS_ERROR", e.message)
        }
    }
    
    /**
     * Run comprehensive test suite
     */
    @ReactMethod
    fun runTestSuite(promise: Promise) {
        moduleScope.launch {
            try {
                Log.d(TAG, "🧪 Starting comprehensive test suite...")
                
                val testResults = mutableListOf<Map<String, Any>>()
                var allTestsPassed = true
                
                // Test 1: Hash Generation
                try {
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.GRADIENT)
                    val (hash, processingTime) = PerceptualHashGenerator.generateHashWithTiming(testBitmap)
                    testBitmap.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (hash == 0L) {
                        issues.add("Hash generation returned 0 - indicates no visual variation")
                    }
                    if (processingTime > 5) {
                        issues.add("Hash generation too slow: ${processingTime}ms > 5ms")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Hash Generation",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to processingTime,
                        "hash" to hash.toString(16),
                        "issues" to issues
                    ))
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Hash Generation",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Hash generation failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 2: Motion Detection
                try {
                    val bitmap1 = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val bitmap2 = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.GRADIENT)
                    
                    val motionResult = motionEngine.detectMotion(bitmap1, bitmap2)
                    
                    bitmap1.recycle()
                    bitmap2.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (motionResult.pixelDifference <= 0) {
                        issues.add("No motion detected between different patterns")
                    }
                    if (motionResult.processingTimeMs > 10) {
                        issues.add("Motion detection too slow: ${motionResult.processingTimeMs}ms")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Motion Detection",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to motionResult.processingTimeMs,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Motion Detection",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Motion detection failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 3: Similarity Analysis
                try {
                    similarityAnalyzer.clear()
                    
                    val bitmap1 = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val fingerprint1 = PerceptualHashGenerator.generateFrameFingerprint(bitmap1, System.currentTimeMillis())
                    
                    similarityAnalyzer.addFrame(fingerprint1)
                    val analysisResult = similarityAnalyzer.analyzeFrame(fingerprint1)
                    
                    bitmap1.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (analysisResult.shouldProcess) {
                        issues.add("Should not process identical frame")
                    }
                    if (analysisResult.processingTimeMs > 5) {
                        issues.add("Similarity analysis too slow: ${analysisResult.processingTimeMs}ms")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Similarity Analysis",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to analysisResult.processingTimeMs,
                        "similarity" to (analysisResult.mostSimilarMatch?.similarity ?: 0f),
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Similarity Analysis",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Similarity analysis failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 4: Advanced Motion Analysis
                try {
                    val bitmap1 = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val bitmap2 = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.CHECKERBOARD)
                    val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(bitmap1, System.currentTimeMillis())
                    
                    val motionAnalysis = motionAnalyzer.analyzeMotion(
                        currentBitmap = bitmap1,
                        previousBitmap = bitmap2,
                        currentFingerprint = fingerprint,
                        contentType = MotionThresholdEvaluator.ContentType.SOCIAL_MEDIA
                    )
                    
                    bitmap1.recycle()
                    bitmap2.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (motionAnalysis.thresholdEvaluation.adjustedThreshold <= 0) {
                        issues.add("Invalid adjusted threshold")
                    }
                    if (motionAnalysis.thresholdEvaluation.confidenceScore <= 0) {
                        issues.add("Invalid confidence score")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Advanced Motion Analysis",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to motionAnalysis.currentMotion.processingTimeMs,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Advanced Motion Analysis",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Advanced motion analysis failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 5: State Machine Functionality
                try {
                    // Reset state machine for clean test
                    stateMachine.reset()
                    
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(testBitmap, System.currentTimeMillis())
                    
                    // Test normal scanning state
                    val frameContext = FrameContext(
                        fingerprint = fingerprint,
                        similarity = 0.5f,
                        motionDetected = false,
                        isHarmfulContent = false,
                        confidenceScore = 0.8f
                    )
                    
                    val decision = stateMachine.processFrame(frameContext)
                    testBitmap.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (stateMachine.getCurrentState() != DetectionState.SCANNING) {
                        issues.add("State machine not in SCANNING state initially")
                    }
                    if (!decision.shouldAnalyze) {
                        issues.add("Should analyze in SCANNING state with low similarity")
                    }
                    if (decision.processingTimeMs > 10) {
                        issues.add("State machine processing too slow: ${decision.processingTimeMs}ms")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "State Machine Functionality",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to decision.processingTimeMs,
                        "currentState" to stateMachine.getCurrentState().name,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "State Machine Functionality",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("State machine test failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 6: State Transitions
                try {
                    // Test harmful content detection transition
                    stateMachine.forceStateTransition(DetectionState.HARMFUL_DETECTED, "Test harmful content")
                    
                    val issues = mutableListOf<String>()
                    if (stateMachine.getCurrentState() != DetectionState.HARMFUL_DETECTED) {
                        issues.add("Failed to transition to HARMFUL_DETECTED state")
                    }
                    
                    // Test state history
                    val history = stateMachine.getStateHistory()
                    if (history.isEmpty()) {
                        issues.add("State history not being tracked")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "State Transitions",
                        "passed" to issues.isEmpty(),
                        "currentState" to stateMachine.getCurrentState().name,
                        "historySize" to history.size,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "State Transitions",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("State transition test failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 7: Temporal Awareness and Cooldown
                try {
                    // Reset for clean test
                    stateMachine.reset()
                    
                    // Test cooldown functionality
                    val cooldownStatus = stateMachine.getCooldownStatus()
                    val temporalStats = stateMachine.getTemporalAwarenessStats()
                    
                    val issues = mutableListOf<String>()
                    if (cooldownStatus.isInCooldown) {
                        issues.add("Should not be in cooldown initially")
                    }
                    if (temporalStats.totalPatterns < 0) {
                        issues.add("Invalid temporal pattern count")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Temporal Awareness and Cooldown",
                        "passed" to issues.isEmpty(),
                        "cooldownActive" to cooldownStatus.isInCooldown,
                        "totalPatterns" to temporalStats.totalPatterns,
                        "activePatterns" to temporalStats.activePatterns,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Temporal Awareness and Cooldown",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Temporal awareness test failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 8: Local Text Extraction
                try {
                    // Create test bitmap with text
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(200, 100, TestPattern.SOLID)
                    
                    // Extract text (should be minimal for solid color bitmap)
                    val extractionResult = localTextExtractor.extractText(testBitmap)
                    testBitmap.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (extractionResult.processingTimeMs > LocalTextExtractor.TARGET_PROCESSING_TIME_MS) {
                        issues.add("Text extraction too slow: ${extractionResult.processingTimeMs}ms > ${LocalTextExtractor.TARGET_PROCESSING_TIME_MS}ms")
                    }
                    if (extractionResult.confidence < 0) {
                        issues.add("Invalid confidence score: ${extractionResult.confidence}")
                    }
                    if (extractionResult.textDensity < 0) {
                        issues.add("Invalid text density: ${extractionResult.textDensity}")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Local Text Extraction",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to extractionResult.processingTimeMs,
                        "confidence" to extractionResult.confidence,
                        "textDensity" to extractionResult.textDensity,
                        "textRegions" to extractionResult.textRegions.size,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Local Text Extraction",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Text extraction failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 9: Text Density Calculation
                try {
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.CHECKERBOARD)
                    
                    val textDensity = localTextExtractor.calculateTextDensity(testBitmap)
                    val hasText = localTextExtractor.isTextPresent(testBitmap)
                    
                    testBitmap.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (textDensity < 0 || textDensity > 1) {
                        issues.add("Invalid text density range: $textDensity (should be 0-1)")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Text Density Calculation",
                        "passed" to issues.isEmpty(),
                        "textDensity" to textDensity,
                        "hasText" to hasText,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Text Density Calculation",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Text density calculation failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 10: Pre-filter Pipeline Infrastructure
                try {
                    // Reset pipeline for clean test
                    preFilterPipeline.reset()
                    
                    // Add similarity and motion filter stages
                    val similarityStage = SimilarityFilterStage(
                        name = "SimilarityFilter",
                        priority = 1,
                        defaultThreshold = 0.9f,
                        similarityAnalyzer = similarityAnalyzer
                    )
                    
                    val motionStage = MotionFilterStage(
                        name = "MotionFilter", 
                        priority = 2,
                        defaultThreshold = 0.25f,
                        motionEngine = motionEngine,
                        motionAnalyzer = motionAnalyzer
                    )
                    
                    preFilterPipeline.addFilterStage(similarityStage)
                    preFilterPipeline.addFilterStage(motionStage)
                    
                    // Create test frame
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(testBitmap, System.currentTimeMillis())
                    val capturedFrame = CapturedFrame(
                        bitmap = testBitmap,
                        timestamp = System.currentTimeMillis(),
                        fingerprint = fingerprint
                    )
                    
                    // Test pipeline execution
                    val filterContext = FilterContext(
                        currentState = DetectionState.SCANNING,
                        configuration = FilterConfiguration(enableVerboseLogging = false)
                    )
                    
                    val decision = preFilterPipeline.shouldAnalyze(capturedFrame, filterContext)
                    testBitmap.recycle()
                    
                    val issues = mutableListOf<String>()
                    if (decision.totalStagesExecuted < 1) {
                        issues.add("Expected at least 1 stage executed, got ${decision.totalStagesExecuted}")
                    }
                    if (decision.processingTimeMs > 10) {
                        issues.add("Pipeline processing too slow: ${decision.processingTimeMs}ms")
                    }
                    if (decision.stageResults.isEmpty()) {
                        issues.add("No stage results returned")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Pre-filter Pipeline with Real Stages",
                        "passed" to issues.isEmpty(),
                        "processingTimeMs" to decision.processingTimeMs,
                        "stagesExecuted" to decision.totalStagesExecuted,
                        "shouldProceed" to decision.shouldProceed,
                        "activeStages" to preFilterPipeline.getActiveStages().size,
                        "issues" to issues
                    ))
                    
                    if (issues.isNotEmpty()) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Pre-filter Pipeline with Real Stages",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Pre-filter pipeline test failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                // Test 11: Performance Requirements Validation
                try {
                    val testBitmaps = mutableListOf<Bitmap>()
                    val timings = mutableListOf<Long>()
                    
                    // Create diverse test bitmaps for realistic performance testing
                    val patterns = listOf(TestPattern.GRADIENT, TestPattern.CHECKERBOARD, TestPattern.SOLID)
                    
                    for (i in 0 until 10) {
                        val pattern = patterns[i % patterns.size]
                        val bitmap = PerceptualHashGenerator.createTestBitmap(100, 100, pattern)
                        testBitmaps.add(bitmap)
                        
                        // Measure individual hash generation time
                        val (_, processingTime) = PerceptualHashGenerator.generateHashWithTiming(bitmap)
                        timings.add(processingTime)
                    }
                    
                    // Clean up bitmaps
                    testBitmaps.forEach { it.recycle() }
                    
                    val averageTimeMs = timings.average()
                    val maxTimeMs = timings.maxOrNull() ?: 0L
                    val meetsRequirements = averageTimeMs < 5.0 && maxTimeMs < 10L
                    
                    val issues = mutableListOf<String>()
                    if (averageTimeMs >= 5.0) {
                        issues.add("Average processing time too slow: ${String.format("%.2f", averageTimeMs)}ms >= 5ms")
                    }
                    if (maxTimeMs >= 10L) {
                        issues.add("Max processing time too slow: ${maxTimeMs}ms >= 10ms")
                    }
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Performance Requirements",
                        "passed" to meetsRequirements,
                        "totalBitmaps" to testBitmaps.size,
                        "averageTimeMs" to averageTimeMs,
                        "maxTimeMs" to maxTimeMs,
                        "meetsRequirements" to meetsRequirements,
                        "issues" to issues
                    ))
                    
                    if (!meetsRequirements) allTestsPassed = false
                    
                } catch (e: Exception) {
                    allTestsPassed = false
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Performance Requirements",
                        "passed" to false,
                        "error" to (e.message ?: "Unknown error"),
                        "issues" to listOf("Performance validation failed: ${e.message ?: "Unknown error"}")
                    ))
                }
                
                val summary = "Completed ${testResults.size} tests. " +
                    "${testResults.count { (it["passed"] as Boolean) }} passed, " +
                    "${testResults.count { !(it["passed"] as Boolean) }} failed."
                
                Log.d(TAG, "✅ Test suite complete: $summary")
                
                val result = Arguments.createMap().apply {
                    putBoolean("allTestsPassed", allTestsPassed)
                    putString("summary", summary)
                    
                    val resultsArray = Arguments.createArray()
                    testResults.forEach { test ->
                        val testMap = Arguments.createMap().apply {
                            putString("testName", test["testName"] as String)
                            putBoolean("passed", test["passed"] as Boolean)
                            test["processingTimeMs"]?.let { putDouble("processingTimeMs", (it as Number).toDouble()) }
                            test["hash"]?.let { putString("hash", it as String) }
                            test["similarity"]?.let { putDouble("similarity", (it as Number).toDouble()) }
                            test["error"]?.let { putString("error", it.toString()) }
                            
                            val issuesArray = Arguments.createArray()
                            @Suppress("UNCHECKED_CAST")
                            (test["issues"] as List<String>).forEach { issue ->
                                issuesArray.pushString(issue)
                            }
                            putArray("issues", issuesArray)
                        }
                        resultsArray.pushMap(testMap)
                    }
                    putArray("testResults", resultsArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error running test suite: ${e.message}", e)
                promise.reject("TEST_SUITE_ERROR", e.message)
            }
        }
    }
    
    /**
     * Test state machine with frame context
     */
    @ReactMethod
    fun testStateMachine(
        base64Image: String,
        similarity: Double,
        motionDetected: Boolean,
        isHarmfulContent: Boolean,
        confidenceScore: Double,
        promise: Promise
    ) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("test_state_machine")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate fingerprint
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis()
                )
                
                // Create frame context
                val frameContext = FrameContext(
                    fingerprint = fingerprint,
                    similarity = similarity.toFloat(),
                    motionDetected = motionDetected,
                    isHarmfulContent = isHarmfulContent,
                    confidenceScore = confidenceScore.toFloat()
                )
                
                // Process frame through state machine
                val decision = stateMachine.processFrame(frameContext)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putString("currentState", stateMachine.getCurrentState().name)
                    putBoolean("shouldAnalyze", decision.shouldAnalyze)
                    putDouble("analysisFrequency", decision.analysisFrequency.toDouble())
                    putDouble("confidenceThreshold", decision.confidenceThreshold.toDouble())
                    putString("reason", decision.reason)
                    putBoolean("recommendsStateChange", decision.recommendsStateChange())
                    putDouble("processingTimeMs", decision.processingTimeMs.toDouble())
                    
                    decision.newState?.let { newState ->
                        putString("newState", newState.name)
                    }
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error testing state machine: ${e.message}", e)
                promise.reject("STATE_MACHINE_TEST_ERROR", e.message)
            }
        }
    }
    
    /**
     * Force state transition for testing
     */
    @ReactMethod
    fun forceStateTransition(stateName: String, reason: String, promise: Promise) {
        try {
            val detectionState = when (stateName.uppercase()) {
                "SCANNING" -> DetectionState.SCANNING
                "HARMFUL_DETECTED" -> DetectionState.HARMFUL_DETECTED
                "SCROLLING_AWAY" -> DetectionState.SCROLLING_AWAY
                "COOLDOWN" -> DetectionState.COOLDOWN
                "SAFE_CONTENT" -> DetectionState.SAFE_CONTENT
                else -> {
                    promise.reject("INVALID_STATE", "Invalid state name: $stateName")
                    return
                }
            }
            
            stateMachine.forceStateTransition(detectionState, reason)
            
            val result = Arguments.createMap().apply {
                putString("newState", stateMachine.getCurrentState().name)
                putString("reason", reason)
                putBoolean("success", true)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error forcing state transition: ${e.message}", e)
            promise.reject("FORCE_TRANSITION_ERROR", e.message)
        }
    }
    
    /**
     * Get current state machine status
     */
    @ReactMethod
    fun getStateMachineStatus(promise: Promise) {
        try {
            val stats = stateMachine.getStateMachineStats()
            val history = stateMachine.getStateHistory()
            val cooldownStatus = stateMachine.getCooldownStatus()
            val decisionStats = stateMachine.getAdvancedDecisionStats()
            
            val result = Arguments.createMap().apply {
                putString("currentState", stats.currentState.name)
                putDouble("timeSinceLastStateChange", stats.timeSinceLastStateChange.toDouble())
                putDouble("timeSinceLastHarmful", stats.timeSinceLastHarmful.toDouble())
                putDouble("timeSinceLastAnalysis", stats.timeSinceLastAnalysis.toDouble())
                putInt("totalTransitions", stats.totalTransitions)
                putDouble("averageProcessingTimeMs", stats.averageProcessingTimeMs.toDouble())
                putDouble("historyUtilization", (stats.historyUtilization * 100).toDouble())
                putString("mostFrequentState", stats.mostFrequentState?.name)
                
                // Add state information
                val stateInfoMap = Arguments.createMap().apply {
                    putString("displayName", stats.currentState.displayName)
                    putString("description", stats.currentState.description)
                    putDouble("defaultAnalysisFrequency", stats.currentState.defaultAnalysisFrequency.toDouble())
                    putDouble("defaultConfidenceThreshold", stats.currentState.defaultConfidenceThreshold.toDouble())
                    putDouble("similarityThreshold", stats.currentState.getSimilarityThreshold().toDouble())
                    putBoolean("allowsNormalAnalysis", stats.currentState.allowsNormalAnalysis())
                    putBoolean("isProtectiveMode", stats.currentState.isProtectiveMode())
                    putBoolean("indicatesUserActivity", stats.currentState.indicatesUserActivity())
                }
                putMap("stateInfo", stateInfoMap)
                
                // Add recent history
                val historyArray = Arguments.createArray()
                history.takeLast(10).forEach { transition ->
                    val transitionMap = Arguments.createMap().apply {
                        putString("fromState", transition.fromState.name)
                        putString("toState", transition.toState.name)
                        putString("reason", transition.reason)
                        putDouble("ageMs", transition.getAgeMs().toDouble())
                        putDouble("processingTimeMs", transition.processingTimeMs.toDouble())
                    }
                    historyArray.pushMap(transitionMap)
                }
                putArray("recentHistory", historyArray)
                
                // Add configuration
                val configMap = Arguments.createMap().apply {
                    putDouble("cooldownPeriodMs", stats.config.cooldownPeriodMs.toDouble())
                    putDouble("extendedCooldownMs", stats.config.extendedCooldownMs.toDouble())
                    putDouble("safeContentThresholdMs", stats.config.safeContentThresholdMs.toDouble())
                    putInt("maxHistorySize", stats.config.maxHistorySize)
                    putBoolean("enableVerboseLogging", stats.config.enableVerboseLogging)
                }
                putMap("config", configMap)
                
                // Add cooldown status
                val cooldownStatusMap = Arguments.createMap().apply {
                    putBoolean("isInCooldown", cooldownStatus.isInCooldown)
                    putString("cooldownType", cooldownStatus.cooldownType.name)
                    putDouble("remainingTimeMs", cooldownStatus.remainingTimeMs.toDouble())
                    putInt("extensionCount", cooldownStatus.extensionCount)
                    putBoolean("safeContentWindowActive", cooldownStatus.safeContentWindowActive)
                    putDouble("lastHarmfulSimilarity", cooldownStatus.lastHarmfulSimilarity.toDouble())
                }
                putMap("cooldownStatus", cooldownStatusMap)
                
                // Add advanced decision stats
                val decisionStatsMap = Arguments.createMap().apply {
                    putInt("totalDecisions", decisionStats.totalDecisions)
                    putDouble("averageProcessingTime", decisionStats.averageProcessingTime.toDouble())
                    putDouble("analysisRate", (decisionStats.analysisRate * 100).toDouble())
                }
                putMap("advancedDecisionStats", decisionStatsMap)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting state machine status: ${e.message}", e)
            promise.reject("STATE_MACHINE_STATUS_ERROR", e.message)
        }
    }
    
    /**
     * Get detailed cooldown statistics
     */
    @ReactMethod
    fun getCooldownStats(promise: Promise) {
        try {
            val cooldownStats = stateMachine.getCooldownManagerStats()
            val cooldownStatus = stateMachine.getCooldownStatus()
            
            val result = Arguments.createMap().apply {
                // Current status
                putBoolean("isInCooldown", cooldownStatus.isInCooldown)
                putString("cooldownType", cooldownStatus.cooldownType.name)
                putDouble("remainingTimeMs", cooldownStatus.remainingTimeMs.toDouble())
                putInt("extensionCount", cooldownStatus.extensionCount)
                putBoolean("safeContentWindowActive", cooldownStatus.safeContentWindowActive)
                
                // Statistics
                putInt("totalCooldowns", cooldownStats.totalCooldowns)
                putInt("totalExtensions", cooldownStats.totalExtensions)
                putDouble("averageCooldownDuration", cooldownStats.averageCooldownDuration.toDouble())
                putInt("temporalWindowCount", cooldownStats.temporalWindowCount)
                
                // Safe content stats
                val safeContentStatsMap = Arguments.createMap().apply {
                    putInt("totalFrames", cooldownStats.safeContentStats.totalFrames)
                    putInt("safeFrames", cooldownStats.safeContentStats.safeFrames)
                    putInt("unsafeFrames", cooldownStats.safeContentStats.unsafeFrames)
                    putDouble("safeRatio", (cooldownStats.safeContentStats.safeRatio * 100).toDouble())
                    putDouble("windowDurationMs", cooldownStats.safeContentStats.windowDurationMs.toDouble())
                    putDouble("oldestFrameAge", cooldownStats.safeContentStats.oldestFrameAge.toDouble())
                }
                putMap("safeContentStats", safeContentStatsMap)
                
                // Configuration
                val configMap = Arguments.createMap().apply {
                    putDouble("baseCooldownPeriodMs", cooldownStats.config.baseCooldownPeriodMs.toDouble())
                    putDouble("extendedCooldownPeriodMs", cooldownStats.config.extendedCooldownPeriodMs.toDouble())
                    putDouble("safeContentWindowMs", cooldownStats.config.safeContentWindowMs.toDouble())
                    putDouble("similarContentThreshold", (cooldownStats.config.similarContentThreshold * 100).toDouble())
                    putDouble("skipAnalysisThreshold", (cooldownStats.config.skipAnalysisThreshold * 100).toDouble())
                }
                putMap("config", configMap)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cooldown stats: ${e.message}", e)
            promise.reject("COOLDOWN_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Get temporal awareness statistics
     */
    @ReactMethod
    fun getTemporalAwarenessStats(promise: Promise) {
        try {
            val temporalStats = stateMachine.getTemporalAwarenessStats()
            
            val result = Arguments.createMap().apply {
                // Pattern statistics
                putInt("totalPatterns", temporalStats.totalPatterns)
                putInt("activePatterns", temporalStats.activePatterns)
                
                // Analysis skip statistics
                val skipStatsMap = Arguments.createMap().apply {
                    putInt("totalDecisions", temporalStats.analysisSkipStats.totalDecisions)
                    putInt("totalSkipped", temporalStats.analysisSkipStats.totalSkipped)
                    putDouble("skipRate", (temporalStats.analysisSkipStats.skipRate * 100).toDouble())
                    putDouble("recentSkipRate", (temporalStats.analysisSkipStats.recentSkipRate * 100).toDouble())
                }
                putMap("analysisSkipStats", skipStatsMap)
                
                // State window statistics
                val stateWindowStatsMap = Arguments.createMap()
                temporalStats.stateWindowStats.forEach { (state, stats) ->
                    val stateStatsMap = Arguments.createMap().apply {
                        putInt("totalFrames", stats.totalFrames)
                        putInt("harmfulFrames", stats.harmfulFrames)
                        putInt("safeFrames", stats.safeFrames)
                        putDouble("harmfulRatio", (stats.harmfulRatio * 100).toDouble())
                        putDouble("windowDurationMs", stats.windowDurationMs.toDouble())
                        putDouble("oldestFrameAge", stats.oldestFrameAge.toDouble())
                    }
                    stateWindowStatsMap.putMap(state.name, stateStatsMap)
                }
                putMap("stateWindowStats", stateWindowStatsMap)
                
                // Basic temporal information
                putString("temporalEngineStatus", "Active")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting temporal awareness stats: ${e.message}", e)
            promise.reject("TEMPORAL_AWARENESS_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Reset state machine to initial state
     */
    @ReactMethod
    fun resetStateMachine(promise: Promise) {
        try {
            stateMachine.reset()
            
            val result = Arguments.createMap().apply {
                putString("currentState", stateMachine.getCurrentState().name)
                putBoolean("success", true)
                putString("message", "State machine reset to initial state")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting state machine: ${e.message}", e)
            promise.reject("RESET_STATE_MACHINE_ERROR", e.message)
        }
    }
    
    /**
     * Test pre-filter pipeline with a frame
     */
    @ReactMethod
    fun testPreFilterPipeline(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("test_prefilter_pipeline")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Generate fingerprint for the frame
                val fingerprint = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis()
                )
                
                // Create captured frame
                val capturedFrame = CapturedFrame(
                    bitmap = bitmap,
                    timestamp = System.currentTimeMillis(),
                    fingerprint = fingerprint,
                    metadata = mapOf("source" to "react_native_test")
                )
                
                // Create filter context
                val filterContext = FilterContext(
                    currentState = stateMachine.getCurrentState(),
                    timeSinceLastAnalysis = 0L,
                    timeSinceLastHarmful = 0L,
                    isInCooldown = false,
                    configuration = FilterConfiguration(enableVerboseLogging = true)
                )
                
                // Process through pipeline
                val decision = preFilterPipeline.shouldAnalyze(capturedFrame, filterContext)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putBoolean("shouldProceed", decision.shouldProceed)
                    putString("reason", decision.reason)
                    putDouble("processingTimeMs", decision.processingTimeMs.toDouble())
                    putDouble("executionId", decision.executionId.toDouble())
                    putInt("totalStagesExecuted", decision.totalStagesExecuted)
                    putDouble("averageConfidence", (decision.averageConfidence * 100).toDouble())
                    putBoolean("meetsPerformanceRequirements", decision.meetsPerformanceRequirements())
                    
                    // Stage results
                    val stageResultsArray = Arguments.createArray()
                    decision.stageResults.forEach { stageResult ->
                        val stageMap = Arguments.createMap().apply {
                            putString("stageName", stageResult.stageName)
                            putBoolean("passed", stageResult.passed)
                            putDouble("confidence", (stageResult.confidence * 100).toDouble())
                            putDouble("processingTimeMs", stageResult.processingTimeMs.toDouble())
                            putString("reason", stageResult.reason)
                            putBoolean("shouldTerminateEarly", stageResult.shouldTerminateEarly)
                        }
                        stageResultsArray.pushMap(stageMap)
                    }
                    putArray("stageResults", stageResultsArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error testing pre-filter pipeline: ${e.message}", e)
                promise.reject("PREFILTER_PIPELINE_ERROR", e.message)
            }
        }
    }
    
    /**
     * Add a test filter stage to the pipeline
     */
    @ReactMethod
    fun addTestFilterStage(stageName: String, priority: Int, passRate: Double, promise: Promise) {
        try {
            val testStage = TestFilterStage(
                name = stageName,
                priority = priority,
                passRate = passRate.toFloat(),
                simulatedProcessingTimeMs = 1L
            )
            
            preFilterPipeline.addFilterStage(testStage)
            
            val result = Arguments.createMap().apply {
                putString("stageName", stageName)
                putInt("priority", priority)
                putDouble("passRate", passRate)
                putBoolean("success", true)
                putArray("activeStages", Arguments.createArray().apply {
                    preFilterPipeline.getActiveStages().forEach { pushString(it) }
                })
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding test filter stage: ${e.message}", e)
            promise.reject("ADD_FILTER_STAGE_ERROR", e.message)
        }
    }
    
    /**
     * Add similarity filter stage to the pipeline
     */
    @ReactMethod
    fun addSimilarityFilterStage(threshold: Double, promise: Promise) {
        try {
            val similarityStage = SimilarityFilterStage(
                name = "SimilarityFilter",
                priority = 1,
                defaultThreshold = threshold.toFloat(),
                similarityAnalyzer = similarityAnalyzer
            )
            
            preFilterPipeline.addFilterStage(similarityStage)
            
            val result = Arguments.createMap().apply {
                putString("stageName", "SimilarityFilter")
                putInt("priority", 1)
                putDouble("threshold", threshold)
                putBoolean("success", true)
                putArray("activeStages", Arguments.createArray().apply {
                    preFilterPipeline.getActiveStages().forEach { pushString(it) }
                })
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding similarity filter stage: ${e.message}", e)
            promise.reject("ADD_SIMILARITY_FILTER_ERROR", e.message)
        }
    }
    
    /**
     * Add motion filter stage to the pipeline
     */
    @ReactMethod
    fun addMotionFilterStage(threshold: Double, promise: Promise) {
        try {
            val motionStage = MotionFilterStage(
                name = "MotionFilter",
                priority = 2,
                defaultThreshold = threshold.toFloat(),
                motionEngine = motionEngine,
                motionAnalyzer = motionAnalyzer
            )
            
            preFilterPipeline.addFilterStage(motionStage)
            
            val result = Arguments.createMap().apply {
                putString("stageName", "MotionFilter")
                putInt("priority", 2)
                putDouble("threshold", threshold)
                putBoolean("success", true)
                putArray("activeStages", Arguments.createArray().apply {
                    preFilterPipeline.getActiveStages().forEach { pushString(it) }
                })
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding motion filter stage: ${e.message}", e)
            promise.reject("ADD_MOTION_FILTER_ERROR", e.message)
        }
    }
    
    /**
     * Get pre-filter pipeline statistics
     */
    @ReactMethod
    fun getPreFilterPipelineStats(promise: Promise) {
        try {
            val stats = preFilterPipeline.getFilterStats()
            
            val result = Arguments.createMap().apply {
                putDouble("totalExecutions", stats.totalExecutions.toDouble())
                putDouble("totalFramesProcessed", stats.totalFramesProcessed.toDouble())
                putDouble("totalFramesSkipped", stats.totalFramesSkipped.toDouble())
                putDouble("skipRate", stats.getSkipRate().toDouble())
                putDouble("averageProcessingTimeMs", stats.averageProcessingTimeMs.toDouble())
                putDouble("maxProcessingTimeMs", stats.maxProcessingTimeMs.toDouble())
                putBoolean("meetsPerformanceRequirements", stats.meetsPerformanceRequirements())
                
                // Pipeline efficiency
                val efficiencyMap = Arguments.createMap().apply {
                    putDouble("skipRate", stats.pipelineEfficiency.skipRate.toDouble())
                    putDouble("processRate", stats.pipelineEfficiency.processRate.toDouble())
                    putDouble("averageStagesExecuted", stats.pipelineEfficiency.averageStagesExecuted.toDouble())
                    putDouble("earlyTerminationRate", stats.pipelineEfficiency.earlyTerminationRate.toDouble())
                }
                putMap("pipelineEfficiency", efficiencyMap)
                
                // Stage statistics
                val stageStatsArray = Arguments.createArray()
                stats.stageStats.forEach { stageStats ->
                    val stageMap = Arguments.createMap().apply {
                        putString("stageName", stageStats.stageName)
                        putDouble("totalExecutions", stageStats.totalExecutions.toDouble())
                        putDouble("passRate", stageStats.getPassRate().toDouble())
                        putDouble("averageProcessingTimeMs", stageStats.averageProcessingTimeMs.toDouble())
                        putDouble("maxProcessingTimeMs", stageStats.maxProcessingTimeMs.toDouble())
                        putDouble("averageConfidence", (stageStats.averageConfidence * 100).toDouble())
                        putBoolean("meetsPerformanceRequirements", stageStats.meetsPerformanceRequirements(2L))
                        
                        val alertsArray = Arguments.createArray()
                        stageStats.performanceAlerts.forEach { alert ->
                            alertsArray.pushString(alert)
                        }
                        putArray("performanceAlerts", alertsArray)
                    }
                    stageStatsArray.pushMap(stageMap)
                }
                putArray("stageStats", stageStatsArray)
                
                // Performance alerts
                val alertsArray = Arguments.createArray()
                stats.performanceAlerts.forEach { alert ->
                    alertsArray.pushString(alert)
                }
                putArray("performanceAlerts", alertsArray)
                
                // Active stages
                val activeStagesArray = Arguments.createArray()
                preFilterPipeline.getActiveStages().forEach { stage ->
                    activeStagesArray.pushString(stage)
                }
                putArray("activeStages", activeStagesArray)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting pre-filter pipeline stats: ${e.message}", e)
            promise.reject("PREFILTER_PIPELINE_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Reset pre-filter pipeline
     */
    @ReactMethod
    fun resetPreFilterPipeline(promise: Promise) {
        try {
            preFilterPipeline.reset()
            
            val result = Arguments.createMap().apply {
                putBoolean("success", true)
                putString("message", "Pre-filter pipeline reset completed")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting pre-filter pipeline: ${e.message}", e)
            promise.reject("RESET_PREFILTER_PIPELINE_ERROR", e.message)
        }
    }
    
    /**
     * Clear all data and reset
     */
    @ReactMethod
    fun clearAllData(promise: Promise) {
        try {
            similarityAnalyzer.clear()
            performanceMonitor.clearAllData()
            motionAnalyzer.clearHistory()
            motionThresholdEvaluator.reset()
            stateMachine.reset()
            preFilterPipeline.reset()
            
            promise.resolve(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing data: ${e.message}", e)
            promise.reject("CLEAR_ERROR", e.message)
        }
    }

    /**
     * Extract text from image using ML Kit
     */
    @ReactMethod
    fun extractText(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("extract_text")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Extract text using ML Kit
                val extractionResult = localTextExtractor.extractText(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putString("extractedText", extractionResult.extractedText)
                    putDouble("confidence", (extractionResult.confidence * 100).toDouble())
                    putDouble("textDensity", (extractionResult.textDensity * 100).toDouble())
                    putDouble("processingTimeMs", extractionResult.processingTimeMs.toDouble())
                    putBoolean("hasText", extractionResult.hasText())
                    putBoolean("meetsConfidenceThreshold", extractionResult.meetsConfidenceThreshold())
                    putBoolean("meetsPerformanceTarget", extractionResult.meetsPerformanceTarget())
                    
                    // Text regions
                    val regionsArray = Arguments.createArray()
                    extractionResult.textRegions.forEach { region ->
                        val regionMap = Arguments.createMap().apply {
                            putString("text", region.text)
                            putDouble("confidence", (region.confidence * 100).toDouble())
                            putString("textType", region.textType.name)
                            putBoolean("isContentText", region.isContentText())
                            putBoolean("isUIElement", region.isUIElement())
                            putBoolean("isMetadata", region.isMetadata())
                            putInt("area", region.getArea())
                            
                            // Bounding box
                            val boundingBoxMap = Arguments.createMap().apply {
                                putInt("left", region.boundingBox.left)
                                putInt("top", region.boundingBox.top)
                                putInt("right", region.boundingBox.right)
                                putInt("bottom", region.boundingBox.bottom)
                                putInt("width", region.boundingBox.width())
                                putInt("height", region.boundingBox.height())
                            }
                            putMap("boundingBox", boundingBoxMap)
                        }
                        regionsArray.pushMap(regionMap)
                    }
                    putArray("textRegions", regionsArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error extracting text: ${e.message}", e)
                promise.reject("TEXT_EXTRACTION_ERROR", e.message)
            }
        }
    }
    
    /**
     * Extract text regions with detailed analysis
     */
    @ReactMethod
    fun extractTextWithRegions(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("extract_text_regions")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Extract text regions
                val textRegions = localTextExtractor.extractTextWithRegions(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putInt("totalRegions", textRegions.size)
                    putInt("contentTextRegions", textRegions.count { it.isContentText() })
                    putInt("uiElementRegions", textRegions.count { it.isUIElement() })
                    putInt("metadataRegions", textRegions.count { it.isMetadata() })
                    
                    // Regions array
                    val regionsArray = Arguments.createArray()
                    textRegions.forEach { region ->
                        val regionMap = Arguments.createMap().apply {
                            putString("text", region.text)
                            putDouble("confidence", (region.confidence * 100).toDouble())
                            putString("textType", region.textType.name)
                            putInt("area", region.getArea())
                            
                            val boundingBoxMap = Arguments.createMap().apply {
                                putInt("left", region.boundingBox.left)
                                putInt("top", region.boundingBox.top)
                                putInt("right", region.boundingBox.right)
                                putInt("bottom", region.boundingBox.bottom)
                                putInt("width", region.boundingBox.width())
                                putInt("height", region.boundingBox.height())
                            }
                            putMap("boundingBox", boundingBoxMap)
                        }
                        regionsArray.pushMap(regionMap)
                    }
                    putArray("regions", regionsArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error extracting text regions: ${e.message}", e)
                promise.reject("TEXT_REGIONS_ERROR", e.message)
            }
        }
    }
    
    /**
     * Calculate text density for intelligent filtering
     */
    @ReactMethod
    fun calculateTextDensity(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("calculate_text_density")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Calculate text density
                val textDensity = localTextExtractor.calculateTextDensity(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putDouble("textDensity", (textDensity * 100).toDouble())
                    putBoolean("hasSignificantText", textDensity >= LocalTextExtractor.MIN_TEXT_DENSITY_THRESHOLD)
                    putDouble("processingTimeMs", timer.getElapsedTime().toDouble())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error calculating text density: ${e.message}", e)
                promise.reject("TEXT_DENSITY_ERROR", e.message)
            }
        }
    }
    
    /**
     * Quick text presence detection
     */
    @ReactMethod
    fun isTextPresent(base64Image: String, threshold: Double?, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("is_text_present")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                val thresholdValue = threshold?.toFloat() ?: LocalTextExtractor.MIN_TEXT_DENSITY_THRESHOLD
                
                // Check text presence
                val hasText = localTextExtractor.isTextPresent(bitmap, thresholdValue)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putBoolean("hasText", hasText)
                    putDouble("threshold", (thresholdValue * 100).toDouble())
                    putDouble("processingTimeMs", timer.getElapsedTime().toDouble())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error checking text presence: ${e.message}", e)
                promise.reject("TEXT_PRESENCE_ERROR", e.message)
            }
        }
    }
    
    /**
     * Get text extraction performance metrics
     */
    @ReactMethod
    fun getTextExtractionMetrics(promise: Promise) {
        try {
            val metrics = localTextExtractor.getPerformanceMetrics()
            
            val result = Arguments.createMap().apply {
                putDouble("totalExtractions", metrics.totalExtractions.toDouble())
                putDouble("averageProcessingTimeMs", metrics.averageProcessingTimeMs.toDouble())
                putDouble("successRate", (metrics.successRate * 100).toDouble())
                putBoolean("meetsPerformanceTarget", metrics.meetsPerformanceTarget)
                putDouble("targetProcessingTimeMs", LocalTextExtractor.TARGET_PROCESSING_TIME_MS.toDouble())
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting text extraction metrics: ${e.message}", e)
            promise.reject("TEXT_METRICS_ERROR", e.message)
        }
    }
    
    /**
     * Calculate regional text density for intelligent filtering
     */
    @ReactMethod
    fun calculateRegionalTextDensity(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("regional_text_density")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Calculate regional density
                val regionalDensity = localTextExtractor.calculateRegionalTextDensity(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putDouble("averageDensity", (regionalDensity.averageDensity * 100).toDouble())
                    putDouble("maxDensity", (regionalDensity.maxDensity * 100).toDouble())
                    putDouble("coverage", (regionalDensity.coverage * 100).toDouble())
                    putInt("significantRegions", regionalDensity.significantRegions)
                    putInt("totalRegions", regionalDensity.totalRegions)
                    putBoolean("hasSignificantText", regionalDensity.hasSignificantText())
                    putBoolean("isWellDistributed", regionalDensity.isWellDistributed())
                    putDouble("processingTimeMs", timer.getElapsedTime().toDouble())
                    
                    // Region densities map
                    val regionDensitiesMap = Arguments.createMap()
                    regionalDensity.regionDensities.forEach { (position, density) ->
                        regionDensitiesMap.putDouble(position.toString(), (density * 100).toDouble())
                    }
                    putMap("regionDensities", regionDensitiesMap)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error calculating regional text density: ${e.message}", e)
                promise.reject("REGIONAL_TEXT_DENSITY_ERROR", e.message)
            }
        }
    }
    
    /**
     * Calculate comprehensive text quality score
     */
    @ReactMethod
    fun calculateTextQuality(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("text_quality_analysis")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Calculate quality score
                val qualityScore = localTextExtractor.calculateTextQuality(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putDouble("clarityScore", (qualityScore.clarityScore * 100).toDouble())
                    putDouble("completenessScore", (qualityScore.completenessScore * 100).toDouble())
                    putDouble("relevanceScore", (qualityScore.relevanceScore * 100).toDouble())
                    putDouble("overallScore", (qualityScore.overallScore * 100).toDouble())
                    putBoolean("meetsQualityThreshold", qualityScore.meetsQualityThreshold)
                    putBoolean("isHighQuality", qualityScore.isHighQuality)
                    putString("qualityLevel", qualityScore.getQualityLevel().name)
                    putDouble("processingTimeMs", qualityScore.processingTimeMs.toDouble())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error calculating text quality: ${e.message}", e)
                promise.reject("TEXT_QUALITY_ERROR", e.message)
            }
        }
    }
    
    /**
     * Filter text regions to focus on meaningful content
     */
    @ReactMethod
    fun filterMeaningfulContent(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("filter_meaningful_content")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Extract text first
                val extractionResult = localTextExtractor.extractText(bitmap)
                
                // Filter meaningful content
                val filteredResult = localTextExtractor.filterMeaningfulContent(extractionResult.textRegions)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putString("contentText", filteredResult.contentText)
                    putDouble("filterEfficiency", (filteredResult.filterEfficiency * 100).toDouble())
                    putDouble("contentCoverage", (filteredResult.contentCoverage * 100).toDouble())
                    putDouble("qualityImprovement", (filteredResult.qualityImprovement * 100).toDouble())
                    putBoolean("hasSignificantContent", filteredResult.hasSignificantContent())
                    putInt("meaningfulRegionsCount", filteredResult.meaningfulRegions.size)
                    putInt("filteredOutCount", filteredResult.getTotalFilteredCount())
                    putInt("contentRegionCount", filteredResult.getContentRegionCount())
                    putDouble("processingTimeMs", timer.getElapsedTime().toDouble())
                    
                    // Meaningful regions
                    val meaningfulArray = Arguments.createArray()
                    filteredResult.meaningfulRegions.forEach { region ->
                        val regionMap = Arguments.createMap().apply {
                            putString("text", region.text)
                            putDouble("confidence", (region.confidence * 100).toDouble())
                            putString("textType", region.textType.name)
                            putInt("area", region.getArea())
                            
                            val boundingBoxMap = Arguments.createMap().apply {
                                putInt("left", region.boundingBox.left)
                                putInt("top", region.boundingBox.top)
                                putInt("right", region.boundingBox.right)
                                putInt("bottom", region.boundingBox.bottom)
                            }
                            putMap("boundingBox", boundingBoxMap)
                        }
                        meaningfulArray.pushMap(regionMap)
                    }
                    putArray("meaningfulRegions", meaningfulArray)
                    
                    // Filtered out regions summary
                    val filteredOutArray = Arguments.createArray()
                    filteredResult.filteredOutRegions.take(10).forEach { region -> // Limit to first 10 for performance
                        val regionMap = Arguments.createMap().apply {
                            putString("text", region.text)
                            putString("textType", region.textType.name)
                            putString("reason", "Filtered as non-meaningful content")
                        }
                        filteredOutArray.pushMap(regionMap)
                    }
                    putArray("filteredOutSample", filteredOutArray)
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error filtering meaningful content: ${e.message}", e)
                promise.reject("FILTER_MEANINGFUL_CONTENT_ERROR", e.message)
            }
        }
    }

    /**
     * Get text extraction cache statistics
     */
    @ReactMethod
    fun getTextExtractionCacheStats(promise: Promise) {
        try {
            val cacheStats = localTextExtractor.getCacheStats()
            
            val result = Arguments.createMap().apply {
                putInt("size", cacheStats.size)
                putInt("maxSize", cacheStats.maxSize)
                putDouble("hits", cacheStats.hits.toDouble())
                putDouble("misses", cacheStats.misses.toDouble())
                putDouble("hitRate", (cacheStats.hitRate * 100).toDouble())
                putInt("memoryUsageKB", cacheStats.memoryUsageKB)
                putBoolean("isEffective", cacheStats.hitRate > 0.2f)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cache stats: ${e.message}", e)
            promise.reject("CACHE_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Test fast text presence detection
     */
    @ReactMethod
    fun testFastTextPresence(base64Image: String, threshold: Double?, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("fast_text_presence")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                val thresholdValue = threshold?.toFloat() ?: LocalTextExtractor.MIN_TEXT_DENSITY_THRESHOLD
                
                // Test both fast and regular detection for comparison
                val startTime = System.currentTimeMillis()
                val fastResult = localTextExtractor.isTextPresentFast(bitmap, thresholdValue)
                val fastTime = System.currentTimeMillis() - startTime
                
                val regularStartTime = System.currentTimeMillis()
                val regularResult = localTextExtractor.isTextPresent(bitmap, thresholdValue)
                val regularTime = System.currentTimeMillis() - regularStartTime
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putBoolean("fastResult", fastResult)
                    putBoolean("regularResult", regularResult)
                    putDouble("fastProcessingTimeMs", fastTime.toDouble())
                    putDouble("regularProcessingTimeMs", regularTime.toDouble())
                    putDouble("speedImprovement", if (regularTime > 0) (regularTime.toDouble() / fastTime.toDouble()) else 1.0)
                    putBoolean("resultsMatch", fastResult == regularResult)
                    putDouble("threshold", (thresholdValue * 100).toDouble())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error testing fast text presence: ${e.message}", e)
                promise.reject("FAST_TEXT_PRESENCE_ERROR", e.message)
            }
        }
    }
    
    /**
     * Add text presence filter stage to pipeline
     */
    @ReactMethod
    fun addTextPresenceFilterStage(threshold: Double, promise: Promise) {
        try {
            val textStage = TextPresenceFilterStage(
                name = "TextPresenceFilter",
                priority = 3,
                textExtractor = localTextExtractor,
                textThreshold = threshold.toFloat(),
                enableROIOptimization = true
            )
            
            preFilterPipeline.addFilterStage(textStage)
            
            val result = Arguments.createMap().apply {
                putString("stageName", "TextPresenceFilter")
                putInt("priority", 3)
                putDouble("threshold", threshold)
                putBoolean("roiOptimized", true)
                putBoolean("success", true)
                putArray("activeStages", Arguments.createArray().apply {
                    preFilterPipeline.getActiveStages().forEach { pushString(it) }
                })
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding text presence filter stage: ${e.message}", e)
            promise.reject("ADD_TEXT_PRESENCE_FILTER_ERROR", e.message)
        }
    }

    /**
     * Get A/B testing statistics for text extraction
     */
    @ReactMethod
    fun getABTestingStats(promise: Promise) {
        try {
            val abStats = localTextExtractor.abTestingFramework.getABTestingStats()
            
            val result = Arguments.createMap().apply {
                putBoolean("isEnabled", abStats.isEnabled)
                putString("currentTestGroup", abStats.currentTestGroup.name)
                putInt("totalResults", abStats.totalResults)
                putInt("controlResults", abStats.controlResults)
                putInt("treatmentResults", abStats.treatmentResults)
                putDouble("controlAvgConfidence", (abStats.controlAvgConfidence * 100).toDouble())
                putDouble("treatmentAvgConfidence", (abStats.treatmentAvgConfidence * 100).toDouble())
                putDouble("controlAvgProcessingTime", abStats.controlAvgProcessingTime.toDouble())
                putDouble("treatmentAvgProcessingTime", abStats.treatmentAvgProcessingTime.toDouble())
                putDouble("controlSuccessRate", (abStats.controlSuccessRate * 100).toDouble())
                putDouble("treatmentSuccessRate", (abStats.treatmentSuccessRate * 100).toDouble())
                putDouble("confidenceImprovement", (abStats.getConfidenceImprovement() * 100).toDouble())
                putDouble("performanceImprovement", abStats.getPerformanceImprovement().toDouble())
                putDouble("successRateImprovement", (abStats.getSuccessRateImprovement() * 100).toDouble())
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting A/B testing stats: ${e.message}", e)
            promise.reject("AB_TESTING_STATS_ERROR", e.message)
        }
    }
    
    /**
     * Enable A/B testing for text extraction
     */
    @ReactMethod
    fun enableABTesting(testGroup: String, promise: Promise) {
        try {
            val group = when (testGroup.uppercase()) {
                "CONTROL" -> ABTestGroup.CONTROL
                "TREATMENT" -> ABTestGroup.TREATMENT
                else -> {
                    promise.reject("INVALID_TEST_GROUP", "Invalid test group: $testGroup")
                    return
                }
            }
            
            localTextExtractor.abTestingFramework.enableABTesting(group)
            
            val result = Arguments.createMap().apply {
                putBoolean("success", true)
                putString("testGroup", group.name)
                putString("message", "A/B testing enabled for group: ${group.name}")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling A/B testing: ${e.message}", e)
            promise.reject("ENABLE_AB_TESTING_ERROR", e.message)
        }
    }
    
    /**
     * Disable A/B testing
     */
    @ReactMethod
    fun disableABTesting(promise: Promise) {
        try {
            localTextExtractor.abTestingFramework.disableABTesting()
            
            val result = Arguments.createMap().apply {
                putBoolean("success", true)
                putString("message", "A/B testing disabled")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling A/B testing: ${e.message}", e)
            promise.reject("DISABLE_AB_TESTING_ERROR", e.message)
        }
    }
    
    /**
     * Test text extraction with validation and fallback
     */
    @ReactMethod
    fun testTextExtractionWithValidation(base64Image: String, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("test_text_extraction_validation")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Extract text with full validation and fallback system
                val extractionResult = localTextExtractor.extractText(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                val result = Arguments.createMap().apply {
                    putString("extractedText", extractionResult.extractedText)
                    putDouble("confidence", (extractionResult.confidence * 100).toDouble())
                    putDouble("textDensity", (extractionResult.textDensity * 100).toDouble())
                    putDouble("processingTimeMs", extractionResult.processingTimeMs.toDouble())
                    putInt("textRegions", extractionResult.textRegions.size)
                    putBoolean("meetsPerformanceTarget", extractionResult.meetsPerformanceTarget())
                    putBoolean("usedCache", extractionResult.usedCache)
                    putBoolean("roiDetected", extractionResult.roiDetected)
                    
                    // Validation results
                    extractionResult.validationPassed?.let { putBoolean("validationPassed", it) }
                    extractionResult.validationScore?.let { putDouble("validationScore", (it * 100).toDouble()) }
                    extractionResult.fallbackUsed?.let { putBoolean("fallbackUsed", it) }
                    extractionResult.fallbackStrategy?.let { putString("fallbackStrategy", it.name) }
                    
                    putBoolean("isHighQuality", extractionResult.isHighQuality())
                    putBoolean("requiresFallback", extractionResult.requiresFallback())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error testing text extraction with validation: ${e.message}", e)
                promise.reject("TEXT_EXTRACTION_VALIDATION_ERROR", e.message)
            }
        }
    }

    /**
     * Get validation system status
     */
    @ReactMethod
    fun getValidationSystemStatus(promise: Promise) {
        try {
            val status = localTextExtractor.getValidationSystemStatus()
            
            val result = Arguments.createMap().apply {
                putBoolean("validatorEnabled", status.validatorEnabled)
                putBoolean("fallbackManagerEnabled", status.fallbackManagerEnabled)
                putBoolean("abTestingEnabled", status.abTestingEnabled)
                putDouble("validationThreshold", (status.validationThreshold * 100).toDouble())
                
                val strategiesArray = Arguments.createArray()
                status.supportedFallbackStrategies.forEach { strategy ->
                    strategiesArray.pushString(strategy.name)
                }
                putArray("supportedFallbackStrategies", strategiesArray)
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting validation system status: ${e.message}", e)
            promise.reject("VALIDATION_SYSTEM_STATUS_ERROR", e.message)
        }
    }

    /**
     * Test text extraction with different content types
     */
    @ReactMethod
    fun testTextExtraction(base64Image: String, expectedText: String?, promise: Promise) {
        moduleScope.launch {
            val timer = performanceMonitor.startOperation("test_text_extraction")
            
            try {
                val bitmap = base64ToBitmap(base64Image)
                
                // Extract text
                val extractionResult = localTextExtractor.extractText(bitmap)
                
                bitmap.recycle()
                timer.complete()
                
                // Analyze results
                val contentTextRegions = extractionResult.textRegions.filter { it.isContentText() }
                val uiElementRegions = extractionResult.textRegions.filter { it.isUIElement() }
                val metadataRegions = extractionResult.textRegions.filter { it.isMetadata() }
                
                // Calculate accuracy if expected text provided
                val accuracy = if (expectedText != null && expectedText.isNotBlank()) {
                    calculateTextAccuracy(extractionResult.extractedText, expectedText)
                } else null
                
                val result = Arguments.createMap().apply {
                    putString("extractedText", extractionResult.extractedText)
                    putDouble("confidence", (extractionResult.confidence * 100).toDouble())
                    putDouble("textDensity", (extractionResult.textDensity * 100).toDouble())
                    putDouble("processingTimeMs", extractionResult.processingTimeMs.toDouble())
                    putBoolean("meetsPerformanceTarget", extractionResult.meetsPerformanceTarget())
                    
                    // Region analysis
                    putInt("totalRegions", extractionResult.textRegions.size)
                    putInt("contentTextRegions", contentTextRegions.size)
                    putInt("uiElementRegions", uiElementRegions.size)
                    putInt("metadataRegions", metadataRegions.size)
                    
                    // Classification accuracy
                    val classificationAccuracy = if (extractionResult.textRegions.isNotEmpty()) {
                        contentTextRegions.size.toFloat() / extractionResult.textRegions.size * 100
                    } else 0f
                    putDouble("classificationAccuracy", classificationAccuracy.toDouble())
                    
                    // Text accuracy if expected text provided
                    accuracy?.let { putDouble("textAccuracy", (it * 100).toDouble()) }
                    
                    // Performance assessment
                    val issues = mutableListOf<String>()
                    if (extractionResult.processingTimeMs > LocalTextExtractor.TARGET_PROCESSING_TIME_MS) {
                        issues.add("Processing time exceeded target: ${extractionResult.processingTimeMs}ms > ${LocalTextExtractor.TARGET_PROCESSING_TIME_MS}ms")
                    }
                    if (extractionResult.confidence < 0.8f) {
                        issues.add("Low confidence: ${(extractionResult.confidence * 100).toInt()}%")
                    }
                    if (accuracy != null && accuracy < 0.95f) {
                        issues.add("Text accuracy below target: ${(accuracy * 100).toInt()}% < 95%")
                    }
                    
                    val issuesArray = Arguments.createArray()
                    issues.forEach { issue -> issuesArray.pushString(issue) }
                    putArray("issues", issuesArray)
                    
                    putBoolean("passed", issues.isEmpty())
                }
                
                promise.resolve(result)
                
            } catch (e: Exception) {
                timer.fail(e.message)
                Log.e(TAG, "Error testing text extraction: ${e.message}", e)
                promise.reject("TEXT_EXTRACTION_TEST_ERROR", e.message)
            }
        }
    }
    
    /**
     * Clear text extraction metrics
     */
    @ReactMethod
    fun clearTextExtractionMetrics(promise: Promise) {
        try {
            localTextExtractor.clearMetrics()
            
            val result = Arguments.createMap().apply {
                putBoolean("success", true)
                putString("message", "Text extraction metrics cleared")
            }
            
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing text extraction metrics: ${e.message}", e)
            promise.reject("CLEAR_TEXT_METRICS_ERROR", e.message)
        }
    }
    
    /**
     * Calculate text accuracy between extracted and expected text
     */
    private fun calculateTextAccuracy(extracted: String, expected: String): Float {
        if (expected.isBlank()) return if (extracted.isBlank()) 1f else 0f
        if (extracted.isBlank()) return 0f
        
        val extractedWords = extracted.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        val expectedWords = expected.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        
        if (expectedWords.isEmpty()) return if (extractedWords.isEmpty()) 1f else 0f
        
        val matchingWords = extractedWords.intersect(expectedWords.toSet()).size
        return matchingWords.toFloat() / expectedWords.size
    }

    /**
     * Helper functions
     */
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes))
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun invalidate() {
        super.invalidate()
        moduleScope.cancel()
    }
}