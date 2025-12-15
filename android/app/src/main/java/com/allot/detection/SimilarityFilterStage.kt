package com.allot.detection

import android.util.Log

/**
 * SimilarityFilterStage - Visual similarity pre-filter stage
 * 
 * Implements similarity-based filtering as the first pipeline stage with 90% threshold.
 * Provides fast similarity checking with early termination for performance optimization.
 * Supports content-type specific threshold configuration and tuning.
 * 
 * Performance Target: <2ms per check
 */
class SimilarityFilterStage(
    name: String = "SimilarityFilter",
    priority: Int = 1, // First stage in pipeline
    private val defaultThreshold: Float = 0.90f,
    private val similarityAnalyzer: SimilarityAnalyzer
) : BaseFilterStage(name, priority, targetProcessingTimeMs = 2L) {
    
    companion object {
        const val TAG = "SimilarityFilterStage"
        
        // Content-type specific thresholds
        const val SOCIAL_MEDIA_THRESHOLD = 0.92f
        const val VIDEO_CONTENT_THRESHOLD = 0.88f
        const val STATIC_CONTENT_THRESHOLD = 0.95f
        const val UNKNOWN_CONTENT_THRESHOLD = 0.90f
        
        // Performance optimization settings
        const val FAST_CHECK_ENABLED = true
        const val EARLY_TERMINATION_ENABLED = true
    }
    
    // Performance tracking
    private var totalSimilarityChecks = 0L
    private var totalSkippedFrames = 0L
    private var totalProcessedFrames = 0L
    
    // Content type detection cache
    private val contentTypeCache = mutableMapOf<String, ContentType>()
    
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
            
            // Generate or use existing fingerprint
            val fingerprint = frame.fingerprint ?: run {
                val startTime = System.currentTimeMillis()
                val generated = PerceptualHashGenerator.generateFrameFingerprint(
                    bitmap = frame.bitmap,
                    timestamp = frame.timestamp
                )
                val fingerprintTime = System.currentTimeMillis() - startTime
                
                if (fingerprintTime > 3) {
                    Log.w(TAG, "Fingerprint generation took ${fingerprintTime}ms (target: <3ms)")
                }
                
                generated
            }
            
            // Determine content-specific threshold
            val threshold = determineThreshold(frame, context)
            
            // Perform fast similarity check
            val similarityResult = performSimilarityCheck(fingerprint, threshold, context)
            
            // Update statistics
            updateStatistics(similarityResult)
            
            // Create result based on similarity check
            createSimilarityResult(similarityResult, threshold, similarityResult.processingTimeMs)
        }
        
        if (isVerboseLoggingEnabled(context)) {
            logPerformanceMetrics(processingTime, result.confidence, result.reason)
        }
        
        return result
    }
    
    /**
     * Determine appropriate similarity threshold based on content type and context
     */
    private fun determineThreshold(frame: CapturedFrame, context: FilterContext): Float {
        // Check stage-specific configuration first
        val stageConfig = getStageConfig(context)
        val configuredThreshold = stageConfig["threshold"] as? Float
        
        if (configuredThreshold != null) {
            return configuredThreshold
        }
        
        // Detect content type and use appropriate threshold
        val contentType = detectContentType(frame, context)
        
        return when (contentType) {
            ContentType.SOCIAL_MEDIA -> SOCIAL_MEDIA_THRESHOLD
            ContentType.VIDEO_CONTENT -> VIDEO_CONTENT_THRESHOLD
            ContentType.STATIC_CONTENT -> STATIC_CONTENT_THRESHOLD
            ContentType.UNKNOWN -> UNKNOWN_CONTENT_THRESHOLD
        }
    }
    
    /**
     * Detect content type for threshold optimization
     */
    private fun detectContentType(frame: CapturedFrame, context: FilterContext): ContentType {
        // Use cached result if available
        val cacheKey = "${frame.bitmap.width}x${frame.bitmap.height}"
        contentTypeCache[cacheKey]?.let { return it }
        
        // Simple heuristic-based content type detection
        val contentType = when {
            // Social media: typically square or portrait, medium resolution
            frame.bitmap.width in 300..800 && frame.bitmap.height in 300..1200 -> {
                ContentType.SOCIAL_MEDIA
            }
            // Video content: typically landscape, various resolutions
            frame.bitmap.width > frame.bitmap.height && frame.bitmap.width >= 640 -> {
                ContentType.VIDEO_CONTENT
            }
            // Static content: very consistent dimensions or very high resolution
            frame.bitmap.width == frame.bitmap.height || 
            (frame.bitmap.width * frame.bitmap.height) > 1920 * 1080 -> {
                ContentType.STATIC_CONTENT
            }
            else -> ContentType.UNKNOWN
        }
        
        // Cache the result
        contentTypeCache[cacheKey] = contentType
        
        return contentType
    }
    
    /**
     * Perform optimized similarity check
     */
    private fun performSimilarityCheck(
        fingerprint: FrameFingerprint,
        threshold: Float,
        context: FilterContext
    ): SimilarityCheckResult {
        val startTime = System.currentTimeMillis()
        
        try {
            // Fast check: Look for very similar frames first
            if (FAST_CHECK_ENABLED) {
                val hasSimilar = similarityAnalyzer.hasSimilarFrame(fingerprint, threshold)
                val checkTime = System.currentTimeMillis() - startTime
                
                if (hasSimilar) {
                    // Found similar frame, skip processing
                    return SimilarityCheckResult(
                        shouldProcess = false,
                        similarity = threshold + 0.01f, // Estimate above threshold
                        processingTimeMs = checkTime,
                        reason = "Fast similarity check found similar frame",
                        matchFound = true
                    )
                }
            }
            
            // Detailed check: Get exact similarity if fast check didn't find match
            val mostSimilar = similarityAnalyzer.findMostSimilar(fingerprint, 0.5f)
            val checkTime = System.currentTimeMillis() - startTime
            
            return if (mostSimilar != null && mostSimilar.similarity >= threshold) {
                SimilarityCheckResult(
                    shouldProcess = false,
                    similarity = mostSimilar.similarity,
                    processingTimeMs = checkTime,
                    reason = "Similar frame found (${String.format("%.3f", mostSimilar.similarity)})",
                    matchFound = true,
                    ageMs = mostSimilar.ageMs
                )
            } else {
                SimilarityCheckResult(
                    shouldProcess = true,
                    similarity = mostSimilar?.similarity ?: 0f,
                    processingTimeMs = checkTime,
                    reason = if (mostSimilar != null) {
                        "Similarity below threshold (${String.format("%.3f", mostSimilar.similarity)})"
                    } else {
                        "No similar frame found"
                    },
                    matchFound = false
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in similarity check: ${e.message}", e)
            return SimilarityCheckResult(
                shouldProcess = true, // Fail open for safety
                similarity = 0f,
                processingTimeMs = System.currentTimeMillis() - startTime,
                reason = "Similarity check error: ${e.message}",
                matchFound = false
            )
        }
    }
    
    /**
     * Create filter result based on similarity check
     */
    private fun createSimilarityResult(
        similarityResult: SimilarityCheckResult,
        threshold: Float,
        totalProcessingTime: Long
    ): FilterResult {
        val confidence = calculateConfidence(similarityResult, threshold)
        
        val metadata = mapOf(
            "similarity" to similarityResult.similarity,
            "threshold" to threshold,
            "matchFound" to similarityResult.matchFound,
            "checkTimeMs" to similarityResult.processingTimeMs,
            "fastCheckEnabled" to FAST_CHECK_ENABLED
        ).let { baseMetadata ->
            if (similarityResult.ageMs != null) {
                baseMetadata + ("matchAgeMs" to similarityResult.ageMs)
            } else {
                baseMetadata
            }
        }
        
        return if (similarityResult.shouldProcess) {
            createPassResult(
                confidence = confidence,
                processingTimeMs = totalProcessingTime,
                reason = similarityResult.reason,
                metadata = metadata
            )
        } else {
            createFailResult(
                confidence = confidence,
                processingTimeMs = totalProcessingTime,
                reason = similarityResult.reason,
                shouldTerminate = EARLY_TERMINATION_ENABLED,
                metadata = metadata
            )
        }
    }
    
    /**
     * Calculate confidence score based on similarity result
     */
    private fun calculateConfidence(result: SimilarityCheckResult, threshold: Float): Float {
        return when {
            !result.matchFound -> 0.95f // High confidence when no match found
            result.similarity >= threshold + 0.05f -> 0.90f // Very high similarity
            result.similarity >= threshold -> 0.85f // Above threshold
            result.similarity >= threshold - 0.05f -> 0.70f // Close to threshold
            else -> 0.80f // Below threshold
        }
    }
    
    /**
     * Update internal statistics
     */
    private fun updateStatistics(result: SimilarityCheckResult) {
        totalSimilarityChecks++
        
        if (result.shouldProcess) {
            totalProcessedFrames++
        } else {
            totalSkippedFrames++
        }
    }
    
    /**
     * Get similarity filter statistics
     */
    fun getSimilarityStats(): SimilarityFilterStats {
        return SimilarityFilterStats(
            totalChecks = totalSimilarityChecks,
            framesSkipped = totalSkippedFrames,
            framesProcessed = totalProcessedFrames,
            skipRate = if (totalSimilarityChecks > 0) {
                (totalSkippedFrames.toFloat() / totalSimilarityChecks.toFloat()) * 100f
            } else 0f,
            averageThreshold = defaultThreshold,
            contentTypeCacheSize = contentTypeCache.size
        )
    }
    
    /**
     * Configure similarity thresholds for different content types
     */
    fun configureThresholds(
        socialMedia: Float? = null,
        videoContent: Float? = null,
        staticContent: Float? = null,
        unknown: Float? = null
    ) {
        // This would update the threshold constants in a real implementation
        // For now, we log the configuration
        Log.d(TAG, "Threshold configuration: " +
                "social=${socialMedia ?: SOCIAL_MEDIA_THRESHOLD}, " +
                "video=${videoContent ?: VIDEO_CONTENT_THRESHOLD}, " +
                "static=${staticContent ?: STATIC_CONTENT_THRESHOLD}, " +
                "unknown=${unknown ?: UNKNOWN_CONTENT_THRESHOLD}")
    }
    
    override fun onReset() {
        totalSimilarityChecks = 0L
        totalSkippedFrames = 0L
        totalProcessedFrames = 0L
        contentTypeCache.clear()
        Log.d(TAG, "Similarity filter stage reset - statistics cleared")
    }
}

/**
 * Content type enumeration for threshold optimization
 */
enum class ContentType {
    SOCIAL_MEDIA,
    VIDEO_CONTENT,
    STATIC_CONTENT,
    UNKNOWN
}

/**
 * Result of similarity check operation
 */
data class SimilarityCheckResult(
    val shouldProcess: Boolean,
    val similarity: Float,
    val processingTimeMs: Long,
    val reason: String,
    val matchFound: Boolean,
    val ageMs: Long? = null
)

/**
 * Statistics for similarity filter stage
 */
data class SimilarityFilterStats(
    val totalChecks: Long,
    val framesSkipped: Long,
    val framesProcessed: Long,
    val skipRate: Float,
    val averageThreshold: Float,
    val contentTypeCacheSize: Int
) {
    override fun toString(): String {
        return "SimilarityFilterStats(checks=$totalChecks, " +
                "skipped=$framesSkipped, processed=$framesProcessed, " +
                "skipRate=${String.format("%.1f", skipRate)}%, " +
                "avgThreshold=${String.format("%.3f", averageThreshold)})"
    }
}