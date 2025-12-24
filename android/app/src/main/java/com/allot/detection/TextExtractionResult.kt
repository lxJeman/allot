package com.allot.detection

import android.graphics.Rect

/**
 * Result of text extraction operation
 */
data class TextExtractionResult(
    val extractedText: String,
    val confidence: Float,
    val textRegions: List<TextRegion>,
    val textDensity: Float,
    val processingTimeMs: Long,
    val usedCache: Boolean = false,
    val roiDetected: Boolean = false,
    val roiArea: Int = 0,
    val preprocessingTimeMs: Long = 0L,
    val validationPassed: Boolean? = null,
    val validationScore: Float? = null,
    val fallbackUsed: Boolean? = null,
    val fallbackStrategy: String? = null
) {
    fun isHighQuality(): Boolean {
        return confidence > 0.8f && textDensity > 0.1f && extractedText.length > 10
    }
}

/**
 * Individual text region detected in the image
 */
data class TextRegion(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val textType: TextType
)

/**
 * Type of text detected
 */
enum class TextType {
    GENERAL,
    HASHTAG,
    MENTION,
    URL,
    CAPTION,
    COMMENT
}