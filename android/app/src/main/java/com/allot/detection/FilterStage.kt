package com.allot.detection

import android.graphics.Bitmap

/**
 * FilterStage - Interface for pluggable pre-filter stages
 * 
 * Defines the contract for individual filter stages in the pre-filter pipeline.
 * Each stage can evaluate a frame and decide whether to continue processing.
 */
interface FilterStage {
    /**
     * Unique name for this filter stage
     */
    val name: String
    
    /**
     * Priority of this stage (lower numbers execute first)
     */
    val priority: Int
    
    /**
     * Target processing time in milliseconds
     */
    val targetProcessingTimeMs: Long
    
    /**
     * Filter a frame and return the result
     * 
     * @param frame The captured frame to filter
     * @param context Additional context for filtering decisions
     * @return FilterResult indicating whether to continue processing
     */
    fun filter(frame: CapturedFrame, context: FilterContext): FilterResult
    
    /**
     * Get performance statistics for this filter stage
     */
    fun getPerformanceStats(): FilterStageStats
    
    /**
     * Reset any internal state or statistics
     */
    fun reset()
}

/**
 * CapturedFrame - Represents a frame to be filtered
 */
data class CapturedFrame(
    val bitmap: Bitmap,
    val timestamp: Long,
    val fingerprint: FrameFingerprint? = null,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * FilterContext - Context information for filter decisions
 */
data class FilterContext(
    val previousFrame: CapturedFrame? = null,
    val recentFrames: List<CapturedFrame> = emptyList(),
    val currentState: DetectionState = DetectionState.SCANNING,
    val timeSinceLastAnalysis: Long = 0L,
    val timeSinceLastHarmful: Long = 0L,
    val isInCooldown: Boolean = false,
    val analysisHistory: List<PreviousAnalysisResult> = emptyList(),
    val configuration: FilterConfiguration = FilterConfiguration()
)

/**
 * FilterResult - Result of a filter stage evaluation
 */
data class FilterResult(
    val stageName: String,
    val passed: Boolean,
    val confidence: Float,
    val processingTimeMs: Long,
    val reason: String,
    val metadata: Map<String, Any> = emptyMap(),
    val shouldTerminateEarly: Boolean = false
) {
    /**
     * Check if this result indicates the frame should be processed
     */
    fun shouldContinue(): Boolean = passed && !shouldTerminateEarly
    
    /**
     * Get a summary string for logging
     */
    fun getSummary(): String {
        return "$stageName: ${if (passed) "PASS" else "FAIL"} (${confidence * 100}% confidence, ${processingTimeMs}ms) - $reason"
    }
}

/**
 * FilterStageStats - Performance statistics for a filter stage
 */
data class FilterStageStats(
    val stageName: String,
    val totalExecutions: Long,
    val totalPassedFrames: Long,
    val totalFailedFrames: Long,
    val averageProcessingTimeMs: Float,
    val maxProcessingTimeMs: Long,
    val averageConfidence: Float,
    val lastExecutionTimeMs: Long,
    val performanceAlerts: List<String> = emptyList()
) {
    /**
     * Calculate pass rate as percentage
     */
    fun getPassRate(): Float {
        return if (totalExecutions > 0) {
            (totalPassedFrames.toFloat() / totalExecutions.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * Check if performance meets requirements
     */
    fun meetsPerformanceRequirements(targetTimeMs: Long): Boolean {
        return averageProcessingTimeMs <= targetTimeMs && maxProcessingTimeMs <= (targetTimeMs * 2)
    }
}

/**
 * FilterConfiguration - Configuration for filter stages
 */
data class FilterConfiguration(
    // Similarity thresholds
    val similarityThreshold: Float = 0.90f,
    val motionThreshold: Float = 0.25f,
    val textDensityThreshold: Float = 0.1f,
    
    // Performance targets
    val maxProcessingTimeMs: Long = 10L,
    val targetProcessingTimeMs: Long = 5L,
    
    // Pipeline behavior
    val enableEarlyTermination: Boolean = true,
    val enablePerformanceMonitoring: Boolean = true,
    val enableVerboseLogging: Boolean = false,
    
    // Stage-specific settings
    val stageConfigurations: Map<String, Map<String, Any>> = emptyMap()
) {
    /**
     * Get configuration for a specific stage
     */
    fun getStageConfig(stageName: String): Map<String, Any> {
        return stageConfigurations[stageName] ?: emptyMap()
    }
}

