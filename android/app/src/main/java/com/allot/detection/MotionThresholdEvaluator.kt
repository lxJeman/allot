package com.allot.detection

import kotlin.math.*

/**
 * Motion Threshold Evaluator - Advanced motion analysis with adaptive thresholds
 * 
 * Implements sophisticated motion significance evaluation, adaptive threshold adjustment,
 * and motion-based analysis frequency recommendations. This component enhances the basic
 * motion detection with intelligent content-aware analysis.
 * 
 * Requirements addressed:
 * - 3.2: Motion significance evaluation logic
 * - 3.3: Adaptive threshold adjustment based on content type
 * - 3.5: Motion-based analysis frequency adjustment
 */
class MotionThresholdEvaluator(
    private val config: ThresholdConfig = ThresholdConfig()
) {
    
    data class ThresholdConfig(
        // Base thresholds
        val baseMotionThreshold: Float = 0.25f,
        val significantMotionThreshold: Float = 0.4f,
        val highMotionThreshold: Float = 0.6f,
        
        // Content type adjustments
        val socialMediaAdjustment: Float = 0.8f,    // More sensitive for social media
        val videoContentAdjustment: Float = 1.2f,   // Less sensitive for video
        val staticContentAdjustment: Float = 0.6f,  // Very sensitive for static content
        
        // Velocity thresholds
        val lowVelocityThreshold: Float = 0.1f,
        val mediumVelocityThreshold: Float = 0.3f,
        val highVelocityThreshold: Float = 0.5f,
        
        // Frequency adjustment factors
        val highMotionFrequencyReduction: Float = 0.3f,  // Analyze every 3rd frame
        val mediumMotionFrequencyReduction: Float = 0.5f, // Analyze every 2nd frame
        val lowMotionFrequencyIncrease: Float = 1.5f,     // Analyze more frequently
        
        // Adaptive learning
        val adaptationRate: Float = 0.1f,
        val confidenceThreshold: Float = 0.8f,
        val historyWindowSize: Int = 10
    )
    
    enum class ContentType {
        SOCIAL_MEDIA,    // Instagram, TikTok, etc.
        VIDEO_CONTENT,   // YouTube, streaming
        STATIC_CONTENT,  // Text, images
        UNKNOWN
    }
    
    enum class MotionSignificance {
        NEGLIGIBLE,      // < 10% threshold
        LOW,            // 10-25% threshold  
        MODERATE,       // 25-40% threshold
        HIGH,           // 40-60% threshold
        EXTREME         // > 60% threshold
    }
    
    data class MotionEvaluation(
        val rawMotionValue: Float,
        val adjustedThreshold: Float,
        val motionSignificance: MotionSignificance,
        val contentType: ContentType,
        val motionVelocity: Float,
        val isSignificantMotion: Boolean,
        val confidenceScore: Float,
        val recommendedAnalysisFrequency: Float,
        val shouldForceAnalysis: Boolean,
        val adaptiveAdjustment: Float
    )
    
    data class VelocityMetrics(
        val instantVelocity: Float,
        val averageVelocity: Float,
        val velocityTrend: VelocityTrend,
        val acceleration: Float
    )
    
    enum class VelocityTrend {
        ACCELERATING,
        DECELERATING, 
        STABLE,
        OSCILLATING
    }
    
    // Motion history for velocity and trend analysis
    private val motionHistory = mutableListOf<MotionDataPoint>()
    private var adaptiveThresholdAdjustment = 0f
    private var contentTypeConfidence = 0f
    private var detectedContentType = ContentType.UNKNOWN
    
    data class MotionDataPoint(
        val timestamp: Long,
        val motionValue: Float,
        val contentType: ContentType,
        val processingTimeMs: Long
    )
    
    /**
     * Evaluates motion significance with adaptive thresholds and content awareness
     */
    fun evaluateMotion(
        motionResult: MotionDetectionEngine.MotionResult,
        contentType: ContentType = ContentType.UNKNOWN,
        frameFingerprint: FrameFingerprint? = null
    ): MotionEvaluation {
        val timestamp = System.currentTimeMillis()
        
        // Add to motion history
        addToHistory(MotionDataPoint(
            timestamp = timestamp,
            motionValue = motionResult.pixelDifference,
            contentType = contentType,
            processingTimeMs = motionResult.processingTimeMs
        ))
        
        // Detect content type if unknown
        val detectedType = if (contentType == ContentType.UNKNOWN) {
            detectContentType(motionResult, frameFingerprint)
        } else {
            contentType
        }
        
        // Calculate adaptive threshold
        val adaptiveThreshold = calculateAdaptiveThreshold(detectedType)
        
        // Calculate motion velocity metrics
        val velocityMetrics = calculateVelocityMetrics()
        
        // Determine motion significance
        val significance = determineMotionSignificance(
            motionResult.pixelDifference, 
            adaptiveThreshold
        )
        
        // Calculate confidence score
        val confidence = calculateConfidenceScore(
            motionResult, 
            velocityMetrics, 
            detectedType
        )
        
        // Determine if motion is significant
        val isSignificant = motionResult.pixelDifference >= adaptiveThreshold
        
        // Calculate recommended analysis frequency
        val analysisFrequency = calculateAnalysisFrequency(
            significance,
            velocityMetrics,
            detectedType
        )
        
        // Determine if analysis should be forced
        val shouldForce = shouldForceAnalysis(
            significance,
            velocityMetrics,
            confidence
        )
        
        // Update adaptive learning
        updateAdaptiveLearning(motionResult.pixelDifference, detectedType, confidence)
        
        return MotionEvaluation(
            rawMotionValue = motionResult.pixelDifference,
            adjustedThreshold = adaptiveThreshold,
            motionSignificance = significance,
            contentType = detectedType,
            motionVelocity = velocityMetrics.instantVelocity,
            isSignificantMotion = isSignificant,
            confidenceScore = confidence,
            recommendedAnalysisFrequency = analysisFrequency,
            shouldForceAnalysis = shouldForce,
            adaptiveAdjustment = adaptiveThresholdAdjustment
        )
    }
    
    /**
     * Calculates adaptive threshold based on content type and learning
     */
    private fun calculateAdaptiveThreshold(contentType: ContentType): Float {
        val baseThreshold = config.baseMotionThreshold
        
        // Apply content type adjustment
        val contentAdjustment = when (contentType) {
            ContentType.SOCIAL_MEDIA -> config.socialMediaAdjustment
            ContentType.VIDEO_CONTENT -> config.videoContentAdjustment
            ContentType.STATIC_CONTENT -> config.staticContentAdjustment
            ContentType.UNKNOWN -> 1.0f
        }
        
        // Apply adaptive learning adjustment
        val adaptiveAdjustment = 1.0f + adaptiveThresholdAdjustment
        
        return baseThreshold * contentAdjustment * adaptiveAdjustment
    }
    
    /**
     * Detects content type based on motion patterns and frame characteristics
     */
    private fun detectContentType(
        motionResult: MotionDetectionEngine.MotionResult,
        frameFingerprint: FrameFingerprint?
    ): ContentType {
        // Use motion history to detect patterns
        if (motionHistory.size < 5) {
            return ContentType.UNKNOWN
        }
        
        val recentMotion = motionHistory.takeLast(5)
        val averageMotion = recentMotion.map { it.motionValue }.average().toFloat()
        val motionVariance = calculateVariance(recentMotion.map { it.motionValue })
        
        // Social media: High variance, frequent motion changes
        if (motionVariance > 0.1f && averageMotion > 0.15f) {
            updateContentTypeConfidence(ContentType.SOCIAL_MEDIA)
            return ContentType.SOCIAL_MEDIA
        }
        
        // Video content: Consistent moderate motion
        if (motionVariance < 0.05f && averageMotion > 0.2f) {
            updateContentTypeConfidence(ContentType.VIDEO_CONTENT)
            return ContentType.VIDEO_CONTENT
        }
        
        // Static content: Low motion, low variance
        if (averageMotion < 0.1f && motionVariance < 0.02f) {
            updateContentTypeConfidence(ContentType.STATIC_CONTENT)
            return ContentType.STATIC_CONTENT
        }
        
        return detectedContentType
    }
    
    /**
     * Updates content type confidence and detection
     */
    private fun updateContentTypeConfidence(newType: ContentType) {
        if (newType == detectedContentType) {
            contentTypeConfidence = min(1.0f, contentTypeConfidence + 0.1f)
        } else {
            contentTypeConfidence = max(0.0f, contentTypeConfidence - 0.05f)
            if (contentTypeConfidence < 0.3f) {
                detectedContentType = newType
                contentTypeConfidence = 0.5f
            }
        }
    }
    
    /**
     * Calculates motion velocity metrics including trends and acceleration
     */
    private fun calculateVelocityMetrics(): VelocityMetrics {
        if (motionHistory.size < 2) {
            return VelocityMetrics(0f, 0f, VelocityTrend.STABLE, 0f)
        }
        
        val recentFrames = motionHistory.takeLast(min(5, motionHistory.size))
        
        // Calculate instant velocity (change from previous frame)
        val instantVelocity = if (recentFrames.size >= 2) {
            val current = recentFrames.last()
            val previous = recentFrames[recentFrames.size - 2]
            val timeDiff = (current.timestamp - previous.timestamp).toFloat()
            
            if (timeDiff > 0) {
                abs(current.motionValue - previous.motionValue) / (timeDiff / 1000f)
            } else 0f
        } else 0f
        
        // Calculate average velocity over recent frames
        val averageVelocity = if (recentFrames.size >= 3) {
            var totalVelocity = 0f
            var validPairs = 0
            
            for (i in 1 until recentFrames.size) {
                val current = recentFrames[i]
                val previous = recentFrames[i - 1]
                val timeDiff = (current.timestamp - previous.timestamp).toFloat()
                
                if (timeDiff > 0) {
                    val velocity = abs(current.motionValue - previous.motionValue) / (timeDiff / 1000f)
                    totalVelocity += velocity
                    validPairs++
                }
            }
            
            if (validPairs > 0) totalVelocity / validPairs else 0f
        } else instantVelocity
        
        // Determine velocity trend
        val velocityTrend = if (recentFrames.size >= 4) {
            val velocities = mutableListOf<Float>()
            for (i in 1 until recentFrames.size) {
                val current = recentFrames[i]
                val previous = recentFrames[i - 1]
                val timeDiff = (current.timestamp - previous.timestamp).toFloat()
                
                if (timeDiff > 0) {
                    velocities.add(abs(current.motionValue - previous.motionValue) / (timeDiff / 1000f))
                }
            }
            
            if (velocities.size >= 3) {
                val trend = velocities.last() - velocities.first()
                when {
                    trend > 0.1f -> VelocityTrend.ACCELERATING
                    trend < -0.1f -> VelocityTrend.DECELERATING
                    calculateVariance(velocities) > 0.05f -> VelocityTrend.OSCILLATING
                    else -> VelocityTrend.STABLE
                }
            } else VelocityTrend.STABLE
        } else VelocityTrend.STABLE
        
        // Calculate acceleration (change in velocity)
        val acceleration = if (recentFrames.size >= 3) {
            val velocities = mutableListOf<Float>()
            for (i in 1 until recentFrames.size) {
                val current = recentFrames[i]
                val previous = recentFrames[i - 1]
                val timeDiff = (current.timestamp - previous.timestamp).toFloat()
                
                if (timeDiff > 0) {
                    velocities.add(abs(current.motionValue - previous.motionValue) / (timeDiff / 1000f))
                }
            }
            
            if (velocities.size >= 2) {
                velocities.last() - velocities[velocities.size - 2]
            } else 0f
        } else 0f
        
        return VelocityMetrics(
            instantVelocity = instantVelocity,
            averageVelocity = averageVelocity,
            velocityTrend = velocityTrend,
            acceleration = acceleration
        )
    }
    
    /**
     * Determines motion significance level
     */
    private fun determineMotionSignificance(
        motionValue: Float,
        threshold: Float
    ): MotionSignificance {
        val ratio = motionValue / threshold
        
        return when {
            ratio < 0.4f -> MotionSignificance.NEGLIGIBLE
            ratio < 1.0f -> MotionSignificance.LOW
            ratio < 1.6f -> MotionSignificance.MODERATE
            ratio < 2.4f -> MotionSignificance.HIGH
            else -> MotionSignificance.EXTREME
        }
    }
    
    /**
     * Calculates confidence score for the motion evaluation
     */
    private fun calculateConfidenceScore(
        motionResult: MotionDetectionEngine.MotionResult,
        velocityMetrics: VelocityMetrics,
        contentType: ContentType
    ): Float {
        var confidence = 0.5f // Base confidence
        
        // Higher confidence for consistent velocity patterns
        when (velocityMetrics.velocityTrend) {
            VelocityTrend.STABLE -> confidence += 0.2f
            VelocityTrend.ACCELERATING, VelocityTrend.DECELERATING -> confidence += 0.1f
            VelocityTrend.OSCILLATING -> confidence -= 0.1f
        }
        
        // Higher confidence for known content types
        if (contentType != ContentType.UNKNOWN) {
            confidence += contentTypeConfidence * 0.3f
        }
        
        // Higher confidence for clear motion patterns
        if (motionResult.motionRegions.isNotEmpty()) {
            confidence += 0.1f
        }
        
        // Lower confidence for very fast processing (might be inaccurate)
        if (motionResult.processingTimeMs < 1) {
            confidence -= 0.1f
        }
        
        return max(0f, min(1f, confidence))
    }
    
    /**
     * Calculates recommended analysis frequency based on motion characteristics
     */
    private fun calculateAnalysisFrequency(
        significance: MotionSignificance,
        velocityMetrics: VelocityMetrics,
        contentType: ContentType
    ): Float {
        var baseFrequency = 1.0f // Normal frequency
        
        // Adjust based on motion significance
        baseFrequency *= when (significance) {
            MotionSignificance.NEGLIGIBLE -> config.lowMotionFrequencyIncrease
            MotionSignificance.LOW -> 1.0f
            MotionSignificance.MODERATE -> config.mediumMotionFrequencyReduction
            MotionSignificance.HIGH -> config.highMotionFrequencyReduction
            MotionSignificance.EXTREME -> config.highMotionFrequencyReduction * 0.5f
        }
        
        // Adjust based on velocity
        when {
            velocityMetrics.averageVelocity > config.highVelocityThreshold -> 
                baseFrequency *= 0.3f // Very high motion, reduce frequency significantly
            velocityMetrics.averageVelocity > config.mediumVelocityThreshold -> 
                baseFrequency *= 0.6f // High motion, reduce frequency
            velocityMetrics.averageVelocity < config.lowVelocityThreshold -> 
                baseFrequency *= 1.2f // Low motion, can increase frequency
        }
        
        // Adjust based on content type
        baseFrequency *= when (contentType) {
            ContentType.SOCIAL_MEDIA -> 1.1f // Slightly more frequent for social media
            ContentType.VIDEO_CONTENT -> 0.8f // Less frequent for video
            ContentType.STATIC_CONTENT -> 1.3f // More frequent for static content
            ContentType.UNKNOWN -> 1.0f
        }
        
        return max(0.1f, min(2.0f, baseFrequency))
    }
    
    /**
     * Determines if analysis should be forced regardless of other factors
     */
    private fun shouldForceAnalysis(
        significance: MotionSignificance,
        velocityMetrics: VelocityMetrics,
        confidence: Float
    ): Boolean {
        // Force analysis for extreme motion
        if (significance == MotionSignificance.EXTREME) {
            return true
        }
        
        // Force analysis for high acceleration (sudden changes)
        if (abs(velocityMetrics.acceleration) > 0.5f) {
            return true
        }
        
        // Force analysis for high confidence significant motion
        if (significance >= MotionSignificance.MODERATE && confidence > config.confidenceThreshold) {
            return true
        }
        
        return false
    }
    
    /**
     * Updates adaptive learning based on motion patterns
     */
    private fun updateAdaptiveLearning(
        motionValue: Float,
        contentType: ContentType,
        confidence: Float
    ) {
        if (confidence < config.confidenceThreshold) {
            return // Don't learn from low-confidence evaluations
        }
        
        // Simple adaptive adjustment based on recent patterns
        val recentMotion = motionHistory.takeLast(config.historyWindowSize)
        if (recentMotion.size >= config.historyWindowSize) {
            val averageMotion = recentMotion.map { it.motionValue }.average().toFloat()
            val currentThreshold = calculateAdaptiveThreshold(contentType)
            
            // If we're consistently over/under threshold, adjust
            val overThresholdRatio = recentMotion.count { it.motionValue > currentThreshold }.toFloat() / recentMotion.size
            
            when {
                overThresholdRatio > 0.8f -> {
                    // Too many frames over threshold, increase threshold
                    adaptiveThresholdAdjustment += config.adaptationRate
                }
                overThresholdRatio < 0.2f -> {
                    // Too few frames over threshold, decrease threshold
                    adaptiveThresholdAdjustment -= config.adaptationRate
                }
            }
            
            // Clamp adjustment to reasonable bounds
            adaptiveThresholdAdjustment = max(-0.5f, min(0.5f, adaptiveThresholdAdjustment))
        }
    }
    
    /**
     * Adds motion data point to history with size management
     */
    private fun addToHistory(dataPoint: MotionDataPoint) {
        motionHistory.add(dataPoint)
        
        // Maintain history size
        while (motionHistory.size > config.historyWindowSize * 2) {
            motionHistory.removeAt(0)
        }
        
        // Remove old entries (older than 30 seconds)
        val cutoffTime = System.currentTimeMillis() - 30_000L
        motionHistory.removeAll { it.timestamp < cutoffTime }
    }
    
    /**
     * Calculates variance for a list of values
     */
    private fun calculateVariance(values: List<Float>): Float {
        if (values.size < 2) return 0f
        
        val mean = values.average().toFloat()
        val squaredDiffs = values.map { (it - mean).pow(2) }
        return squaredDiffs.average().toFloat()
    }
    
    /**
     * Gets current evaluation statistics
     */
    fun getEvaluationStats(): EvaluationStats {
        val recentHistory = motionHistory.takeLast(20)
        
        return EvaluationStats(
            totalEvaluations = motionHistory.size,
            detectedContentType = detectedContentType,
            contentTypeConfidence = contentTypeConfidence,
            adaptiveAdjustment = adaptiveThresholdAdjustment,
            averageMotionValue = if (recentHistory.isNotEmpty()) {
                recentHistory.map { it.motionValue }.average().toFloat()
            } else 0f,
            averageProcessingTime = if (recentHistory.isNotEmpty()) {
                recentHistory.map { it.processingTimeMs }.average().toFloat()
            } else 0f,
            historySize = motionHistory.size
        )
    }
    
    /**
     * Clears motion history and resets adaptive learning
     */
    fun reset() {
        motionHistory.clear()
        adaptiveThresholdAdjustment = 0f
        contentTypeConfidence = 0f
        detectedContentType = ContentType.UNKNOWN
    }
    
    data class EvaluationStats(
        val totalEvaluations: Int,
        val detectedContentType: ContentType,
        val contentTypeConfidence: Float,
        val adaptiveAdjustment: Float,
        val averageMotionValue: Float,
        val averageProcessingTime: Float,
        val historySize: Int
    )
}