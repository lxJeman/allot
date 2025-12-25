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
 * LocalTextExtractor - Simplified on-device text extraction using Google ML Kit
 * 
 * Provides fast, private text recognition without external API calls.
 * Simplified version that focuses on core ML Kit functionality.
 */
class LocalTextExtractor {
    
    companion object {
        const val TAG = "LocalTextExtractor"
        const val MIN_CONFIDENCE_THRESHOLD = 0.5f
    }
    
    // ML Kit text recognizer with Latin script optimization
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).also {
        Log.d(TAG, "🤖 ML Kit TextRecognizer initialized with DEFAULT_OPTIONS")
    }
    
    /**
     * Extract text from bitmap using ML Kit directly
     */
    suspend fun extractText(bitmap: Bitmap): TextExtractionResult {
        return try {
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "🔍 Starting ML Kit text extraction for bitmap ${bitmap.width}x${bitmap.height}")
            
            // Validate bitmap
            if (bitmap.isRecycled) {
                Log.e(TAG, "❌ Bitmap is recycled!")
                throw IllegalStateException("Bitmap is recycled")
            }
            
            if (bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e(TAG, "❌ Invalid bitmap dimensions: ${bitmap.width}x${bitmap.height}")
                throw IllegalArgumentException("Invalid bitmap dimensions")
            }
            
            // Create ML Kit input image
            val inputImage = try {
                Log.d(TAG, "🖼️ Creating InputImage from bitmap...")
                val image = InputImage.fromBitmap(bitmap, 0)
                Log.d(TAG, "✅ InputImage created successfully: ${image.width}x${image.height}, format=${image.format}")
                image
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to create InputImage: ${e.message}")
                throw Exception("Failed to create InputImage: ${e.message}", e)
            }
            
            // Perform text recognition
            val visionText = suspendCancellableCoroutine { continuation ->
                Log.d(TAG, "🔍 Processing image with ML Kit...")
                Log.d(TAG, "📊 InputImage details: width=${inputImage.width}, height=${inputImage.height}, format=${inputImage.format}")
                
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { text ->
                        Log.d(TAG, "✅ ML Kit processing successful - found ${text.textBlocks.size} text blocks")
                        continuation.resume(text)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "❌ ML Kit processing failed: ${exception.message}")
                        Log.e(TAG, "❌ Exception type: ${exception.javaClass.simpleName}")
                        Log.e(TAG, "❌ Exception cause: ${exception.cause?.message}")
                        exception.printStackTrace()
                        continuation.resumeWithException(Exception("Failed to run text recognizer text-recognition: ${exception.message}", exception))
                    }
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Extract text from all blocks
            val textRegions = mutableListOf<TextRegion>()
            var totalText = ""
            var totalConfidence = 0f
            var regionCount = 0
            
            Log.d(TAG, "📊 Processing ${visionText.textBlocks.size} text blocks")
            
            for (block in visionText.textBlocks) {
                Log.d(TAG, "📝 Block text: '${block.text}'")
                for (line in block.lines) {
                    Log.d(TAG, "📄 Line text: '${line.text}'")
                    for (element in line.elements) {
                        val text = element.text
                        val boundingBox = element.boundingBox
                        
                        Log.d(TAG, "🔤 Element text: '$text', boundingBox: $boundingBox")
                        
                        if (text.isNotBlank()) {
                            // Simple confidence estimation (ML Kit doesn't provide confidence)
                            val confidence = estimateTextConfidence(text)
                            
                            if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
                                textRegions.add(
                                    TextRegion(
                                        text = text,
                                        boundingBox = boundingBox ?: Rect(0, 0, 0, 0),
                                        confidence = confidence,
                                        textType = TextType.GENERAL
                                    )
                                )
                                
                                totalText += "$text "
                                totalConfidence += confidence
                                regionCount++
                            }
                        }
                    }
                }
            }
            
            // Calculate text density (simple version)
            val textDensity = if (bitmap.width > 0 && bitmap.height > 0) {
                totalText.length.toFloat() / (bitmap.width * bitmap.height) * 10000f // Scale for readability
            } else 0f
            
            // Calculate average confidence
            val averageConfidence = if (regionCount > 0) totalConfidence / regionCount else 0f
            
            val finalText = totalText.trim()
            
            Log.d(TAG, "✅ ML Kit extraction complete:")
            Log.d(TAG, "   📝 Text: '$finalText'")
            Log.d(TAG, "   📊 Regions: $regionCount")
            Log.d(TAG, "   🎯 Confidence: ${(averageConfidence * 100).toInt()}%")
            Log.d(TAG, "   ⏱️ Time: ${processingTime}ms")
            
            TextExtractionResult(
                extractedText = finalText,
                confidence = averageConfidence,
                textRegions = textRegions,
                textDensity = textDensity,
                processingTimeMs = processingTime,
                usedCache = false,
                roiDetected = false,
                roiArea = 0,
                preprocessingTimeMs = 0L,
                validationPassed = true,
                validationScore = averageConfidence,
                fallbackUsed = false,
                fallbackStrategy = null
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error extracting text: ${e.message}", e)
            
            // Return empty result on error
            TextExtractionResult(
                extractedText = "",
                confidence = 0f,
                textRegions = emptyList(),
                textDensity = 0f,
                processingTimeMs = 0L,
                usedCache = false,
                roiDetected = false,
                roiArea = 0,
                preprocessingTimeMs = 0L,
                validationPassed = false,
                validationScore = 0f,
                fallbackUsed = false,
                fallbackStrategy = null
            )
        }
    }
    
    /**
     * Simple confidence estimation based on text characteristics
     */
    private fun estimateTextConfidence(text: String): Float {
        if (text.isBlank()) return 0f
        
        var confidence = 0.8f // Base confidence
        
        // Boost confidence for longer text
        if (text.length > 3) confidence += 0.1f
        if (text.length > 10) confidence += 0.1f
        
        // Reduce confidence for very short text
        if (text.length == 1) confidence -= 0.3f
        
        // Boost confidence for common words/patterns
        if (text.matches(Regex("\\w+"))) confidence += 0.1f // Word characters only
        
        return confidence.coerceIn(0f, 1f)
    }
}