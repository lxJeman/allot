package com.allot.detection

import android.util.Log

/**
 * MotionFilterStage - Motion detection pre-filter stage
 * 
 * Implements motion-based filtering as the second pipeline stage with 25% threshold.
 * Provides motion threshold evaluation and decision logic with bypass capability.
 * Supports motion-based analysis frequency adjustment (every 3rd frame for high velocity).
 * 
 * Performance Target: <2ms per frame pair
 */
class MotionFilterStage(
    name: String = "MotionFilter",
    priority: Int = 2, // Second stage in pipeline
    private val defaultThreshold: Float = 0.25f,
    private val motionEngine: MotionDetectionEngine,
    private val motionAnalyzer: MotionAnalyzer
) : BaseFilterStage(name, priority, targetProcessingTimeMs = 2L) {
    
    companion object {
        const val TAG = "MotionFilterStage"
        
        // Motion thresholds for different scenarios
        const val HIGH_VELOCITY_THRESHOLD = 0.40f
        const val NORMAL_MOTION_THRESHOLD = 0.25f
        const val LOW_MOTION_THRESHOLD = 0.15f
        
        // Frequency adjustment settings
        const val HIGH_VELOCITY_FRAME_SKIP = 3 // Every 3rd frame
        const val NORMAL_FRAME_SKIP = 1 // Every frame
        
        // Performance optimization
        const val BYPASS_ENABLED = true
        const val VELOCITY_TRACKING_ENABLED = true
    }
    
    // Motion tracking state
    private var previousFrame: CapturedFrame? = null
    private var frameCounter = 0L
    private var velocityHistory = mutableListOf<Float>()
    private val maxVelocityHistory = 10
    
    // Statistics
    private var totalMotionChecks = 0L
    private var significantMotionDetected = 0L
    private var framesSkippedDueToVelocity = 0L
    private var bypassedFrames = 0L  
  override fun executeFilter(frame: CapturedFrame, context: FilterContext): FilterResult {
        val (result, processingTime) = measureTime {
            // Validate frame first
            if (!validateFrame(frame)) {
                return@measureTime createFailResult(
                    confidence = 0f,
                    processingTimeMs = 0L,
                    reason = "Invalid frame data",
                    shouldTerminate = true
                )
            }
            
            frameCounter++
            
            // Check if we should skip this frame due to high velocity
            if (shouldSkipFrameForVelocity()) {
                framesSkippedDueToVelocity++
                return@measureTime createPassResult(
                    confidence = 0.7f,
                    processingTimeMs = 0L,
                    reason = "Frame skipped due to high velocity (every ${HIGH_VELOCITY_FRAME_SKIP}rd frame)",
                    metadata = mapOf(
                        "skippedForVelocity" to true,
                        "frameCounter" to frameCounter,
                        "skipInterval" to HIGH_VELOCITY_FRAME_SKIP
                    )
                )
            }
            
            // If no previous frame, allow processing
            val prevFrame = previousFrame
            if (prevFrame == null) {
                previousFrame = frame
                return@measureTime createPassResult(
                    confidence = 0.9f,
                    processingTimeMs = 0L,
                    reason = "First frame - no motion comparison possible",
                    metadata = mapOf("firstFrame" to true)
                )
            }
            
            // Perform motion detection
            val motionResult = performMotionDetection(frame, prevFrame, context)
            
            // Update previous frame
            previousFrame = frame
            
            // Update statistics and velocity tracking
            updateMotionStatistics(motionResult)
            
            // Create result based on motion analysis
            createMotionResult(motionResult, motionResult.processingTimeMs)
        }
        
        if (isVerboseLoggingEnabled(context)) {
            logPerformanceMetrics(processingTime, result.confidence, result.reason)
        }
        
        return result
    }
    
    /**
     * Check if frame should be skipped due to high velocity
     */
    private fun shouldSkipFrameForVelocity(): Boolean {
        if (!VELOCITY_TRACKING_ENABLED || velocityHistory.isEmpty()) {
            return false
        }
        
        // Calculate recent average velocity
        val recentVelocity = velocityHistory.takeLast(3).average().toFloat()
        
        // Skip frames if velocity is high
        return recentVelocity > HIGH_VELOCITY_THRESHOLD && 
               frameCounter % HIGH_VELOCITY_FRAME_SKIP != 0L
    }
    
    /**
     * Perform motion detection between current and previous frame
     */
    private fun performMotionDetection(
        currentFrame: CapturedFrame,
        previousFrame: CapturedFrame,
        context: FilterContext
    ): MotionAnalysisResult {
        val startTime = System.currentTimeMillis()
        
        // Determine motion threshold based on context
        val threshold = determineMotionThreshold(context)
        
        try {
            // Basic motion detection
            val motionResult = motionEngine.detectMotion(
                currentFrame.bitmap,
                previousFrame.bitmap
            )
            
            // Advanced motion analysis if available
            val advancedAnalysis = if (currentFrame.fingerprint != null) {
                motionAnalyzer.analyzeMotion(
                    currentBitmap = currentFrame.bitmap,
                    previousBitmap = previousFrame.bitmap,
                    currentFingerprint = currentFrame.fingerprint!!,
                    contentType = MotionThresholdEvaluator.ContentType.UNKNOWN
                )
            } else null
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Determine if motion is significant
            val isSignificantMotion = motionResult.pixelDifference >= threshold
            
            // Check for bypass conditions
            val shouldBypass = checkBypassConditions(motionResult, advancedAnalysis, context)
            
            return MotionAnalysisResult(
                pixelDifference = motionResult.pixelDifference,
                isSignificantMotion = isSignificantMotion,
                shouldProcess = isSignificantMotion || shouldBypass,
                threshold = threshold,
                processingTimeMs = processingTime,
                motionRegions = motionResult.motionRegions.size,
                velocity = advancedAnalysis?.motionVelocity ?: 0f,
                shouldBypass = shouldBypass,
                bypassReason = if (shouldBypass) "Motion bypass condition met" else null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in motion detection: ${e.message}", e)
            return MotionAnalysisResult(
                pixelDifference = 0f,
                isSignificantMotion = false,
                shouldProcess = true, // Fail open for safety
                threshold = threshold,
                processingTimeMs = System.currentTimeMillis() - startTime,
                motionRegions = 0,
                velocity = 0f,
                shouldBypass = false,
                bypassReason = "Motion detection error: ${e.message}"
            )
        }
    } 
   /**
     * Determine motion threshold based on context and configuration
     */
    private fun determineMotionThreshold(context: FilterContext): Float {
        // Check stage-specific configuration
        val stageConfig = getStageConfig(context)
        val configuredThreshold = stageConfig["motionThreshold"] as? Float
        
        if (configuredThreshold != null) {
            return configuredThreshold
        }
        
        // Use context-based threshold adjustment
        return when {
            context.isInCooldown -> LOW_MOTION_THRESHOLD // More sensitive during cooldown
            context.currentState == DetectionState.SCROLLING_AWAY -> HIGH_VELOCITY_THRESHOLD
            else -> NORMAL_MOTION_THRESHOLD
        }
    }
    
    /**
     * Check bypass conditions for motion detection
     */
    private fun checkBypassConditions(
        motionResult: MotionDetectionEngine.MotionResult,
        advancedAnalysis: MotionAnalyzer.MotionAnalysis?,
        context: FilterContext
    ): Boolean {
        if (!BYPASS_ENABLED) return false
        
        // Bypass if in certain states that require analysis regardless of motion
        when (context.currentState) {
            DetectionState.HARMFUL_DETECTED -> return true
            DetectionState.COOLDOWN -> return false // Respect cooldown
            else -> {}
        }
        
        // Bypass if advanced analysis recommends it
        advancedAnalysis?.let { analysis ->
            if (analysis.shouldForceAnalysis) {
                return true
            }
        }
        
        // Bypass if motion regions are concentrated (might indicate content change)
        if (motionResult.motionRegions.isNotEmpty()) {
            val totalArea = motionResult.motionRegions.sumOf { region ->
                (region.right - region.left) * (region.bottom - region.top)
            }
            // If motion is concentrated in small areas, might be content change
            if (totalArea < 10000) { // Arbitrary threshold for concentrated motion
                return true
            }
        }
        
        return false
    }
    
    /**
     * Update motion statistics and velocity tracking
     */
    private fun updateMotionStatistics(result: MotionAnalysisResult) {
        totalMotionChecks++
        
        if (result.isSignificantMotion) {
            significantMotionDetected++
        }
        
        if (result.shouldBypass) {
            bypassedFrames++
        }
        
        // Update velocity history
        if (VELOCITY_TRACKING_ENABLED && result.velocity > 0) {
            velocityHistory.add(result.velocity)
            if (velocityHistory.size > maxVelocityHistory) {
                velocityHistory.removeAt(0)
            }
        }
    }
    
    /**
     * Create filter result based on motion analysis
     */
    private fun createMotionResult(
        motionResult: MotionAnalysisResult,
        totalProcessingTime: Long
    ): FilterResult {
        val confidence = calculateMotionConfidence(motionResult)
        
        val metadata = mapOf(
            "pixelDifference" to motionResult.pixelDifference,
            "threshold" to motionResult.threshold,
            "isSignificantMotion" to motionResult.isSignificantMotion,
            "motionRegions" to motionResult.motionRegions,
            "velocity" to motionResult.velocity,
            "shouldBypass" to motionResult.shouldBypass,
            "frameCounter" to frameCounter,
            "averageVelocity" to if (velocityHistory.isNotEmpty()) {
                velocityHistory.average().toFloat()
            } else 0f
        )
        
        val reason = when {
            motionResult.shouldBypass -> motionResult.bypassReason ?: "Motion bypass enabled"
            motionResult.isSignificantMotion -> 
                "Significant motion detected (${String.format("%.3f", motionResult.pixelDifference)})"
            else -> 
                "Motion below threshold (${String.format("%.3f", motionResult.pixelDifference)})"
        }
        
        return if (motionResult.shouldProcess) {
            createPassResult(
                confidence = confidence,
                processingTimeMs = totalProcessingTime,
                reason = reason,
                metadata = metadata
            )
        } else {
            createFailResult(
                confidence = confidence,
                processingTimeMs = totalProcessingTime,
                reason = reason,
                shouldTerminate = false, // Don't terminate on motion - let other stages decide
                metadata = metadata
            )
        }
    }
    
    /**
     * Calculate confidence score for motion result
     */
    private fun calculateMotionConfidence(result: MotionAnalysisResult): Float {
        return when {
            result.shouldBypass -> 0.95f // High confidence for bypass
            result.isSignificantMotion -> {
                // Higher confidence for more significant motion
                val motionRatio = result.pixelDifference / result.threshold
                (0.7f + (motionRatio * 0.2f)).coerceAtMost(0.9f)
            }
            else -> {
                // Lower confidence when no significant motion
                val motionRatio = result.pixelDifference / result.threshold
                (0.5f + (motionRatio * 0.3f)).coerceAtMost(0.8f)
            }
        }
    } 
   /**
     * Get motion filter statistics
     */
    fun getMotionStats(): MotionFilterStats {
        val avgVelocity = if (velocityHistory.isNotEmpty()) {
            velocityHistory.average().toFloat()
        } else 0f
        
        return MotionFilterStats(
            totalChecks = totalMotionChecks,
            significantMotionDetected = significantMotionDetected,
            framesSkippedForVelocity = framesSkippedDueToVelocity,
            bypassedFrames = bypassedFrames,
            motionDetectionRate = if (totalMotionChecks > 0) {
                (significantMotionDetected.toFloat() / totalMotionChecks.toFloat()) * 100f
            } else 0f,
            averageVelocity = avgVelocity,
            currentVelocityHistorySize = velocityHistory.size,
            frameCounter = frameCounter
        )
    }
    
    /**
     * Configure motion detection parameters
     */
    fun configureMotion(
        normalThreshold: Float? = null,
        highVelocityThreshold: Float? = null,
        lowMotionThreshold: Float? = null,
        enableBypass: Boolean? = null,
        enableVelocityTracking: Boolean? = null
    ) {
        Log.d(TAG, "Motion configuration: " +
                "normal=${normalThreshold ?: NORMAL_MOTION_THRESHOLD}, " +
                "highVel=${highVelocityThreshold ?: HIGH_VELOCITY_THRESHOLD}, " +
                "lowMotion=${lowMotionThreshold ?: LOW_MOTION_THRESHOLD}, " +
                "bypass=${enableBypass ?: BYPASS_ENABLED}, " +
                "velTracking=${enableVelocityTracking ?: VELOCITY_TRACKING_ENABLED}")
    }
    
    /**
     * Get current velocity information
     */
    fun getCurrentVelocityInfo(): VelocityInfo {
        return VelocityInfo(
            currentVelocity = velocityHistory.lastOrNull() ?: 0f,
            averageVelocity = if (velocityHistory.isNotEmpty()) {
                velocityHistory.average().toFloat()
            } else 0f,
            maxVelocity = velocityHistory.maxOrNull() ?: 0f,
            historySize = velocityHistory.size,
            isHighVelocity = velocityHistory.lastOrNull()?.let { it > HIGH_VELOCITY_THRESHOLD } ?: false
        )
    }
    
    override fun onReset() {
        previousFrame = null
        frameCounter = 0L
        velocityHistory.clear()
        totalMotionChecks = 0L
        significantMotionDetected = 0L
        framesSkippedDueToVelocity = 0L
        bypassedFrames = 0L
        Log.d(TAG, "Motion filter stage reset - all state cleared")
    }
}

/**
 * Result of motion analysis operation
 */
data class MotionAnalysisResult(
    val pixelDifference: Float,
    val isSignificantMotion: Boolean,
    val shouldProcess: Boolean,
    val threshold: Float,
    val processingTimeMs: Long,
    val motionRegions: Int,
    val velocity: Float,
    val shouldBypass: Boolean,
    val bypassReason: String?
)

/**
 * Statistics for motion filter stage
 */
data class MotionFilterStats(
    val totalChecks: Long,
    val significantMotionDetected: Long,
    val framesSkippedForVelocity: Long,
    val bypassedFrames: Long,
    val motionDetectionRate: Float,
    val averageVelocity: Float,
    val currentVelocityHistorySize: Int,
    val frameCounter: Long
) {
    override fun toString(): String {
        return "MotionFilterStats(checks=$totalChecks, " +
                "motion=$significantMotionDetected, " +
                "skipped=$framesSkippedForVelocity, " +
                "bypassed=$bypassedFrames, " +
                "rate=${String.format("%.1f", motionDetectionRate)}%, " +
                "avgVel=${String.format("%.3f", averageVelocity)})"
    }
}

/**
 * Current velocity information
 */
data class VelocityInfo(
    val currentVelocity: Float,
    val averageVelocity: Float,
    val maxVelocity: Float,
    val historySize: Int,
    val isHighVelocity: Boolean
) {
    override fun toString(): String {
        return "VelocityInfo(current=${String.format("%.3f", currentVelocity)}, " +
                "avg=${String.format("%.3f", averageVelocity)}, " +
                "max=${String.format("%.3f", maxVelocity)}, " +
                "high=$isHighVelocity)"
    }
}