package com.allot.detection

import android.graphics.Bitmap
import android.graphics.Color

/**
 * HashGenerationTest - Simple test utilities for validating hash generation
 * 
 * Provides basic test functions to verify that the perceptual hash generation
 * is working correctly and meets performance requirements.
 */
object HashGenerationTest {
    
    /**
     * Create a simple test bitmap with a gradient pattern
     */
    fun createTestBitmap(width: Int = 100, height: Int = 100): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Fill with a simple gradient pattern
        for (y in 0 until height) {
            for (x in 0 until width) {
                val gray = (x * 255) / width
                val color = Color.rgb(gray, gray, gray)
                bitmap.setPixel(x, y, color)
            }
        }
        
        return bitmap
    }
    
    /**
     * Test basic hash generation functionality
     */
    fun testBasicHashGeneration(): TestResult {
        val issues = mutableListOf<String>()
        
        try {
            // Create test bitmap
            val testBitmap = createTestBitmap(100, 100)
            
            // Generate hash
            val startTime = System.nanoTime()
            val hash = PerceptualHashGenerator.generateHash(testBitmap)
            val endTime = System.nanoTime()
            
            val processingTimeMs = (endTime - startTime) / 1_000_000
            
            // Validate results
            if (hash == 0L) {
                issues.add("Generated hash is zero")
            }
            
            if (processingTimeMs > 10) { // Allow 10ms for test environment
                issues.add("Processing time too slow: ${processingTimeMs}ms")
            }
            
            // Test consistency - same bitmap should produce same hash
            val hash2 = PerceptualHashGenerator.generateHash(testBitmap)
            if (hash != hash2) {
                issues.add("Hash generation not consistent")
            }
            
            testBitmap.recycle()
            
            return TestResult(
                testName = "Basic Hash Generation",
                passed = issues.isEmpty(),
                processingTimeMs = processingTimeMs,
                hash = hash,
                issues = issues
            )
            
        } catch (e: Exception) {
            issues.add("Exception during test: ${e.message}")
            return TestResult(
                testName = "Basic Hash Generation",
                passed = false,
                processingTimeMs = -1,
                hash = 0L,
                issues = issues
            )
        }
    }
    
    /**
     * Test similarity detection between similar bitmaps
     */
    fun testSimilarityDetection(): TestResult {
        val issues = mutableListOf<String>()
        
        try {
            // Create two similar bitmaps
            val bitmap1 = createTestBitmap(100, 100)
            val bitmap2 = createTestBitmap(100, 100) // Same pattern
            
            // Generate hashes
            val hash1 = PerceptualHashGenerator.generateHash(bitmap1)
            val hash2 = PerceptualHashGenerator.generateHash(bitmap2)
            
            // Calculate similarity
            val hammingDistance = FingerprintUtils.calculateHammingDistance(hash1, hash2)
            val similarity = FingerprintUtils.calculateSimilarity(
                FrameFingerprint(hash1, System.currentTimeMillis(), 100, 100),
                FrameFingerprint(hash2, System.currentTimeMillis(), 100, 100)
            )
            
            // Identical patterns should be very similar
            if (similarity < 0.9f) {
                issues.add("Identical patterns not detected as similar: $similarity")
            }
            
            bitmap1.recycle()
            bitmap2.recycle()
            
            return TestResult(
                testName = "Similarity Detection",
                passed = issues.isEmpty(),
                processingTimeMs = 0,
                hash = hash1,
                similarity = similarity,
                hammingDistance = hammingDistance,
                issues = issues
            )
            
        } catch (e: Exception) {
            issues.add("Exception during test: ${e.message}")
            return TestResult(
                testName = "Similarity Detection",
                passed = false,
                processingTimeMs = -1,
                hash = 0L,
                issues = issues
            )
        }
    }
    
    /**
     * Run all tests and return summary
     */
    fun runAllTests(): List<TestResult> {
        return listOf(
            testBasicHashGeneration(),
            testSimilarityDetection()
        )
    }
}

/**
 * Test result data class
 */
data class TestResult(
    val testName: String,
    val passed: Boolean,
    val processingTimeMs: Long,
    val hash: Long,
    val similarity: Float = 0f,
    val hammingDistance: Int = 0,
    val issues: List<String>
) {
    override fun toString(): String {
        val status = if (passed) "PASS" else "FAIL"
        return "[$status] $testName - ${processingTimeMs}ms - Hash: ${hash.toString(16)}" +
                if (issues.isNotEmpty()) " - Issues: ${issues.joinToString(", ")}" else ""
    }
}