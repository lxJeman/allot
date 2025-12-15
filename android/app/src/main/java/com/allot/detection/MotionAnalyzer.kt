package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Rect
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * Motion Analyzer that integrates motion detection with frame similarity analysis.
 * Provides intelligent motion-based decisions for content analysis with advanced
 * threshold evaluation and adaptive learning capabilities.
 */
class MotionAnalyzer(
    private val motionEngine: MotionDetectionEngine = MotionDetectionEngine(),
    private val thresholdEvaluator: MotionThresholdEvaluator = MotionThresholdEvaluator(),
    private val config: MotionAnalysisConfig = MotionAnalysisConfig()
) {
    
    data class MotionAnalysisConfig(
        val motionHistorySize: Int = 5,
        val velocityThreshold: Float = 0.5f,
        val consistentMotionThreshold: Int = 3,
        val motionDecayFactor: Float = 0.8f,
        val maxFrameAge: Long = 5000L // 5 seconds
    )
    
    data class MotionFrame(
        val timestamp: Long,
        val motionResult: MotionDetectionEngine.MotionResult,
        val frameFingerprint: FrameFingerprint?
    )
    
    data class MotionAnalysis(
        val currentMotion: MotionDetectionEngine.MotionResult,
        val motionVelocity: Float,
        val isConsistentMotion: Boolean,
        val shouldForceAnalysis: Boolean,
        val shouldReduceFrequency: Boolean,
        val analysisRecommendation: AnalysisRecommendation,
        val thresholdEvaluation: MotionThresholdEvaluator.MotionEvaluation,
        val adaptiveFrequency: Float,
        val contentType: MotionThresholdEvaluator.ContentType
    )
    
    enum class AnalysisRecommendation {
        PROCEED_NORMAL,     // Normal analysis
        FORCE_ANALYSIS,     // Motion detected, force analysis
        REDUCE_FREQUENCY,   // High motion, reduce frequency
        SKIP_SIMILAR        // Low motion, can skip similar frames
    }
    
    private val motionHistory = ConcurrentLinkedQueue<MotionFrame>()
    private val totalFramesProcessed = AtomicLong(0)
    private val motionFramesDetected = AtomicLong(0)
    
    /**
     * Analyzes motion between current and previous frame with advanced threshold evaluation
     */
    fun analyzeMotion(
        currentBitmap: Bitmap,
        previousBitmap: Bitmap?,
        currentFingerprint: FrameFingerprint,
        contentType: MotionThresholdEvaluator.ContentType = MotionThresholdEvaluator.ContentType.UNKNOWN
    ): MotionAnalysis {
        val timestamp = System.currentTimeMillis()
        totalFramesProcessed.incrementAndGet()
        
        // If no previous frame, create default evaluation
        if (previousBitmap == null) {
            val noMotionResult = MotionDetectionEngine.MotionResult(
                pixelDifference = 0f,
                isSignificantMotion = false,
                motionRegions = emptyList(),
                processingTimeMs = 0L
            )
            
            // Still evaluate with threshold evaluator for learning
            val thresholdEvaluation = thresholdEvaluator.evaluateMotion(
                noMotionResult, 
                contentType, 
                currentFingerprint
            )
            
            return MotionAnalysis(
                currentMotion = noMotionResult,
                motionVelocity = 0f,
                isConsistentMotion = false,
                shouldForceAnalysis = true, // First frame should be analyzed
                shouldReduceFrequency = false,
                analysisRecommendation = AnalysisRecommendation.PROCEED_NORMAL,
                thresholdEvaluation = thresholdEvaluation,
                adaptiveFrequency = 1.0f,
                contentType = thresholdEvaluation.contentType
            )
        }
        
        // Detect motion between frames
        val motionResult = motionEngine.detectMotion(currentBitmap, previousBitmap)
        
        // Perform advanced threshold evaluation
        val thresholdEvaluation = thresholdEvaluator.evaluateMotion(
            motionResult, 
            contentType, 
            currentFingerprint
        )
        
        if (thresholdEvaluation.isSignificantMotion) {
            motionFramesDetected.incrementAndGet()
        }
        
        // Add to motion history
        val motionFrame = MotionFrame(timestamp, motionResult, currentFingerprint)
        addToHistory(motionFrame)
        
        // Calculate motion velocity and consistency (legacy method)
        val legacyMotionVelocity = calculateMotionVelocity()
        val isConsistentMotion = isConsistentMotion()
        
        // Use advanced velocity from threshold evaluator
        val advancedVelocity = thresholdEvaluation.motionVelocity
        
        // Make enhanced analysis recommendations
        val (shouldForceAnalysis, shouldReduceFrequency, recommendation) = 
            makeEnhancedAnalysisRecommendation(
                motionResult, 
                thresholdEvaluation, 
                legacyMotionVelocity, 
                isConsistentMotion
            )
        
        return MotionAnalysis(
            currentMotion = motionResult,
            motionVelocity = advancedVelocity,
            isConsistentMotion = isConsistentMotion,
            shouldForceAnalysis = shouldForceAnalysis,
            shouldReduceFrequency = shouldReduceFrequency,
            analysisRecommendation = recommendation,
            thresholdEvaluation = thresholdEvaluation,
            adaptiveFrequency = thresholdEvaluation.recommendedAnalysisFrequency,
            contentType = thresholdEvaluation.contentType
        )
    }
    
    /**
     * Adds motion frame to history and maintains size limit
     */
    private fun addToHistory(motionFrame: MotionFrame) {
        motionHistory.offer(motionFrame)
        
        // Remove old frames
        while (motionHistory.size > config.motionHistorySize) {
            motionHistory.poll()
        }
        
        // Remove frames older than max age
        val cutoffTime = System.currentTimeMillis() - config.maxFrameAge
        val iterator = motionHistory.iterator()
        while (iterator.hasNext()) {
            val frame = iterator.next()
            if (frame.timestamp < cutoffTime) {
                iterator.remove()
            }
        }
    }
    
    /**
     * Calculates motion velocity based on recent motion history
     */
    private fun calculateMotionVelocity(): Float {
        if (motionHistory.size < 2) return 0f
        
        val framesList = ArrayList(motionHistory)
        val recentFrames = if (framesList.size > 3) {
            framesList.subList(framesList.size - 3, framesList.size)
        } else {
            framesList
        }
        
        var totalVelocity = 0f
        var validPairs = 0
        
        for (i in 1 until recentFrames.size) {
            val current = recentFrames[i]
            val previous = recentFrames[i - 1]
            
            val timeDiff = (current.timestamp - previous.timestamp).toFloat()
            if (timeDiff > 0) {
                val motionDiff = current.motionResult.pixelDifference - previous.motionResult.pixelDifference
                val velocity = kotlin.math.abs(motionDiff) / (timeDiff / 1000f) // per second
                
                totalVelocity += velocity
                validPairs++
            }
        }
        
        return if (validPairs > 0) totalVelocity / validPairs else 0f
    }
    
    /**
     * Determines if motion is consistent across recent frames
     */
    private fun isConsistentMotion(): Boolean {
        if (motionHistory.size < config.consistentMotionThreshold) return false
        
        val framesList = ArrayList(motionHistory)
        val recentFrames = if (framesList.size > config.consistentMotionThreshold) {
            framesList.subList(framesList.size - config.consistentMotionThreshold, framesList.size)
        } else {
            framesList
        }
        
        val motionFrames = recentFrames.count { frame -> frame.motionResult.isSignificantMotion }
        
        return motionFrames >= config.consistentMotionThreshold
    }
    
    /**
     * Makes analysis recommendation based on motion characteristics (legacy method)
     */
    private fun makeAnalysisRecommendation(
        motionResult: MotionDetectionEngine.MotionResult,
        motionVelocity: Float,
        isConsistentMotion: Boolean
    ): Triple<Boolean, Boolean, AnalysisRecommendation> {
        
        // High velocity motion - reduce frequency
        if (motionVelocity > config.velocityThreshold) {
            return Triple(false, true, AnalysisRecommendation.REDUCE_FREQUENCY)
        }
        
        // Significant motion detected - force analysis
        if (motionResult.isSignificantMotion) {
            return Triple(true, false, AnalysisRecommendation.FORCE_ANALYSIS)
        }
        
        // Consistent motion pattern - reduce frequency
        if (isConsistentMotion) {
            return Triple(false, true, AnalysisRecommendation.REDUCE_FREQUENCY)
        }
        
        // Low motion - can skip similar frames
        if (motionResult.pixelDifference < 0.1f) {
            return Triple(false, false, AnalysisRecommendation.SKIP_SIMILAR)
        }
        
        // Default - proceed normally
        return Triple(false, false, AnalysisRecommendation.PROCEED_NORMAL)
    }
    
    /**
     * Makes enhanced analysis recommendation using advanced threshold evaluation
     */
    private fun makeEnhancedAnalysisRecommendation(
        motionResult: MotionDetectionEngine.MotionResult,
        thresholdEvaluation: MotionThresholdEvaluator.MotionEvaluation,
        legacyVelocity: Float,
        isConsistentMotion: Boolean
    ): Triple<Boolean, Boolean, AnalysisRecommendation> {
        
        // Use threshold evaluator's force analysis recommendation
        if (thresholdEvaluation.shouldForceAnalysis) {
            return Triple(true, false, AnalysisRecommendation.FORCE_ANALYSIS)
        }
        
        // Check motion significance level
        when (thresholdEvaluation.motionSignificance) {
            MotionThresholdEvaluator.MotionSignificance.EXTREME -> {
                return Triple(true, true, AnalysisRecommendation.FORCE_ANALYSIS)
            }
            MotionThresholdEvaluator.MotionSignificance.HIGH -> {
                return Triple(false, true, AnalysisRecommendation.REDUCE_FREQUENCY)
            }
            MotionThresholdEvaluator.MotionSignificance.MODERATE -> {
                // Use adaptive frequency recommendation
                val shouldReduce = thresholdEvaluation.recommendedAnalysisFrequency < 0.8f
                return Triple(false, shouldReduce, AnalysisRecommendation.PROCEED_NORMAL)
            }
            MotionThresholdEvaluator.MotionSignificance.LOW -> {
                return Triple(false, false, AnalysisRecommendation.SKIP_SIMILAR)
            }
            MotionThresholdEvaluator.MotionSignificance.NEGLIGIBLE -> {
                return Triple(false, false, AnalysisRecommendation.SKIP_SIMILAR)
            }
        }
    }
    
    /**
     * Gets motion detection statistics
     */
    fun getMotionStats(): MotionStats {
        val totalFrames = totalFramesProcessed.get()
        val motionFrames = motionFramesDetected.get()
        val motionRate = if (totalFrames > 0) motionFrames.toFloat() / totalFrames else 0f
        
        val framesList = ArrayList(motionHistory)
        val recentMotionRate = if (framesList.isNotEmpty()) {
            val recentMotionFrames = framesList.count { frame -> frame.motionResult.isSignificantMotion }
            recentMotionFrames.toFloat() / framesList.size
        } else 0f
        
        val averageProcessingTime = if (framesList.isNotEmpty()) {
            framesList.map { frame -> frame.motionResult.processingTimeMs }.average().toFloat()
        } else 0f
        
        return MotionStats(
            totalFramesProcessed = totalFrames,
            motionFramesDetected = motionFrames,
            motionDetectionRate = motionRate,
            recentMotionRate = recentMotionRate,
            averageProcessingTimeMs = averageProcessingTime,
            historySize = motionHistory.size
        )
    }
    
    /**
     * Gets enhanced motion analysis statistics including threshold evaluation
     */
    fun getEnhancedMotionStats(): EnhancedMotionStats {
        val basicStats = getMotionStats()
        val evaluationStats = thresholdEvaluator.getEvaluationStats()
        
        return EnhancedMotionStats(
            basicStats = basicStats,
            evaluationStats = evaluationStats,
            adaptiveThresholdActive = evaluationStats.adaptiveAdjustment != 0f,
            contentTypeDetected = evaluationStats.detectedContentType != MotionThresholdEvaluator.ContentType.UNKNOWN,
            averageAdaptiveFrequency = calculateAverageAdaptiveFrequency()
        )
    }
    
    /**
     * Calculates average adaptive frequency from recent motion history
     */
    private fun calculateAverageAdaptiveFrequency(): Float {
        // This would require storing frequency recommendations in history
        // For now, return a placeholder based on recent motion patterns
        val framesList = ArrayList(motionHistory)
        if (framesList.isEmpty()) return 1.0f
        
        val recentFrames = framesList.takeLast(5)
        val averageMotion = recentFrames.map { it.motionResult.pixelDifference }.average().toFloat()
        
        // Estimate frequency based on motion level
        return when {
            averageMotion > 0.5f -> 0.3f  // High motion, low frequency
            averageMotion > 0.25f -> 0.6f // Medium motion, reduced frequency
            else -> 1.2f                  // Low motion, increased frequency
        }
    }
    
    /**
     * Clears motion history (useful for testing or reset)
     */
    fun clearHistory() {
        motionHistory.clear()
    }
    
    data class MotionStats(
        val totalFramesProcessed: Long,
        val motionFramesDetected: Long,
        val motionDetectionRate: Float,
        val recentMotionRate: Float,
        val averageProcessingTimeMs: Float,
        val historySize: Int
    )
    
    data class EnhancedMotionStats(
        val basicStats: MotionStats,
        val evaluationStats: MotionThresholdEvaluator.EvaluationStats,
        val adaptiveThresholdActive: Boolean,
        val contentTypeDetected: Boolean,
        val averageAdaptiveFrequency: Float
    ) {
        override fun toString(): String {
            return "EnhancedMotionStats(" +
                    "frames=${basicStats.totalFramesProcessed}, " +
                    "motion_rate=${String.format("%.2f", basicStats.motionDetectionRate)}, " +
                    "content_type=${evaluationStats.detectedContentType}, " +
                    "adaptive_freq=${String.format("%.2f", averageAdaptiveFrequency)}, " +
                    "threshold_adj=${String.format("%.3f", evaluationStats.adaptiveAdjustment)})"
        }
    }
}