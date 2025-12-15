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
                    val testBitmap = PerceptualHashGenerator.createTestBitmap(100, 100, TestPattern.SOLID)
                    val (hash, processingTime) = PerceptualHashGenerator.generateHashWithTiming(testBitmap)
                    testBitmap.recycle()
                    
                    testResults.add(mapOf<String, Any>(
                        "testName" to "Hash Generation",
                        "passed" to true,
                        "processingTimeMs" to processingTime,
                        "hash" to hash.toString(16),
                        "issues" to emptyList<String>()
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
     * Clear all data and reset
     */
    @ReactMethod
    fun clearAllData(promise: Promise) {
        try {
            similarityAnalyzer.clear()
            performanceMonitor.clearAllData()
            motionAnalyzer.clearHistory()
            motionThresholdEvaluator.reset()
            
            promise.resolve(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing data: ${e.message}", e)
            promise.reject("CLEAR_ERROR", e.message)
        }
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