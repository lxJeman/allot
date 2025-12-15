package com.allot.detection

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * FingerprintUtils - Utility functions for FrameFingerprint operations
 * 
 * Provides high-performance functions for fingerprint comparison, validation,
 * and analysis. Optimized for the Smart Content Detection System requirements.
 * 
 * Performance Requirements:
 * - Similarity calculation: <1ms per comparison
 * - Hamming distance: <0.5ms per calculation
 * - Batch operations: Linear scaling with input size
 */
object FingerprintUtils {
    
    // Constants for similarity calculations
    private const val BITS_PER_LONG = 64
    private const val MAX_HAMMING_DISTANCE = BITS_PER_LONG
    
    // Thresholds for various similarity levels
    const val SIMILARITY_IDENTICAL = 1.0f      // 100% identical
    const val SIMILARITY_VERY_HIGH = 0.95f     // 95%+ similar
    const val SIMILARITY_HIGH = 0.90f          // 90%+ similar (default threshold)
    const val SIMILARITY_MEDIUM = 0.75f        // 75%+ similar
    const val SIMILARITY_LOW = 0.50f           // 50%+ similar
    
    /**
     * Calculate Hamming distance between two hash values
     * Counts the number of differing bits between two 64-bit hashes
     * 
     * @param hash1 First hash value
     * @param hash2 Second hash value
     * @return Number of differing bits (0-64)
     */
    fun calculateHammingDistance(hash1: Long, hash2: Long): Int {
        // XOR the hashes to find differing bits, then count them
        val xor = hash1 xor hash2
        return java.lang.Long.bitCount(xor)
    }
    
    /**
     * Calculate similarity percentage between two fingerprints
     * Uses Hamming distance to determine visual similarity
     * 
     * @param fp1 First fingerprint
     * @param fp2 Second fingerprint
     * @return Similarity as float (0.0 = completely different, 1.0 = identical)
     */
    fun calculateSimilarity(fp1: FrameFingerprint, fp2: FrameFingerprint): Float {
        val hammingDistance = calculateHammingDistance(fp1.hash, fp2.hash)
        val similarBits = MAX_HAMMING_DISTANCE - hammingDistance
        return similarBits.toFloat() / MAX_HAMMING_DISTANCE
    }
    
    /**
     * Fast similarity check with threshold
     * Optimized for early termination when similarity is below threshold
     * 
     * @param fp1 First fingerprint
     * @param fp2 Second fingerprint
     * @param threshold Minimum similarity required (0.0-1.0)
     * @return true if similarity >= threshold
     */
    fun isSimilar(fp1: FrameFingerprint, fp2: FrameFingerprint, threshold: Float): Boolean {
        val hammingDistance = calculateHammingDistance(fp1.hash, fp2.hash)
        val maxAllowedDistance = ((1.0f - threshold) * MAX_HAMMING_DISTANCE).toInt()
        return hammingDistance <= maxAllowedDistance
    }
    
    /**
     * Check if two fingerprints represent identical content
     * Uses strict criteria including hash, dimensions, and temporal proximity
     * 
     * @param fp1 First fingerprint
     * @param fp2 Second fingerprint
     * @param maxTimeDiffMs Maximum time difference to consider identical (default: 1000ms)
     * @return true if fingerprints represent identical content
     */
    fun areIdentical(
        fp1: FrameFingerprint, 
        fp2: FrameFingerprint, 
        maxTimeDiffMs: Long = 1000L
    ): Boolean {
        // Check hash identity first (fastest check)
        if (fp1.hash != fp2.hash) return false
        
        // Check dimensions match
        if (fp1.width != fp2.width || fp1.height != fp2.height) return false
        
        // Check temporal proximity
        val timeDiff = abs(fp1.timestamp - fp2.timestamp)
        return timeDiff <= maxTimeDiffMs
    }
    
    /**
     * Detect significant visual change between consecutive frames
     * Considers both hash similarity and frame characteristics
     * 
     * @param current Current frame fingerprint
     * @param previous Previous frame fingerprint
     * @param changeThreshold Minimum change required (0.0-1.0, default: 0.1)
     * @return true if significant change detected
     */
    fun isSignificantChange(
        current: FrameFingerprint,
        previous: FrameFingerprint,
        changeThreshold: Float = 0.1f
    ): Boolean {
        val similarity = calculateSimilarity(current, previous)
        val change = 1.0f - similarity
        
        // Consider dimension changes as significant
        val dimensionChange = current.width != previous.width || current.height != previous.height
        
        // Consider text density changes
        val textDensityChange = abs(current.textDensity - previous.textDensity) > 0.2f
        
        return change >= changeThreshold || dimensionChange || textDensityChange
    }
    
    /**
     * Find the most similar fingerprint from a collection
     * 
     * @param target Target fingerprint to match against
     * @param candidates Collection of candidate fingerprints
     * @param minSimilarity Minimum similarity threshold (default: 0.5)
     * @return Pair of (fingerprint, similarity) or null if no match found
     */
    fun findMostSimilar(
        target: FrameFingerprint,
        candidates: Collection<FrameFingerprint>,
        minSimilarity: Float = 0.5f
    ): Pair<FrameFingerprint, Float>? {
        var bestMatch: FrameFingerprint? = null
        var bestSimilarity = minSimilarity
        
        for (candidate in candidates) {
            val similarity = calculateSimilarity(target, candidate)
            if (similarity > bestSimilarity) {
                bestMatch = candidate
                bestSimilarity = similarity
            }
        }
        
        return bestMatch?.let { Pair(it, bestSimilarity) }
    }
    
    /**
     * Find all fingerprints above similarity threshold
     * 
     * @param target Target fingerprint to match against
     * @param candidates Collection of candidate fingerprints
     * @param threshold Similarity threshold (default: 0.9)
     * @return List of pairs (fingerprint, similarity) sorted by similarity descending
     */
    fun findSimilarFingerprints(
        target: FrameFingerprint,
        candidates: Collection<FrameFingerprint>,
        threshold: Float = 0.9f
    ): List<Pair<FrameFingerprint, Float>> {
        return candidates
            .map { candidate -> Pair(candidate, calculateSimilarity(target, candidate)) }
            .filter { (_, similarity) -> similarity >= threshold }
            .sortedByDescending { (_, similarity) -> similarity }
    }
    
    /**
     * Calculate average similarity within a collection of fingerprints
     * Useful for analyzing content diversity in a buffer
     * 
     * @param fingerprints Collection of fingerprints to analyze
     * @return Average pairwise similarity (0.0-1.0)
     */
    fun calculateAverageSimilarity(fingerprints: Collection<FrameFingerprint>): Float {
        if (fingerprints.size < 2) return 1.0f
        
        val fingerprintList = fingerprints.toList()
        var totalSimilarity = 0.0f
        var comparisons = 0
        
        for (i in fingerprintList.indices) {
            for (j in i + 1 until fingerprintList.size) {
                totalSimilarity += calculateSimilarity(fingerprintList[i], fingerprintList[j])
                comparisons++
            }
        }
        
        return if (comparisons > 0) totalSimilarity / comparisons else 1.0f
    }
    
    /**
     * Validate fingerprint collection for consistency
     * Checks for common issues like invalid data, extreme outliers, etc.
     * 
     * @param fingerprints Collection to validate
     * @return ValidationResult with details
     */
    fun validateFingerprintCollection(fingerprints: Collection<FrameFingerprint>): ValidationResult {
        val issues = mutableListOf<String>()
        var validCount = 0
        
        for (fp in fingerprints) {
            if (FrameFingerprint.isValid(fp)) {
                validCount++
            } else {
                issues.add("Invalid fingerprint: ${fp.toCompactString()}")
            }
        }
        
        // Check for temporal ordering
        val sortedByTime = fingerprints.sortedBy { it.timestamp }
        if (sortedByTime != fingerprints.toList()) {
            issues.add("Fingerprints not in temporal order")
        }
        
        // Check for duplicates
        val uniqueHashes = fingerprints.map { it.hash }.toSet()
        if (uniqueHashes.size != fingerprints.size) {
            issues.add("Duplicate hashes detected")
        }
        
        return ValidationResult(
            isValid = issues.isEmpty(),
            validCount = validCount,
            totalCount = fingerprints.size,
            issues = issues
        )
    }
    
    /**
     * Create a similarity matrix for a collection of fingerprints
     * Useful for debugging and analysis
     * 
     * @param fingerprints Collection of fingerprints
     * @return 2D array of similarity values
     */
    fun createSimilarityMatrix(fingerprints: List<FrameFingerprint>): Array<FloatArray> {
        val size = fingerprints.size
        val matrix = Array(size) { FloatArray(size) }
        
        for (i in 0 until size) {
            for (j in 0 until size) {
                matrix[i][j] = if (i == j) {
                    1.0f
                } else {
                    calculateSimilarity(fingerprints[i], fingerprints[j])
                }
            }
        }
        
        return matrix
    }
    
    /**
     * Calculate temporal clustering of similar fingerprints
     * Groups fingerprints by similarity and temporal proximity
     * 
     * @param fingerprints Collection of fingerprints (should be time-ordered)
     * @param similarityThreshold Similarity threshold for clustering
     * @param maxTimeGapMs Maximum time gap within a cluster
     * @return List of clusters, each containing similar fingerprints
     */
    fun clusterByTemporalSimilarity(
        fingerprints: List<FrameFingerprint>,
        similarityThreshold: Float = 0.9f,
        maxTimeGapMs: Long = 5000L
    ): List<List<FrameFingerprint>> {
        if (fingerprints.isEmpty()) return emptyList()
        
        val clusters = mutableListOf<MutableList<FrameFingerprint>>()
        var currentCluster = mutableListOf(fingerprints.first())
        
        for (i in 1 until fingerprints.size) {
            val current = fingerprints[i]
            val previous = fingerprints[i - 1]
            
            val isSimilar = calculateSimilarity(current, previous) >= similarityThreshold
            val isTemporallyClose = (current.timestamp - previous.timestamp) <= maxTimeGapMs
            
            if (isSimilar && isTemporallyClose) {
                currentCluster.add(current)
            } else {
                clusters.add(currentCluster)
                currentCluster = mutableListOf(current)
            }
        }
        
        clusters.add(currentCluster)
        return clusters
    }
}

/**
 * Result of fingerprint collection validation
 */
data class ValidationResult(
    val isValid: Boolean,
    val validCount: Int,
    val totalCount: Int,
    val issues: List<String>
) {
    fun getValidationRate(): Float = if (totalCount > 0) validCount.toFloat() / totalCount else 1.0f
    
    override fun toString(): String {
        return "ValidationResult(valid=$isValid, rate=${String.format("%.2f", getValidationRate())}, " +
                "count=$validCount/$totalCount, issues=${issues.size})"
    }
}