package com.allot.detection

import java.util.concurrent.atomic.AtomicLong

/**
 * SimilarityAnalyzer - High-level interface for frame similarity analysis
 * 
 * Provides efficient frame comparison and similarity detection using the FrameBuffer.
 * Implements the SimilarityAnalyzer interface from the design specification with
 * optimized algorithms for finding similar frames and managing frame lifecycle.
 * 
 * Performance Requirements:
 * - Frame addition: <1ms per operation
 * - Similarity search: <5ms for full buffer scan
 * - Memory efficient: Automatic cleanup and optimization
 */
class SimilarityAnalyzer(
    bufferSize: Int = 15,
    frameExpiryMs: Long = 30_000L,
    private val defaultSimilarityThreshold: Float = 0.9f
) {
    
    // Core frame buffer
    private val frameBuffer = FrameBuffer(bufferSize, frameExpiryMs)
    
    // Performance tracking
    private val operationCounter = AtomicLong(0)
    private val totalProcessingTime = AtomicLong(0)
    private val maxProcessingTime = AtomicLong(0)
    
    /**
     * Add a new frame fingerprint to the analyzer
     * 
     * @param fingerprint FrameFingerprint to add
     */
    fun addFrame(fingerprint: FrameFingerprint) {
        val startTime = System.nanoTime()
        
        try {
            frameBuffer.addFrame(fingerprint)
        } finally {
            updatePerformanceMetrics(startTime)
        }
    }
    
    /**
     * Find frames similar to the target fingerprint
     * 
     * @param fingerprint Target fingerprint to match
     * @param threshold Similarity threshold (default: configured threshold)
     * @return List of FrameMatch objects sorted by similarity
     */
    fun findSimilarFrames(
        fingerprint: FrameFingerprint,
        threshold: Float = defaultSimilarityThreshold
    ): List<FrameMatch> {
        val startTime = System.nanoTime()
        
        return try {
            frameBuffer.findSimilarFrames(fingerprint, threshold)
        } finally {
            updatePerformanceMetrics(startTime)
        }
    }
    
    /**
     * Find the most similar frame to the target
     * 
     * @param fingerprint Target fingerprint
     * @param minSimilarity Minimum similarity threshold
     * @return Most similar FrameMatch or null
     */
    fun findMostSimilar(
        fingerprint: FrameFingerprint,
        minSimilarity: Float = 0.5f
    ): FrameMatch? {
        val startTime = System.nanoTime()
        
        return try {
            frameBuffer.findMostSimilar(fingerprint, minSimilarity)
        } finally {
            updatePerformanceMetrics(startTime)
        }
    }
    
    /**
     * Quick check if buffer contains similar frame
     * Optimized for fast decision making
     * 
     * @param fingerprint Target fingerprint
     * @param threshold Similarity threshold
     * @return true if similar frame exists
     */
    fun hasSimilarFrame(
        fingerprint: FrameFingerprint,
        threshold: Float = defaultSimilarityThreshold
    ): Boolean {
        val startTime = System.nanoTime()
        
        return try {
            frameBuffer.containsSimilarFrame(fingerprint, threshold)
        } finally {
            updatePerformanceMetrics(startTime)
        }
    }
    
    /**
     * Analyze frame and determine if it should be processed
     * Combines similarity check with additional heuristics
     * 
     * @param fingerprint Frame to analyze
     * @param config Analysis configuration
     * @return AnalysisResult with recommendation
     */
    fun analyzeFrame(
        fingerprint: FrameFingerprint,
        config: AnalysisConfig = AnalysisConfig()
    ): AnalysisResult {
        val startTime = System.nanoTime()
        
        try {
            // Find most similar frame
            val mostSimilar = findMostSimilar(fingerprint, config.minSimilarityThreshold)
            
            val shouldProcess = when {
                mostSimilar == null -> {
                    // No similar frame found, should process
                    true
                }
                mostSimilar.similarity >= config.skipThreshold -> {
                    // Very similar frame found, skip processing
                    false
                }
                mostSimilar.ageMs > config.maxSimilarAgeMs -> {
                    // Similar frame is too old, process anyway
                    true
                }
                else -> {
                    // Similar frame exists but not too similar or too old
                    mostSimilar.similarity < config.processThreshold
                }
            }
            
            val reason = when {
                mostSimilar == null -> "No similar frame found"
                !shouldProcess -> "Very similar frame exists (${String.format("%.2f", mostSimilar.similarity)})"
                mostSimilar.ageMs > config.maxSimilarAgeMs -> "Similar frame too old (${mostSimilar.ageMs}ms)"
                else -> "Similar frame below process threshold (${String.format("%.2f", mostSimilar.similarity)})"
            }
            
            return AnalysisResult(
                shouldProcess = shouldProcess,
                reason = reason,
                mostSimilarMatch = mostSimilar,
                confidence = calculateConfidence(mostSimilar, config),
                processingTimeMs = (System.nanoTime() - startTime) / 1_000_000
            )
            
        } finally {
            updatePerformanceMetrics(startTime)
        }
    }
    
    /**
     * Clear old frames from buffer
     * 
     * @param maxAge Maximum age to keep (default: use buffer's configured age)
     * @return Number of frames removed
     */
    fun clearOldFrames(maxAge: Long? = null): Int {
        return if (maxAge != null) {
            // Custom cleanup - remove frames older than specified age
            val validFrames = frameBuffer.getAllValidFrames().filter { frame ->
                frame.getAgeMs() <= maxAge
            }
            
            frameBuffer.clear()
            validFrames.forEach { frameBuffer.addFrame(it) }
            
            frameBuffer.getBufferStats().totalFrames - validFrames.size
        } else {
            // Standard cleanup using buffer's configured expiry
            frameBuffer.cleanupExpiredFrames()
        }
    }
    
    /**
     * Get comprehensive buffer statistics
     * 
     * @return BufferStats with detailed metrics
     */
    fun getBufferStats(): BufferStats {
        return frameBuffer.getBufferStats()
    }
    
    /**
     * Get analyzer performance metrics
     * 
     * @return AnalyzerPerformance with operation statistics
     */
    fun getPerformanceMetrics(): AnalyzerPerformance {
        val operations = operationCounter.get()
        val totalTime = totalProcessingTime.get()
        val maxTime = maxProcessingTime.get()
        
        return AnalyzerPerformance(
            totalOperations = operations,
            totalProcessingTimeMs = totalTime / 1_000_000,
            averageProcessingTimeMs = if (operations > 0) (totalTime / operations).toFloat() / 1_000_000 else 0f,
            maxProcessingTimeMs = maxTime / 1_000_000,
            bufferStats = getBufferStats()
        )
    }
    
    /**
     * Reset performance statistics
     */
    fun resetPerformanceMetrics() {
        operationCounter.set(0)
        totalProcessingTime.set(0)
        maxProcessingTime.set(0)
    }
    
    /**
     * Get frames within specified time window
     * 
     * @param windowMs Time window in milliseconds
     * @return List of frames within window
     */
    fun getFramesInTimeWindow(windowMs: Long): List<FrameFingerprint> {
        return frameBuffer.getFramesInTimeWindow(windowMs)
    }
    
    /**
     * Analyze content diversity in buffer
     * Useful for understanding content patterns
     * 
     * @return DiversityAnalysis with content characteristics
     */
    fun analyzeDiversity(): DiversityAnalysis {
        val allFrames = frameBuffer.getAllValidFrames()
        
        if (allFrames.size < 2) {
            return DiversityAnalysis(
                frameCount = allFrames.size,
                averageSimilarity = 1.0f,
                diversityScore = 0.0f,
                contentStability = 1.0f,
                timeSpanMs = 0L
            )
        }
        
        val averageSimilarity = FingerprintUtils.calculateAverageSimilarity(allFrames)
        val diversityScore = 1.0f - averageSimilarity
        
        // Calculate content stability (how consistent content is over time)
        val timeSpan = allFrames.maxOf { it.timestamp } - allFrames.minOf { it.timestamp }
        val clusters = FingerprintUtils.clusterByTemporalSimilarity(allFrames)
        val contentStability = if (clusters.size > 1) {
            clusters.maxOf { it.size }.toFloat() / allFrames.size
        } else {
            1.0f
        }
        
        return DiversityAnalysis(
            frameCount = allFrames.size,
            averageSimilarity = averageSimilarity,
            diversityScore = diversityScore,
            contentStability = contentStability,
            timeSpanMs = timeSpan,
            clusterCount = clusters.size
        )
    }
    
    /**
     * Update performance metrics after operation
     */
    private fun updatePerformanceMetrics(startTimeNanos: Long) {
        val processingTime = System.nanoTime() - startTimeNanos
        
        operationCounter.incrementAndGet()
        totalProcessingTime.addAndGet(processingTime)
        
        // Update max time atomically
        var currentMax = maxProcessingTime.get()
        while (processingTime > currentMax) {
            if (maxProcessingTime.compareAndSet(currentMax, processingTime)) {
                break
            }
            currentMax = maxProcessingTime.get()
        }
    }
    
    /**
     * Calculate confidence score for analysis result
     */
    private fun calculateConfidence(
        mostSimilar: FrameMatch?,
        config: AnalysisConfig
    ): Float {
        return when {
            mostSimilar == null -> 1.0f // High confidence when no similar frame
            mostSimilar.similarity >= config.skipThreshold -> 0.9f // High confidence to skip
            mostSimilar.similarity <= config.processThreshold -> 0.8f // Good confidence to process
            else -> 0.5f // Medium confidence in borderline cases
        }
    }
    
    /**
     * Clear all frames and reset state
     */
    fun clear() {
        frameBuffer.clear()
        resetPerformanceMetrics()
    }
    
    /**
     * Get current configuration
     */
    fun getConfiguration(): SimilarityAnalyzerConfig {
        val bufferConfig = frameBuffer.getConfig()
        return SimilarityAnalyzerConfig(
            bufferSize = bufferConfig.maxSize,
            frameExpiryMs = bufferConfig.maxAgeMs,
            defaultSimilarityThreshold = defaultSimilarityThreshold,
            currentUtilization = bufferConfig.utilization
        )
    }
}

/**
 * Configuration for frame analysis
 */
data class AnalysisConfig(
    val minSimilarityThreshold: Float = 0.5f,    // Minimum similarity to consider
    val skipThreshold: Float = 0.95f,            // Skip processing if similarity above this
    val processThreshold: Float = 0.8f,          // Process if similarity below this
    val maxSimilarAgeMs: Long = 10_000L          // Max age for similar frame consideration
)

/**
 * Result of frame analysis
 */
data class AnalysisResult(
    val shouldProcess: Boolean,
    val reason: String,
    val mostSimilarMatch: FrameMatch?,
    val confidence: Float,
    val processingTimeMs: Long
) {
    override fun toString(): String {
        val similarityStr = mostSimilarMatch?.let { 
            String.format("%.3f", it.similarity) 
        } ?: "none"
        
        return "AnalysisResult(process=$shouldProcess, similarity=$similarityStr, " +
                "confidence=${String.format("%.2f", confidence)}, time=${processingTimeMs}ms)"
    }
}

/**
 * Performance metrics for the analyzer
 */
data class AnalyzerPerformance(
    val totalOperations: Long,
    val totalProcessingTimeMs: Long,
    val averageProcessingTimeMs: Float,
    val maxProcessingTimeMs: Long,
    val bufferStats: BufferStats
) {
    fun meetsPerformanceRequirements(): Boolean {
        return averageProcessingTimeMs < 5.0f && maxProcessingTimeMs < 20L
    }
    
    override fun toString(): String {
        return "AnalyzerPerformance(ops=$totalOperations, " +
                "avg=${String.format("%.2f", averageProcessingTimeMs)}ms, " +
                "max=${maxProcessingTimeMs}ms, meets_req=${meetsPerformanceRequirements()})"
    }
}

/**
 * Content diversity analysis
 */
data class DiversityAnalysis(
    val frameCount: Int,
    val averageSimilarity: Float,
    val diversityScore: Float,
    val contentStability: Float,
    val timeSpanMs: Long,
    val clusterCount: Int = 0
) {
    override fun toString(): String {
        return "DiversityAnalysis(frames=$frameCount, " +
                "diversity=${String.format("%.3f", diversityScore)}, " +
                "stability=${String.format("%.3f", contentStability)}, " +
                "span=${timeSpanMs}ms, clusters=$clusterCount)"
    }
}

/**
 * Similarity analyzer configuration
 */
data class SimilarityAnalyzerConfig(
    val bufferSize: Int,
    val frameExpiryMs: Long,
    val defaultSimilarityThreshold: Float,
    val currentUtilization: Float
) {
    override fun toString(): String {
        return "SimilarityAnalyzerConfig(buffer=$bufferSize, " +
                "expiry=${frameExpiryMs}ms, " +
                "threshold=${String.format("%.2f", defaultSimilarityThreshold)}, " +
                "util=${String.format("%.1f", currentUtilization * 100)}%)"
    }
}