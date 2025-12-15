package com.allot.detection

/**
 * PreviousAnalysisResult - Result of previous analysis for context
 */
data class PreviousAnalysisResult(
    val timestamp: Long,
    val isHarmful: Boolean,
    val confidence: Float,
    val processingTimeMs: Long,
    val fingerprint: FrameFingerprint,
    val analysisType: String = "unknown"
)