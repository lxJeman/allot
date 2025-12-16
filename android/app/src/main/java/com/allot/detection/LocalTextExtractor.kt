package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * LocalTextExtractor - On-device text extraction using Google ML Kit
 * 
 * Provides fast, private text recognition without external API calls.
 * Optimized for social media content with intelligent filtering and classification.
 */
class LocalTextExtractor {
    
    companion object {
        const val TAG = "LocalTextExtractor"
        const val TARGET_PROCESSING_TIME_MS = 50L
        const val MIN_CONFIDENCE_THRESHOLD = 0.7f
        const val MIN_TEXT_DENSITY_THRESHOLD = 0.1f
    }
    
    // ML Kit text recognizer with Latin script optimization
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    // Performance monitoring
    private val performanceMonitor = PerformanceMonitor()
    
    /**
     * Extract text from bitmap with comprehensive analysis
     */
    suspend fun extractText(bitmap: Bitmap): TextExtractionResult {
        val timer = performanceMonitor.startOperation("extract_text")
        
        return try {
            val startTime = System.currentTimeMillis()
            
            // Preprocess bitmap for optimal text recognition
            val preprocessedBitmap = preprocessBitmap(bitmap)
            
            // Create ML Kit input image
            val inputImage = InputImage.fromBitmap(preprocessedBitmap, 0)
            
            // Perform text recognition
            val visionText = suspendCancellableCoroutine { continuation ->
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { text ->
                        continuation.resume(text)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Extract and classify text regions
            val textRegions = mutableListOf<TextRegion>()
            var totalText = ""
            var totalConfidence = 0f
            var regionCount = 0
            
            for (block in visionText.textBlocks) {
                for (line in block.lines) {
                    for (element in line.elements) {
                        val text = element.text
                        val boundingBox = element.boundingBox ?: continue
                        
                        // Calculate confidence (ML Kit doesn't provide confidence, so we estimate)
                        val confidence = estimateTextConfidence(text, boundingBox, bitmap)
                        
                        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                            // Classify text type
                            val textType = classifyTextType(text, boundingBox, bitmap)
                            
                            textRegions.add(
                                TextRegion(
                                    text = text,
                                    boundingBox = boundingBox,
                                    confidence = confidence,
                                    textType = textType
                                )
                            )
                            
                            totalText += "$text "
                            totalConfidence += confidence
                            regionCount++
                        }
                    }
                }
            }
            
            // Calculate text density
            val textDensity = calculateTextDensity(bitmap, textRegions)
            
            // Calculate average confidence
            val averageConfidence = if (regionCount > 0) totalConfidence / regionCount else 0f
            
            // Clean up preprocessed bitmap if different from original
            if (preprocessedBitmap != bitmap) {
                preprocessedBitmap.recycle()
            }
            
            timer.complete()
            
            TextExtractionResult(
                extractedText = totalText.trim(),
                confidence = averageConfidence,
                textRegions = textRegions,
                textDensity = textDensity,
                processingTimeMs = processingTime
            )
            
        } catch (e: Exception) {
            timer.fail(e.message)
            Log.e(TAG, "Error extracting text: ${e.message}", e)
            
            // Return empty result on error
            TextExtractionResult(
                extractedText = "",
                confidence = 0f,
                textRegions = emptyList(),
                textDensity = 0f,
                processingTimeMs = timer.getElapsedTime()
            )
        }
    }
    
    /**
     * Extract text with detailed region analysis
     */
    suspend fun extractTextWithRegions(bitmap: Bitmap): List<TextRegion> {
        val result = extractText(bitmap)
        return result.textRegions
    }
    
    /**
     * Calculate text density for intelligent filtering
     */
    fun calculateTextDensity(bitmap: Bitmap): Float {
        return try {
            // Quick estimation without full text extraction
            val sampleRegions = getSampleRegions(bitmap)
            var totalTextPixels = 0
            var totalPixels = 0
            
            for (region in sampleRegions) {
                val regionPixels = region.width() * region.height()
                val textPixels = estimateTextPixelsInRegion(bitmap, region)
                
                totalTextPixels += textPixels
                totalPixels += regionPixels
            }
            
            if (totalPixels > 0) totalTextPixels.toFloat() / totalPixels else 0f
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating text density: ${e.message}", e)
            0f
        }
    }
    
    /**
     * Quick text presence detection
     */
    fun isTextPresent(bitmap: Bitmap, threshold: Float = MIN_TEXT_DENSITY_THRESHOLD): Boolean {
        return calculateTextDensity(bitmap) >= threshold
    }
    
    /**
     * Preprocess bitmap for optimal text recognition
     */
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        return try {
            // For social media content, we typically don't need heavy preprocessing
            // ML Kit handles most optimization internally
            
            // Check if bitmap is too large and needs scaling
            val maxDimension = 1024
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
                val scaledWidth = (bitmap.width * scale).toInt()
                val scaledHeight = (bitmap.height * scale).toInt()
                
                Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
            } else {
                bitmap
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Error preprocessing bitmap, using original: ${e.message}")
            bitmap
        }
    }
    
    /**
     * Estimate text confidence based on various factors
     */
    private fun estimateTextConfidence(text: String, boundingBox: Rect, bitmap: Bitmap): Float {
        var confidence = 0.8f // Base confidence for ML Kit detection
        
        // Adjust based on text length (longer text usually more reliable)
        when {
            text.length >= 10 -> confidence += 0.1f
            text.length >= 5 -> confidence += 0.05f
            text.length < 3 -> confidence -= 0.2f
        }
        
        // Adjust based on text characteristics
        if (text.matches(Regex(".*[a-zA-Z].*"))) confidence += 0.05f // Contains letters
        if (text.matches(Regex(".*\\d.*"))) confidence += 0.02f // Contains numbers
        if (text.matches(Regex(".*[!@#$%^&*(),.?\":{}|<>].*"))) confidence -= 0.05f // Special chars
        
        // Adjust based on bounding box size (very small text might be noise)
        val boxArea = boundingBox.width() * boundingBox.height()
        val bitmapArea = bitmap.width * bitmap.height
        val relativeSize = boxArea.toFloat() / bitmapArea
        
        when {
            relativeSize < 0.0001f -> confidence -= 0.3f // Very small text
            relativeSize < 0.001f -> confidence -= 0.1f // Small text
            relativeSize > 0.1f -> confidence += 0.1f // Large text
        }
        
        return confidence.coerceIn(0f, 1f)
    }
    
    /**
     * Classify text type for intelligent filtering
     */
    private fun classifyTextType(text: String, boundingBox: Rect, bitmap: Bitmap): TextType {
        val cleanText = text.trim().lowercase()
        
        // UI element patterns
        val uiPatterns = listOf(
            "like", "share", "comment", "follow", "subscribe", "save",
            "more", "options", "menu", "settings", "back", "next",
            "play", "pause", "stop", "volume", "search", "filter",
            "sort", "edit", "delete", "cancel", "ok", "yes", "no"
        )
        
        // Metadata patterns
        val metadataPatterns = listOf(
            Regex("\\d+[hms]"), // Time indicators (1h, 30m, 45s)
            Regex("\\d+[kmb]"), // Count indicators (1k, 2m, 3b)
            Regex("@\\w+"), // Usernames
            Regex("#\\w+"), // Hashtags
            Regex("\\d{1,2}[:/]\\d{1,2}") // Time stamps
        )
        
        // Check for UI elements
        if (uiPatterns.any { cleanText.contains(it) }) {
            return TextType.UI_ELEMENT
        }
        
        // Check for metadata
        if (metadataPatterns.any { it.containsMatchIn(cleanText) }) {
            return TextType.METADATA
        }
        
        // Check position-based classification
        val bitmapHeight = bitmap.height
        val textCenterY = boundingBox.centerY()
        
        // Text at very top or bottom is likely UI
        if (textCenterY < bitmapHeight * 0.1f || textCenterY > bitmapHeight * 0.9f) {
            return TextType.UI_ELEMENT
        }
        
        // Default to content text for main area
        return TextType.CONTENT_TEXT
    }
    
    /**
     * Calculate text density from extracted regions
     */
    private fun calculateTextDensity(bitmap: Bitmap, textRegions: List<TextRegion>): Float {
        if (textRegions.isEmpty()) return 0f
        
        val bitmapArea = bitmap.width * bitmap.height
        val textArea = textRegions.sumOf { region ->
            region.boundingBox.width() * region.boundingBox.height()
        }
        
        return textArea.toFloat() / bitmapArea
    }
    
    /**
     * Get sample regions for quick density estimation
     */
    private fun getSampleRegions(bitmap: Bitmap): List<Rect> {
        val regions = mutableListOf<Rect>()
        val width = bitmap.width
        val height = bitmap.height
        
        // Sample 9 regions (3x3 grid)
        val regionWidth = width / 3
        val regionHeight = height / 3
        
        for (row in 0..2) {
            for (col in 0..2) {
                val left = col * regionWidth
                val top = row * regionHeight
                val right = minOf(left + regionWidth, width)
                val bottom = minOf(top + regionHeight, height)
                
                regions.add(Rect(left, top, right, bottom))
            }
        }
        
        return regions
    }
    
    /**
     * Estimate text pixels in a region (simplified edge detection)
     */
    private fun estimateTextPixelsInRegion(bitmap: Bitmap, region: Rect): Int {
        return try {
            var textPixels = 0
            val pixels = IntArray(region.width() * region.height())
            
            bitmap.getPixels(
                pixels, 0, region.width(),
                region.left, region.top,
                region.width(), region.height()
            )
            
            // Simple edge detection for text estimation
            for (i in pixels.indices) {
                val pixel = pixels[i]
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val gray = (r + g + b) / 3
                
                // Text typically has high contrast edges
                if (gray < 100 || gray > 200) {
                    textPixels++
                }
            }
            
            textPixels
            
        } catch (e: Exception) {
            Log.w(TAG, "Error estimating text pixels: ${e.message}")
            0
        }
    }
    
    /**
     * Get performance metrics for text extraction
     */
    fun getPerformanceMetrics(): TextExtractionMetrics {
        val summary = performanceMonitor.getPerformanceSummary()
        
        return TextExtractionMetrics(
            totalExtractions = summary.totalOperations,
            averageProcessingTimeMs = summary.averageProcessingTimeMs,
            successRate = if (summary.totalOperations > 0) {
                (summary.totalOperations - summary.alertCount).toFloat() / summary.totalOperations
            } else 0f,
            meetsPerformanceTarget = summary.averageProcessingTimeMs <= TARGET_PROCESSING_TIME_MS
        )
    }
    
    /**
     * Clear performance history
     */
    fun clearMetrics() {
        performanceMonitor.clearAllData()
    }
}

/**
 * Text extraction result with comprehensive analysis
 */
data class TextExtractionResult(
    val extractedText: String,
    val confidence: Float,
    val textRegions: List<TextRegion>,
    val textDensity: Float,
    val processingTimeMs: Long
) {
    fun hasText(): Boolean = extractedText.isNotBlank()
    fun meetsConfidenceThreshold(threshold: Float = 0.8f): Boolean = confidence >= threshold
    fun meetsPerformanceTarget(): Boolean = processingTimeMs <= LocalTextExtractor.TARGET_PROCESSING_TIME_MS
}

/**
 * Individual text region with classification
 */
data class TextRegion(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val textType: TextType
) {
    fun isContentText(): Boolean = textType == TextType.CONTENT_TEXT
    fun isUIElement(): Boolean = textType == TextType.UI_ELEMENT
    fun isMetadata(): Boolean = textType == TextType.METADATA
    fun getArea(): Int = boundingBox.width() * boundingBox.height()
}

/**
 * Text type classification for intelligent filtering
 */
enum class TextType {
    CONTENT_TEXT,    // Main content (posts, comments, etc.)
    UI_ELEMENT,      // Interface elements (buttons, labels)
    METADATA,        // Timestamps, usernames, etc.
    UNKNOWN
}

/**
 * Performance metrics for text extraction
 */
data class TextExtractionMetrics(
    val totalExtractions: Long,
    val averageProcessingTimeMs: Float,
    val successRate: Float,
    val meetsPerformanceTarget: Boolean
)