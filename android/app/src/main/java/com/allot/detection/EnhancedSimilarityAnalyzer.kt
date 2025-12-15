package com.allot.detection

import android.graphics.Bitmap

/**
 * Enhanced Similarity Analyzer - Integrates motion detection with similarity analysis
 * 
 * Combines frame similarity analysis with motion detection to make intelligent decisions
 * about when to perform expensive content analysis.
 */
class EnhancedSimilarityAnalyzer(
    private val similarityAnalyzer: SimilarityAnalyzer,
    private val motionAnalyzer: MotionAnalyzer
) {
    
    data class EnhancedAnalysisResult(
        val shouldProcess: Boolean,
        val reason: String,
        val confidence: Float,
        val processingTimeMs: Long,
        val motionAnalysis: MotionAnalyzer.MotionAnalysis,
        val similarityResult: AnalysisResult,
        val motionOverride: Boolean
    )
    
    // Previous frame for motion analysis
    private var previousBitmap: Bitmap? = null
    
    /**
     * Performs enhanced analysis combining motion detection and similarity analysis
     */
    fun analyzeFrame(
        currentBitmap: Bitmap,
        currentFingerprint: FrameFingerprint,
        contentType: MotionThresholdEvaluator.ContentType = MotionThresholdEvaluator.ContentType.UNKNOWN
    ): EnhancedAnalysisResult {
        val startTime = System.nanoTime()
        
        try {
            // Step 1: Perform similarity analysis
            val similarityResult = similarityAnalyzer.analyzeFrame(currentFingerprint)
            
            // Step 2: Perform motion analysis if we have a previous frame
            val motionAnalysis = if (previousBitmap != null) {
                motionAnalyzer.analyzeMotion(
                    currentBitmap = currentBitmap,
                    previousBitmap = previousBitmap,
                    currentFingerprint = currentFingerprint,
                    contentType = contentType
                )
            } else {
                // First frame - create default motion analysis
                createDefaultMotionAnalysis()
            }
            
            // Step 3: Determine if motion should override similarity decision
            val motionOverride = shouldMotionOverride(similarityResult, motionAnalysis)
            
            // Step 4: Make final decision
            val finalDecision = if (motionOverride) {
                true // Motion overrides similarity-based skipping
            } else {
                similarityResult.shouldProcess
            }
            
            val reason = if (motionOverride) {
                "Motion override: ${motionAnalysis.analysisRecommendation.name}"
            } else {
                similarityResult.reason
            }
            
            val confidence = if (motionOverride) {
                0.8f // High confidence for motion override
            } else {
                similarityResult.confidence
            }
            
            // Step 5: Store current bitmap for next analysis
            previousBitmap?.recycle()
            previousBitmap = currentBitmap.copy(currentBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            
            val processingTime = (System.nanoTime() - startTime) / 1_000_000
            
            return EnhancedAnalysisResult(
                shouldProcess = finalDecision,
                reason = reason,
                confidence = confidence,
                processingTimeMs = processingTime,
                motionAnalysis = motionAnalysis,
                similarityResult = similarityResult,
                motionOverride = motionOverride
            )
            
        } catch (e: Exception) {
            val processingTime = (System.nanoTime() - startTime) / 1_000_000
            
            return EnhancedAnalysisResult(
                shouldProcess = true,
                reason = "Error in enhanced analysis: ${e.message}",
                confidence = 0.3f,
                processingTimeMs = processingTime,
                motionAnalysis = createDefaultMotionAnalysis(),
                similarityResult = AnalysisResult(
                    shouldProcess = true,
                    reason = "Error fallback",
                    mostSimilarMatch = null,
                    confidence = 0.5f,
                    processingTimeMs = 0L
                ),
                motionOverride = false
            )
        }
    }
    
    /**
     * Determines if motion should override similarity-based decision
     */
    private fun shouldMotionOverride(
        similarityResult: AnalysisResult,
        motionAnalysis: MotionAnalyzer.MotionAnalysis
    ): Boolean {
        // Don't override if similarity already says to process
        if (similarityResult.shouldProcess) {
            return false
        }
        
        // Override if motion analysis recommends forcing analysis
        if (motionAnalysis.shouldForceAnalysis) {
            return true
        }
        
        // Override if threshold evaluation recommends forcing analysis
        if (motionAnalysis.thresholdEvaluation.shouldForceAnalysis) {
            return true
        }
        
        // Override if significant motion detected
        if (motionAnalysis.currentMotion.isSignificantMotion) {
            return true
        }
        
        return false
    }
    
    /**
     * Creates default motion analysis for first frame
     */
    private fun createDefaultMotionAnalysis(): MotionAnalyzer.MotionAnalysis {
        val defaultMotionResult = MotionDetectionEngine.MotionResult(
            pixelDifference = 0f,
            isSignificantMotion = false,
            motionRegions = emptyList(),
            processingTimeMs = 0L
        )
        
        val defaultThresholdEvaluation = MotionThresholdEvaluator.MotionEvaluation(
            rawMotionValue = 0f,
            adjustedThreshold = 0.25f,
            motionSignificance = MotionThresholdEvaluator.MotionSignificance.NEGLIGIBLE,
            contentType = MotionThresholdEvaluator.ContentType.UNKNOWN,
            motionVelocity = 0f,
            isSignificantMotion = false,
            confidenceScore = 0.5f,
            recommendedAnalysisFrequency = 1.0f,
            shouldForceAnalysis = true, // First frame should be analyzed
            adaptiveAdjustment = 0f
        )
        
        return MotionAnalyzer.MotionAnalysis(
            currentMotion = defaultMotionResult,
            motionVelocity = 0f,
            isConsistentMotion = false,
            shouldForceAnalysis = true,
            shouldReduceFrequency = false,
            analysisRecommendation = MotionAnalyzer.AnalysisRecommendation.PROCEED_NORMAL,
            thresholdEvaluation = defaultThresholdEvaluation,
            adaptiveFrequency = 1.0f,
            contentType = MotionThresholdEvaluator.ContentType.UNKNOWN
        )
    }
    
    /**
     * Clears all data and resets state
     */
    fun clear() {
        similarityAnalyzer.clear()
        motionAnalyzer.clearHistory()
        previousBitmap?.recycle()
        previousBitmap = null
    }
}