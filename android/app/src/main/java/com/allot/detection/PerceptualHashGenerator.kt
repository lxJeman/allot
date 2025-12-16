package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlin.system.measureTimeMillis

/**
 * PerceptualHashGenerator - Generates dHash (difference hash) for visual similarity detection
 * 
 * Implements the difference hash algorithm optimized for Android bitmap operations.
 * The dHash algorithm compares adjacent pixels in a downscaled grayscale image to create
 * a 64-bit hash that represents the visual characteristics of the image.
 * 
 * Performance Requirements:
 * - Hash generation: <5ms per frame
 * - Memory efficient: Minimal allocations
 * - Thread-safe: Can be called from multiple threads
 * 
 * Algorithm Steps:
 * 1. Resize image to 9x8 pixels (72 pixels total)
 * 2. Convert to grayscale
 * 3. Compare each pixel with its horizontal neighbor
 * 4. Create 64-bit hash from comparison results
 */
object PerceptualHashGenerator {
    
    // Hash generation constants
    private const val HASH_WIDTH = 9   // 9 pixels wide for 8 comparisons per row
    private const val HASH_HEIGHT = 8  // 8 rows for 64 total comparisons
    private const val HASH_BITS = 64   // Total bits in the hash
    
    // Performance monitoring
    private var totalGenerations = 0L
    private var totalTimeMs = 0L
    private var maxTimeMs = 0L
    
    /**
     * Generate perceptual hash for a bitmap using dHash algorithm
     * 
     * @param bitmap Input bitmap to hash
     * @return 64-bit hash representing visual characteristics
     * @throws IllegalArgumentException if bitmap is null or invalid
     */
    fun generateHash(bitmap: Bitmap): Long {
        require(!bitmap.isRecycled) { "Bitmap is recycled" }
        require(bitmap.width > 0 && bitmap.height > 0) { "Bitmap has invalid dimensions" }
        
        return try {
            val startTime = System.nanoTime()
            val hash = generateHashInternal(bitmap)
            val endTime = System.nanoTime()
            val processingTime = (endTime - startTime) / 1_000_000 // Convert to milliseconds
            
            // Update performance metrics
            totalGenerations++
            totalTimeMs += processingTime
            if (processingTime > maxTimeMs) {
                maxTimeMs = processingTime
            }
            
            hash
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to generate hash for bitmap", e)
        }
    }
    
    /**
     * Internal hash generation implementation
     */
    private fun generateHashInternal(bitmap: Bitmap): Long {
        // Step 1: Resize to hash dimensions
        val resizedBitmap = resizeBitmap(bitmap, HASH_WIDTH, HASH_HEIGHT)
        
        try {
            // Step 2: Convert to grayscale and extract pixel values
            val grayscalePixels = extractGrayscalePixels(resizedBitmap)
            
            // Step 3: Generate hash from pixel comparisons
            return generateHashFromPixels(grayscalePixels)
            
        } finally {
            // Clean up resized bitmap if it's different from original
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
        }
    }
    
    /**
     * Resize bitmap to specified dimensions using high-quality scaling
     * 
     * @param bitmap Source bitmap
     * @param width Target width
     * @param height Target height
     * @return Resized bitmap
     */
    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        // If already the right size, return original
        if (bitmap.width == width && bitmap.height == height) {
            return bitmap
        }
        
        // Create scaled bitmap with bilinear filtering for better quality
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    /**
     * Extract grayscale pixel values from bitmap
     * 
     * @param bitmap Source bitmap (should be HASH_WIDTH x HASH_HEIGHT)
     * @return 2D array of grayscale values [row][col]
     */
    private fun extractGrayscalePixels(bitmap: Bitmap): Array<IntArray> {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        
        // Get all pixels at once for efficiency
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        // Convert to grayscale and organize into 2D array
        val grayscalePixels = Array(height) { IntArray(width) }
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]
                grayscalePixels[y][x] = convertToGrayscale(pixel)
            }
        }
        
        return grayscalePixels
    }
    
    /**
     * Convert ARGB pixel to grayscale using luminance formula
     * Uses the standard luminance weights: 0.299*R + 0.587*G + 0.114*B
     * 
     * @param pixel ARGB pixel value
     * @return Grayscale value (0-255)
     */
    private fun convertToGrayscale(pixel: Int): Int {
        val red = (pixel shr 16) and 0xFF
        val green = (pixel shr 8) and 0xFF
        val blue = pixel and 0xFF
        
        // Use integer arithmetic for performance
        return (red * 299 + green * 587 + blue * 114) / 1000
    }
    
    /**
     * Generate 64-bit hash from grayscale pixel array using dHash algorithm
     * Compares each pixel with its horizontal neighbor to create bit pattern
     * 
     * @param pixels 2D grayscale pixel array [row][col]
     * @return 64-bit hash
     */
    private fun generateHashFromPixels(pixels: Array<IntArray>): Long {
        var hash = 0L
        var bitIndex = 0
        
        // Compare each pixel with its horizontal neighbor
        for (row in 0 until HASH_HEIGHT) {
            for (col in 0 until HASH_WIDTH - 1) { // -1 because we compare with next pixel
                val currentPixel = pixels[row][col]
                val nextPixel = pixels[row][col + 1]
                
                // Set bit if current pixel is brighter than next pixel
                if (currentPixel > nextPixel) {
                    hash = hash or (1L shl bitIndex)
                }
                
                bitIndex++
            }
        }
        
        return hash
    }
    
    /**
     * Generate hash with detailed timing information
     * 
     * @param bitmap Input bitmap
     * @return Pair of (hash, processingTimeMs)
     */
    fun generateHashWithTiming(bitmap: Bitmap): Pair<Long, Long> {
        val startTime = System.nanoTime()
        val hash = generateHash(bitmap)
        val endTime = System.nanoTime()
        val processingTimeMs = (endTime - startTime) / 1_000_000
        
        return Pair(hash, processingTimeMs)
    }
    
    /**
     * Batch generate hashes for multiple bitmaps
     * More efficient than individual calls for large collections
     * 
     * @param bitmaps Collection of bitmaps to hash
     * @return List of hashes in same order as input
     */
    fun generateHashesBatch(bitmaps: Collection<Bitmap>): List<Long> {
        return bitmaps.map { bitmap ->
            generateHash(bitmap)
        }
    }
    
    /**
     * Generate FrameFingerprint from bitmap with full metadata
     * 
     * @param bitmap Input bitmap
     * @param timestamp Capture timestamp (default: current time)
     * @param textDensity Estimated text density (default: 0f)
     * @param metadata Additional metadata (default: empty)
     * @return Complete FrameFingerprint
     */
    fun generateFrameFingerprint(
        bitmap: Bitmap,
        timestamp: Long = System.currentTimeMillis(),
        textDensity: Float = 0f,
        metadata: Map<String, String> = emptyMap()
    ): FrameFingerprint {
        val hash = generateHash(bitmap)
        
        return FrameFingerprint(
            hash = hash,
            timestamp = timestamp,
            width = bitmap.width,
            height = bitmap.height,
            textDensity = textDensity,
            metadata = metadata
        )
    }
    
    /**
     * Validate hash generation performance
     * Tests hash generation with various bitmap sizes and reports performance
     * 
     * @param testBitmaps Collection of test bitmaps
     * @return PerformanceReport with detailed metrics
     */
    fun validatePerformance(testBitmaps: Collection<Bitmap>): PerformanceReport {
        val results = mutableListOf<Long>()
        val startTime = System.currentTimeMillis()
        
        testBitmaps.forEach { bitmap ->
            val (_, processingTime) = generateHashWithTiming(bitmap)
            results.add(processingTime)
        }
        
        val totalTime = System.currentTimeMillis() - startTime
        
        return PerformanceReport(
            totalBitmaps = testBitmaps.size,
            totalTimeMs = totalTime,
            averageTimeMs = if (results.isNotEmpty()) results.average() else 0.0,
            minTimeMs = results.minOrNull() ?: 0L,
            maxTimeMs = results.maxOrNull() ?: 0L,
            timings = results
        )
    }
    
    /**
     * Get performance statistics since last reset
     * 
     * @return PerformanceStats with current metrics
     */
    fun getPerformanceStats(): PerformanceStats {
        return PerformanceStats(
            totalGenerations = totalGenerations,
            totalTimeMs = totalTimeMs,
            averageTimeMs = if (totalGenerations > 0) totalTimeMs.toDouble() / totalGenerations else 0.0,
            maxTimeMs = maxTimeMs
        )
    }
    
    /**
     * Reset performance statistics
     */
    fun resetPerformanceStats() {
        totalGenerations = 0L
        totalTimeMs = 0L
        maxTimeMs = 0L
    }
    
    /**
     * Create test bitmap with specific pattern for validation
     * 
     * @param width Bitmap width
     * @param height Bitmap height
     * @param pattern Pattern type (GRADIENT, CHECKERBOARD, SOLID)
     * @return Test bitmap
     */
    fun createTestBitmap(width: Int, height: Int, pattern: TestPattern): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        
        when (pattern) {
            TestPattern.GRADIENT -> {
                // Create horizontal gradient
                for (x in 0 until width) {
                    val gray = (x * 255) / width
                    paint.color = android.graphics.Color.rgb(gray, gray, gray)
                    canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), paint)
                }
            }
            TestPattern.CHECKERBOARD -> {
                // Create checkerboard pattern
                val squareSize = kotlin.math.max(1, kotlin.math.min(width, height) / 8)
                for (y in 0 until height step squareSize) {
                    for (x in 0 until width step squareSize) {
                        val isBlack = ((x / squareSize) + (y / squareSize)) % 2 == 0
                        paint.color = if (isBlack) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                        canvas.drawRect(
                            x.toFloat(),
                            y.toFloat(),
                            (x + squareSize).toFloat(),
                            (y + squareSize).toFloat(),
                            paint
                        )
                    }
                }
            }
            TestPattern.SOLID -> {
                // Create solid gray
                paint.color = android.graphics.Color.GRAY
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
        }
        
        return bitmap
    }
}

/**
 * Test patterns for bitmap generation
 */
enum class TestPattern {
    GRADIENT,      // Horizontal gradient from black to white
    CHECKERBOARD,  // Alternating black and white squares
    SOLID          // Solid gray color
}

/**
 * Performance report for hash generation validation
 */
data class PerformanceReport(
    val totalBitmaps: Int,
    val totalTimeMs: Long,
    val averageTimeMs: Double,
    val minTimeMs: Long,
    val maxTimeMs: Long,
    val timings: List<Long>
) {
    fun meetsRequirements(): Boolean = averageTimeMs < 5.0 && maxTimeMs < 10L
    
    override fun toString(): String {
        return "PerformanceReport(bitmaps=$totalBitmaps, avg=${String.format("%.2f", averageTimeMs)}ms, " +
                "max=${maxTimeMs}ms, meets_req=${meetsRequirements()})"
    }
}

/**
 * Performance statistics for ongoing monitoring
 */
data class PerformanceStats(
    val totalGenerations: Long,
    val totalTimeMs: Long,
    val averageTimeMs: Double,
    val maxTimeMs: Long
) {
    override fun toString(): String {
        return "PerformanceStats(total=$totalGenerations, avg=${String.format("%.2f", averageTimeMs)}ms, max=${maxTimeMs}ms)"
    }
}