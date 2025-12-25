package com.allot

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * SmartDetectionModule - Minimal React Native bridge for text extraction
 * 
 * Provides basic text extraction functionality (mock implementation for now)
 */
class SmartDetectionModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "SmartDetectionModule"
    }

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun getName(): String {
        return "SmartDetectionModule"
    }

    /**
     * Extract text from base64 image (mock implementation)
     */
    @ReactMethod
    fun extractText(base64Image: String, promise: Promise) {
        try {
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "🔍 Starting ML Kit text extraction...")
            
            // Decode base64 to bitmap
            val bitmap = base64ToBitmap(base64Image)
            
            // Create InputImage for ML Kit
            val image = InputImage.fromBitmap(bitmap, 0)
            
            // Process with ML Kit Text Recognition
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    try {
                        val processingTime = System.currentTimeMillis() - startTime
                        val extractedText = visionText.text
                        
                        Log.d(TAG, "✅ ML Kit extraction completed in ${processingTime}ms")
                        Log.d(TAG, "📝 Extracted text: \"$extractedText\"")
                        
                        // Calculate metrics
                        val textDensity = if (bitmap.width > 0 && bitmap.height > 0) {
                            extractedText.length.toFloat() / (bitmap.width * bitmap.height) * 10000
                        } else 0f
                        
                        val confidence = if (extractedText.isNotEmpty()) {
                            // Calculate confidence based on text blocks
                            val avgConfidence = visionText.textBlocks.mapNotNull { block ->
                                block.lines.mapNotNull { line ->
                                    line.elements.mapNotNull { element ->
                                        element.confidence
                                    }.average().takeIf { !it.isNaN() }
                                }.average().takeIf { !it.isNaN() }
                            }.average()
                            
                            if (avgConfidence.isNaN()) 0.8 else avgConfidence
                        } else 0.0
                        
                        bitmap.recycle()
                        
                        // Return result
                        val resultMap = Arguments.createMap().apply {
                            putString("extractedText", extractedText)
                            putDouble("confidence", confidence)
                            putDouble("textDensity", textDensity.toDouble())
                            putDouble("processingTimeMs", processingTime.toDouble())
                            putInt("textRegions", visionText.textBlocks.size)
                            putBoolean("usedCache", false)
                            putBoolean("roiDetected", visionText.textBlocks.isNotEmpty())
                            putBoolean("isHighQuality", confidence > 0.7)
                            putBoolean("meetsPerformanceTarget", processingTime < 1000)
                        }
                        
                        promise.resolve(resultMap)
                        
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing ML Kit result: ${e.message}", e)
                        bitmap.recycle()
                        promise.reject("PROCESSING_ERROR", e.message)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ML Kit text recognition failed: ${e.message}", e)
                    bitmap.recycle()
                    promise.reject("ML_KIT_ERROR", e.message)
                }
                
        } catch (e: Exception) {
            Log.e(TAG, "Error in extractText: ${e.message}", e)
            promise.reject("TEXT_EXTRACTION_ERROR", e.message)
        }
    }

    /**
     * Extract text with validation (mock implementation)
     */
    @ReactMethod
    fun testTextExtractionWithValidation(base64Image: String, promise: Promise) {
        try {
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "🔍 Starting ML Kit text extraction with validation...")
            
            // Decode base64 to bitmap
            val bitmap = base64ToBitmap(base64Image)
            
            // Create InputImage for ML Kit
            val image = InputImage.fromBitmap(bitmap, 0)
            
            // Process with ML Kit Text Recognition
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    try {
                        val processingTime = System.currentTimeMillis() - startTime
                        val extractedText = visionText.text
                        
                        Log.d(TAG, "✅ ML Kit validation extraction completed in ${processingTime}ms")
                        Log.d(TAG, "📝 Extracted text: \"$extractedText\"")
                        
                        // Calculate metrics
                        val textDensity = if (bitmap.width > 0 && bitmap.height > 0) {
                            extractedText.length.toFloat() / (bitmap.width * bitmap.height) * 10000
                        } else 0f
                        
                        val confidence = if (extractedText.isNotEmpty()) {
                            // Calculate confidence based on text blocks
                            val avgConfidence = visionText.textBlocks.mapNotNull { block ->
                                block.lines.mapNotNull { line ->
                                    line.elements.mapNotNull { element ->
                                        element.confidence
                                    }.average().takeIf { !it.isNaN() }
                                }.average().takeIf { !it.isNaN() }
                            }.average()
                            
                            if (avgConfidence.isNaN()) 0.8 else avgConfidence
                        } else 0.0
                        
                        // Validation logic
                        val validationPassed = extractedText.isNotEmpty() && confidence > 0.5
                        val validationScore = confidence
                        val isHighQuality = confidence > 0.7 && extractedText.length > 5
                        
                        bitmap.recycle()
                        
                        // Return result with validation info
                        val resultMap = Arguments.createMap().apply {
                            putString("extractedText", extractedText)
                            putDouble("confidence", confidence)
                            putDouble("textDensity", textDensity.toDouble())
                            putDouble("processingTimeMs", processingTime.toDouble())
                            putInt("textRegions", visionText.textBlocks.size)
                            putBoolean("validationPassed", validationPassed)
                            putDouble("validationScore", validationScore)
                            putBoolean("fallbackUsed", false)
                            putString("fallbackStrategy", "NONE")
                            putBoolean("usedCache", false)
                            putBoolean("roiDetected", visionText.textBlocks.isNotEmpty())
                            putBoolean("isHighQuality", isHighQuality)
                            putBoolean("meetsPerformanceTarget", processingTime < 1000)
                        }
                        
                        promise.resolve(resultMap)
                        
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing ML Kit validation result: ${e.message}", e)
                        bitmap.recycle()
                        promise.reject("PROCESSING_ERROR", e.message)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ML Kit validation text recognition failed: ${e.message}", e)
                    bitmap.recycle()
                    promise.reject("ML_KIT_ERROR", e.message)
                }
                
        } catch (e: Exception) {
            Log.e(TAG, "Error in testTextExtractionWithValidation: ${e.message}", e)
            promise.reject("TEXT_EXTRACTION_ERROR", e.message)
        }
    }

    /**
     * Get text extraction cache stats (mock implementation)
     */
    @ReactMethod
    fun getTextExtractionCacheStats(promise: Promise) {
        try {
            val result = Arguments.createMap().apply {
                putInt("hits", 0)
                putInt("misses", 0)
                putDouble("hitRate", 0.0)
                putInt("totalEntries", 0)
                putDouble("memoryUsageKB", 0.0)
            }
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cache stats: ${e.message}", e)
            promise.reject("CACHE_STATS_ERROR", e.message)
        }
    }

    /**
     * Get text extraction metrics (mock implementation)
     */
    @ReactMethod
    fun getTextExtractionMetrics(promise: Promise) {
        try {
            val result = Arguments.createMap().apply {
                putInt("totalExtractions", 0)
                putDouble("averageProcessingTimeMs", 0.0)
                putDouble("successRate", 1.0)
                putBoolean("meetsPerformanceRequirements", true)
            }
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting metrics: ${e.message}", e)
            promise.reject("METRICS_ERROR", e.message)
        }
    }

    /**
     * Clear all data (mock implementation)
     */
    @ReactMethod
    fun clearAllData(promise: Promise) {
        try {
            Log.d(TAG, "Clearing all data (mock)")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing data: ${e.message}", e)
            promise.reject("CLEAR_DATA_ERROR", e.message)
        }
    }

    /**
     * Get A/B testing stats (mock implementation)
     */
    @ReactMethod
    fun getABTestingStats(promise: Promise) {
        try {
            val result = Arguments.createMap().apply {
                putString("currentGroup", "A")
                putInt("totalTests", 0)
                putDouble("groupASuccessRate", 1.0)
                putDouble("groupBSuccessRate", 1.0)
            }
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting A/B testing stats: ${e.message}", e)
            promise.reject("AB_TESTING_STATS_ERROR", e.message)
        }
    }

    /**
     * Convert base64 string to bitmap
     */
    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val inputStream = ByteArrayInputStream(decodedBytes)
        return BitmapFactory.decodeStream(inputStream)
            ?: throw IllegalArgumentException("Failed to decode base64 image")
    }

    /**
     * Convert bitmap to base64 string
     */
    private fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}