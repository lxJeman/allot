package com.allot.detection

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * FrameBuffer - Thread-safe circular buffer for FrameFingerprint storage
 * 
 * Maintains a rolling buffer of recent frame fingerprints with automatic expiration
 * and memory management. Optimized for high-frequency frame additions and similarity
 * searches in the Smart Content Detection System.
 * 
 * Performance Requirements:
 * - Add operation: <1ms per frame
 * - Search operation: <5ms for full buffer
 * - Memory usage: <10MB for default buffer size
 * - Thread-safe: Concurrent reads, exclusive writes
 */
class FrameBuffer(
    private val maxSize: Int = 15,
    private val maxAgeMs: Long = 30_000L // 30 seconds
) {
    
    // Circular buffer storage
    private val buffer = Array<FrameFingerprint?>(maxSize) { null }
    private var head = 0 // Next position to write
    private var size = 0 // Current number of elements
    
    // Thread safety
    private val lock = ReentrantReadWriteLock()
    
    // Statistics
    private var totalAdded = 0L
    private var totalExpired = 0L
    private var totalSearches = 0L
    private var totalSearchTimeMs = 0L
    
    /**
     * Add a new frame fingerprint to the buffer
     * Automatically handles overflow and expiration
     * 
     * @param fingerprint FrameFingerprint to add
     */
    fun addFrame(fingerprint: FrameFingerprint) {
        lock.write {
            // Add to buffer at head position
            buffer[head] = fingerprint
            
            // Update head position (circular)
            head = (head + 1) % maxSize
            
            // Update size (capped at maxSize)
            if (size < maxSize) {
                size++
            }
            
            totalAdded++
            
            // Clean up expired frames periodically
            if (totalAdded % 5 == 0L) {
                cleanupExpiredFrames()
            }
        }
    }
    
    /**
     * Find similar frames in the buffer
     * 
     * @param targetFingerprint Fingerprint to match against
     * @param similarityThreshold Minimum similarity required (0.0-1.0)
     * @return List of FrameMatch objects sorted by similarity (descending)
     */
    fun findSimilarFrames(
        targetFingerprint: FrameFingerprint,
        similarityThreshold: Float = 0.9f
    ): List<FrameMatch> {
        val startTime = System.currentTimeMillis()
        
        val matches = lock.read {
            totalSearches++
            
            val results = mutableListOf<FrameMatch>()
            val currentTime = System.currentTimeMillis()
            
            // Search through all valid frames
            for (i in 0 until size) {
                val index = getBufferIndex(i)
                val frame = buffer[index] ?: continue
                
                // Skip expired frames
                if (frame.isExpired(maxAgeMs)) continue
                
                // Calculate similarity
                val similarity = FingerprintUtils.calculateSimilarity(targetFingerprint, frame)
                
                if (similarity >= similarityThreshold) {
                    val ageMs = currentTime - frame.timestamp
                    results.add(FrameMatch(frame, similarity, ageMs))
                }
            }
            
            // Sort by similarity (descending)
            results.sortedByDescending { it.similarity }
        }
        
        val searchTime = System.currentTimeMillis() - startTime
        totalSearchTimeMs += searchTime
        
        return matches
    }
    
    /**
     * Find the most similar frame in the buffer
     * 
     * @param targetFingerprint Fingerprint to match against
     * @param minSimilarity Minimum similarity threshold
     * @return FrameMatch or null if no match found
     */
    fun findMostSimilar(
        targetFingerprint: FrameFingerprint,
        minSimilarity: Float = 0.5f
    ): FrameMatch? {
        return lock.read {
            var bestMatch: FrameMatch? = null
            var bestSimilarity = minSimilarity
            val currentTime = System.currentTimeMillis()
            
            for (i in 0 until size) {
                val index = getBufferIndex(i)
                val frame = buffer[index] ?: continue
                
                // Skip expired frames
                if (frame.isExpired(maxAgeMs)) continue
                
                val similarity = FingerprintUtils.calculateSimilarity(targetFingerprint, frame)
                
                if (similarity > bestSimilarity) {
                    val ageMs = currentTime - frame.timestamp
                    bestMatch = FrameMatch(frame, similarity, ageMs)
                    bestSimilarity = similarity
                }
            }
            
            bestMatch
        }
    }
    
    /**
     * Check if buffer contains a similar frame
     * Fast check without returning detailed results
     * 
     * @param targetFingerprint Fingerprint to check
     * @param similarityThreshold Similarity threshold
     * @return true if similar frame exists
     */
    fun containsSimilarFrame(
        targetFingerprint: FrameFingerprint,
        similarityThreshold: Float = 0.9f
    ): Boolean {
        return lock.read {
            for (i in 0 until size) {
                val index = getBufferIndex(i)
                val frame = buffer[index] ?: continue
                
                // Skip expired frames
                if (frame.isExpired(maxAgeMs)) continue
                
                val similarity = FingerprintUtils.calculateSimilarity(targetFingerprint, frame)
                if (similarity >= similarityThreshold) {
                    return@read true
                }
            }
            false
        }
    }
    
    /**
     * Get all valid (non-expired) frames from buffer
     * 
     * @return List of valid FrameFingerprint objects
     */
    fun getAllValidFrames(): List<FrameFingerprint> {
        return lock.read {
            val validFrames = mutableListOf<FrameFingerprint>()
            
            for (i in 0 until size) {
                val index = getBufferIndex(i)
                val frame = buffer[index] ?: continue
                
                if (!frame.isExpired(maxAgeMs)) {
                    validFrames.add(frame)
                }
            }
            
            validFrames.sortedBy { it.timestamp }
        }
    }
    
    /**
     * Get buffer statistics
     * 
     * @return BufferStats with current metrics
     */
    fun getBufferStats(): BufferStats {
        return lock.read {
            val validFrames = getAllValidFrames()
            val memoryUsage = estimateMemoryUsage()
            val averageSimilarity = if (validFrames.size > 1) {
                FingerprintUtils.calculateAverageSimilarity(validFrames)
            } else {
                1.0f
            }
            
            BufferStats(
                totalFrames = validFrames.size,
                maxCapacity = maxSize,
                oldestFrameAge = validFrames.minOfOrNull { it.getAgeMs() } ?: 0L,
                newestFrameAge = validFrames.maxOfOrNull { it.getAgeMs() } ?: 0L,
                averageSimilarity = averageSimilarity,
                memoryUsage = memoryUsage,
                totalAdded = totalAdded,
                totalExpired = totalExpired,
                totalSearches = totalSearches,
                averageSearchTimeMs = if (totalSearches > 0) totalSearchTimeMs.toFloat() / totalSearches else 0f
            )
        }
    }
    
    /**
     * Clear all frames from buffer
     */
    fun clear() {
        lock.write {
            for (i in buffer.indices) {
                buffer[i] = null
            }
            head = 0
            size = 0
        }
    }
    
    /**
     * Force cleanup of expired frames
     * 
     * @return Number of frames removed
     */
    fun cleanupExpiredFrames(): Int {
        return lock.write {
            var removedCount = 0
            val currentTime = System.currentTimeMillis()
            
            // Create new buffer without expired frames
            val validFrames = mutableListOf<FrameFingerprint>()
            
            for (i in 0 until size) {
                val index = getBufferIndex(i)
                val frame = buffer[index] ?: continue
                
                if (currentTime - frame.timestamp <= maxAgeMs) {
                    validFrames.add(frame)
                } else {
                    removedCount++
                }
            }
            
            // Rebuild buffer with valid frames
            clear()
            validFrames.forEach { frame ->
                buffer[head] = frame
                head = (head + 1) % maxSize
                size++
            }
            
            totalExpired += removedCount
            removedCount
        }
    }
    
    /**
     * Get buffer index for logical position
     * Handles circular buffer wraparound
     */
    private fun getBufferIndex(logicalIndex: Int): Int {
        return if (size < maxSize) {
            // Buffer not full yet, simple indexing
            logicalIndex
        } else {
            // Buffer is full, calculate from head position
            (head - size + logicalIndex + maxSize) % maxSize
        }
    }
    
    /**
     * Estimate memory usage of buffer
     */
    private fun estimateMemoryUsage(): Long {
        // Rough estimate: each FrameFingerprint ~200 bytes
        val fingerprintSize = 200L
        val bufferOverhead = maxSize * 8L // Array overhead
        return (size * fingerprintSize) + bufferOverhead
    }
    
    /**
     * Get frames within time window
     * 
     * @param windowMs Time window in milliseconds
     * @return List of frames within the time window
     */
    fun getFramesInTimeWindow(windowMs: Long): List<FrameFingerprint> {
        return lock.read {
            val currentTime = System.currentTimeMillis()
            val cutoffTime = currentTime - windowMs
            
            getAllValidFrames().filter { frame ->
                frame.timestamp >= cutoffTime
            }
        }
    }
    
    /**
     * Check if buffer is full
     */
    fun isFull(): Boolean {
        return lock.read { size >= maxSize }
    }
    
    /**
     * Check if buffer is empty
     */
    fun isEmpty(): Boolean {
        return lock.read { size == 0 }
    }
    
    /**
     * Get current buffer utilization (0.0 to 1.0)
     */
    fun getUtilization(): Float {
        return lock.read { size.toFloat() / maxSize }
    }
    
    /**
     * Get configuration information
     */
    fun getConfig(): BufferConfig {
        return BufferConfig(
            maxSize = maxSize,
            maxAgeMs = maxAgeMs,
            currentSize = lock.read { size },
            utilization = getUtilization()
        )
    }
}

/**
 * Frame match result from similarity search
 */
data class FrameMatch(
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val ageMs: Long
) {
    override fun toString(): String {
        return "FrameMatch(similarity=${String.format("%.3f", similarity)}, age=${ageMs}ms, ${fingerprint.toCompactString()})"
    }
}

/**
 * Buffer statistics for monitoring and debugging
 */
data class BufferStats(
    val totalFrames: Int,
    val maxCapacity: Int,
    val oldestFrameAge: Long,
    val newestFrameAge: Long,
    val averageSimilarity: Float,
    val memoryUsage: Long,
    val totalAdded: Long,
    val totalExpired: Long,
    val totalSearches: Long,
    val averageSearchTimeMs: Float
) {
    fun getUtilization(): Float = totalFrames.toFloat() / maxCapacity
    
    override fun toString(): String {
        return "BufferStats(frames=$totalFrames/$maxCapacity, " +
                "util=${String.format("%.1f", getUtilization() * 100)}%, " +
                "mem=${memoryUsage / 1024}KB, " +
                "searches=$totalSearches, " +
                "avg_search=${String.format("%.2f", averageSearchTimeMs)}ms)"
    }
}

/**
 * Buffer configuration information
 */
data class BufferConfig(
    val maxSize: Int,
    val maxAgeMs: Long,
    val currentSize: Int,
    val utilization: Float
) {
    override fun toString(): String {
        return "BufferConfig(max=$maxSize, age=${maxAgeMs}ms, " +
                "current=$currentSize, util=${String.format("%.1f", utilization * 100)}%)"
    }
}