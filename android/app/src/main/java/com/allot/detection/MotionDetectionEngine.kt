package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Rect
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Motion Detection Engine for detecting significant visual changes between consecutive frames.
 * Optimized for performance with configurable thresholds and region-based analysis.
 */
class MotionDetectionEngine(
    private val config: MotionConfig = MotionConfig()
) {
    
    data class MotionConfig(
        val motionThreshold: Float = 0.25f,
        val downscaleSize: Int = 64,
        val regionSize: Int = 8,
        val rgbWeights: Triple<Float, Float, Float> = Triple(0.299f, 0.587f, 0.114f)
    )
    
    data class MotionResult(
        val pixelDifference: Float,
        val isSignificantMotion: Boolean,
        val motionRegions: List<Rect>,
        val processingTimeMs: Long,
        val regionDifferences: Map<Rect, Float> = emptyMap()
    )
    
    /**
     * Detects motion between current and previous frames
     */
    fun detectMotion(current: Bitmap, previous: Bitmap): MotionResult {
        val startTime = System.currentTimeMillis()
        
        try {
            // Downscale both bitmaps for performance
            val currentScaled = downscaleBitmap(current)
            val previousScaled = downscaleBitmap(previous)
            
            // Calculate overall pixel difference
            val pixelDifference = calculatePixelDifference(currentScaled, previousScaled)
            
            // Determine if motion is significant
            val isSignificantMotion = isSignificantMotion(pixelDifference)
            
            // Calculate motion regions if significant motion detected
            val motionRegions = if (isSignificantMotion) {
                detectMotionRegions(currentScaled, previousScaled)
            } else {
                emptyList()
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            return MotionResult(
                pixelDifference = pixelDifference,
                isSignificantMotion = isSignificantMotion,
                motionRegions = motionRegions,
                processingTimeMs = processingTime
            )
            
        } catch (e: Exception) {
            val processingTime = System.currentTimeMillis() - startTime
            return MotionResult(
                pixelDifference = 0f,
                isSignificantMotion = false,
                motionRegions = emptyList(),
                processingTimeMs = processingTime
            )
        }
    }
    
    /**
     * Calculates pixel difference percentage between two bitmaps
     */
    fun calculatePixelDifference(current: Bitmap, previous: Bitmap): Float {
        if (current.width != previous.width || current.height != previous.height) {
            return 1.0f // 100% difference if dimensions don't match
        }
        
        val width = current.width
        val height = current.height
        val totalPixels = width * height
        
        var totalDifference = 0.0
        
        // Get pixel arrays for efficient processing
        val currentPixels = IntArray(totalPixels)
        val previousPixels = IntArray(totalPixels)
        
        current.getPixels(currentPixels, 0, width, 0, 0, width, height)
        previous.getPixels(previousPixels, 0, width, 0, 0, width, height)
        
        // Calculate RGB differences using luminance weighting
        for (i in 0 until totalPixels) {
            val currentPixel = currentPixels[i]
            val previousPixel = previousPixels[i]
            
            val currentR = (currentPixel shr 16) and 0xFF
            val currentG = (currentPixel shr 8) and 0xFF
            val currentB = currentPixel and 0xFF
            
            val previousR = (previousPixel shr 16) and 0xFF
            val previousG = (previousPixel shr 8) and 0xFF
            val previousB = previousPixel and 0xFF
            
            // Calculate weighted RGB difference
            val rDiff = abs(currentR - previousR) * config.rgbWeights.first
            val gDiff = abs(currentG - previousG) * config.rgbWeights.second
            val bDiff = abs(currentB - previousB) * config.rgbWeights.third
            
            totalDifference += (rDiff + gDiff + bDiff) / 255.0
        }
        
        return (totalDifference / totalPixels).toFloat()
    }
    
    /**
     * Determines if the pixel difference represents significant motion
     */
    fun isSignificantMotion(difference: Float): Boolean {
        return difference >= config.motionThreshold
    }
    
    /**
     * Downscales bitmap to configured size for performance optimization
     */
    private fun downscaleBitmap(bitmap: Bitmap): Bitmap {
        return if (bitmap.width != config.downscaleSize || bitmap.height != config.downscaleSize) {
            Bitmap.createScaledBitmap(bitmap, config.downscaleSize, config.downscaleSize, true)
        } else {
            bitmap
        }
    }
    
    /**
     * Detects regions with significant motion for zone-based analysis
     */
    private fun detectMotionRegions(current: Bitmap, previous: Bitmap): List<Rect> {
        val motionRegions = mutableListOf<Rect>()
        val regionSize = config.regionSize
        val width = current.width
        val height = current.height
        
        // Divide image into regions and check each for motion
        for (y in 0 until height step regionSize) {
            for (x in 0 until width step regionSize) {
                val regionWidth = minOf(regionSize, width - x)
                val regionHeight = minOf(regionSize, height - y)
                
                val regionRect = Rect(x, y, x + regionWidth, y + regionHeight)
                
                // Extract region bitmaps
                val currentRegion = Bitmap.createBitmap(current, x, y, regionWidth, regionHeight)
                val previousRegion = Bitmap.createBitmap(previous, x, y, regionWidth, regionHeight)
                
                // Calculate region difference
                val regionDifference = calculatePixelDifference(currentRegion, previousRegion)
                
                // Add to motion regions if significant
                if (regionDifference >= config.motionThreshold) {
                    motionRegions.add(regionRect)
                }
                
                // Clean up region bitmaps
                currentRegion.recycle()
                previousRegion.recycle()
            }
        }
        
        return motionRegions
    }
    
    /**
     * Updates motion detection configuration
     */
    fun updateConfig(newConfig: MotionConfig) {
        // Note: In a real implementation, you might want to make config mutable
        // For now, this would require creating a new instance
    }
    
    /**
     * Gets current motion detection statistics
     */
    fun getStats(): MotionStats {
        return MotionStats(
            configuredThreshold = config.motionThreshold,
            downscaleSize = config.downscaleSize,
            regionSize = config.regionSize
        )
    }
    
    data class MotionStats(
        val configuredThreshold: Float,
        val downscaleSize: Int,
        val regionSize: Int
    )
}