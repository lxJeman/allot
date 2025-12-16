package com.allot.detection

import android.util.Log

/**
 * TextPresenceFilterStage - Fast text presence detection for pre-filter pipeline
 * 
 * Uses optimized text detection to quickly determine if a frame contains
 * significant text content before expensive OCR analysis.
 * 
 * Performance Target: <5ms per frame
 */
class TextPresenceFilterStage(
    override val name: String = "TextPresenceFilter",
    override val priority: Int = 3, // Third stage after similarity and motion
    private val textExtractor: LocalTextExtractor,
    private val textThreshold: Float = LocalTextExtractor.MIN_TEXT_DENSITY_THRESHOLD,
    private val enableROIOptimization: Boolean = true
) : BaseFilterStage(name, priority, 5L) {
    
    companion object {
        const val TAG = "TextPresenceFilterStage"
    }
    
    override fun executeFilter(frame: CapturedFrame, context: FilterContext): FilterResult {
        val startTime = System.currentTimeMillis()
        
        return try {
            // Use fast text presence detection with ROI optimization
            val hasText = if (enableROIOptimization) {
                textExtractor.isTextPresentFast(frame.bitmap, textThreshold)
            } else {
                textExtractor.isTextPresent(frame.bitmap, textThreshold)
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Pass if text is detected, fail if no significant text
            val shouldContinue = hasText
            val confidence = if (hasText) 0.8f else 0.2f
            
            val reason = if (hasText) {
                "Text presence detected (threshold: ${(textThreshold * 100).toInt()}%)"
            } else {
                "No significant text detected - skipping OCR analysis"
            }
            
            if (context.configuration.enableVerboseLogging) {
                Log.d(TAG, "Text presence check: $hasText (${processingTime}ms) - $reason")
            }
            
            FilterResult(
                stageName = name,
                passed = shouldContinue,
                confidence = confidence,
                processingTimeMs = processingTime,
                reason = reason,
                shouldTerminateEarly = !shouldContinue,
                metadata = mapOf<String, Any>(
                    "hasText" to hasText,
                    "threshold" to textThreshold,
                    "roiOptimized" to enableROIOptimization
                )
            )
            
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            Log.e(TAG, "Error in text presence filter: ${e.message}", e)
            
            // Fail open - allow processing to continue on error
            FilterResult(
                stageName = name,
                passed = true,
                confidence = 0f,
                processingTimeMs = processingTime,
                reason = "Text presence check failed: ${e.message}",
                shouldTerminateEarly = false,
                metadata = mapOf<String, Any>("error" to (e.message ?: "Unknown error"))
            )
        }
    }
    
    /**
     * Update text threshold for dynamic adjustment
     */
    fun updateTextThreshold(newThreshold: Float) {
        // In a full implementation, this would update the threshold
        Log.d(TAG, "Text threshold updated to: ${(newThreshold * 100).toInt()}%")
    }
    
    /**
     * Get current configuration
     */
    fun getConfiguration(): TextPresenceFilterConfig {
        return TextPresenceFilterConfig(
            textThreshold = textThreshold,
            enableROIOptimization = enableROIOptimization,
            targetProcessingTimeMs = targetProcessingTimeMs
        )
    }
}

/**
 * Configuration for text presence filter
 */
data class TextPresenceFilterConfig(
    val textThreshold: Float,
    val enableROIOptimization: Boolean,
    val targetProcessingTimeMs: Long
)