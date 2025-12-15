package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlin.math.min

/**
 * Utility functions for bitmap operations optimized for motion detection and frame analysis.
 */
object BitmapUtils {
    
    /**
     * Efficiently downscales a bitmap to the specified dimensions
     */
    fun downscaleBitmap(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        if (source.width == targetWidth && source.height == targetHeight) {
            return source
        }
        
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
    
    /**
     * Converts bitmap to grayscale for consistent processing
     */
    fun toGrayscale(source: Bitmap): Bitmap {
        val grayscaleBitmap = Bitmap.createBitmap(
            source.width, 
            source.height, 
            Bitmap.Config.ARGB_8888
        )
        
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        
        canvas.drawBitmap(source, 0f, 0f, paint)
        
        return grayscaleBitmap
    }
    
    /**
     * Extracts a region from a bitmap efficiently
     */
    fun extractRegion(source: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val safeX = x.coerceAtLeast(0)
        val safeY = y.coerceAtLeast(0)
        val safeWidth = min(width, source.width - safeX)
        val safeHeight = min(height, source.height - safeY)
        
        return Bitmap.createBitmap(source, safeX, safeY, safeWidth, safeHeight)
    }
    
    /**
     * Calculates the luminance value of a pixel using standard RGB weights
     */
    fun calculateLuminance(pixel: Int): Float {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        
        // Standard luminance calculation
        return (0.299f * r + 0.587f * g + 0.114f * b) / 255f
    }
    
    /**
     * Calculates RGB difference between two pixels
     */
    fun calculatePixelDifference(pixel1: Int, pixel2: Int): Float {
        val r1 = (pixel1 shr 16) and 0xFF
        val g1 = (pixel1 shr 8) and 0xFF
        val b1 = pixel1 and 0xFF
        
        val r2 = (pixel2 shr 16) and 0xFF
        val g2 = (pixel2 shr 8) and 0xFF
        val b2 = pixel2 and 0xFF
        
        val rDiff = kotlin.math.abs(r1 - r2)
        val gDiff = kotlin.math.abs(g1 - g2)
        val bDiff = kotlin.math.abs(b1 - b2)
        
        // Return normalized difference (0.0 to 1.0)
        return (rDiff + gDiff + bDiff) / (3f * 255f)
    }
    
    /**
     * Safely recycles a bitmap if it's not null and not recycled
     */
    fun safeRecycle(bitmap: Bitmap?) {
        bitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
    }
    
    /**
     * Creates a copy of a bitmap with the same configuration
     */
    fun copyBitmap(source: Bitmap): Bitmap {
        return source.copy(source.config ?: Bitmap.Config.ARGB_8888, false)
    }
    
    /**
     * Checks if two bitmaps have the same dimensions
     */
    fun haveSameDimensions(bitmap1: Bitmap, bitmap2: Bitmap): Boolean {
        return bitmap1.width == bitmap2.width && bitmap1.height == bitmap2.height
    }
    
    /**
     * Calculates the memory usage of a bitmap in bytes
     */
    fun calculateMemoryUsage(bitmap: Bitmap): Long {
        return (bitmap.rowBytes * bitmap.height).toLong()
    }
    
    /**
     * Validates that a bitmap is suitable for processing
     */
    fun isValidForProcessing(bitmap: Bitmap?): Boolean {
        return bitmap != null && 
               !bitmap.isRecycled && 
               bitmap.width > 0 && 
               bitmap.height > 0
    }
}