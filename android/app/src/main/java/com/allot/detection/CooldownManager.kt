package com.allot.detection

import android.util.Log
import org.json.JSONObject
import kotlin.math.*

/**
 * Types of cooldown periods
 */
enum class CooldownType {
    NONE,
    BASE,
    EXTENDED
}

/**
 * Configuration for cooldown manager
 */
data class CooldownConfig(
    // Base cooldown period (3 seconds as per requirements)
    val baseCooldownPeriodMs: Long = 3_000L,
    
    // Extended cooldown period (additional 2 seconds as per requirements)
    val extendedCooldownPeriodMs: Long = 2_000L,
    
    // Safe content window (10 seconds as per requirements)
    val safeContentWindowMs: Long = 10_000L,
    
    // Default temporal window duration
    val defaultTemporalWindowMs: Long = 15_000L,
    
    // Similarity thresholds
    val similarContentThreshold: Float = 0.85f,
    val skipAnalysisThreshold: Float = 0.90f,
    
    // Frequency adjustments during cooldown
    val baseCooldownFrequencyMultiplier: Float = 0.2f,
    val extendedCooldownFrequencyMultiplier: Float = 0.1f,
    
    // Similarity threshold adjustments during cooldown
    val baseCooldownSimilarityAdjustment: Float = 0.05f,
    val extendedCooldownSimilarityAdjustment: Float = 0.10f
) {
    /**
     * Convert to JSON for serialization
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("baseCooldownPeriodMs", baseCooldownPeriodMs)
            put("extendedCooldownPeriodMs", extendedCooldownPeriodMs)
            put("safeContentWindowMs", safeContentWindowMs)
            put("similarContentThreshold", similarContentThreshold)
            put("skipAnalysisThreshold", skipAnalysisThreshold)
            put("baseCooldownFrequencyMultiplier", baseCooldownFrequencyMultiplier)
            put("extendedCooldownFrequencyMultiplier", extendedCooldownFrequencyMultiplier)
        }
    }
}

/**
 * Safe content statistics
 */
data class SafeContentStats(
    val totalFrames: Int,
    val safeFrames: Int,
    val unsafeFrames: Int,
    val safeRatio: Float,
    val windowDurationMs: Long,
    val oldestFrameAge: Long
) {
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("totalFrames", totalFrames)
            put("safeFrames", safeFrames)
            put("unsafeFrames", unsafeFrames)
            put("safeRatio", safeRatio)
            put("windowDurationMs", windowDurationMs)
            put("oldestFrameAge", oldestFrameAge)
        }
    }
}

/**
 * CooldownManager - Advanced cooldown and temporal awareness functionality
 * 
 * Implements sophisticated cooldown period management, extended cooldown logic,
 * and temporal window tracking for the Smart Content Detection System.
 */
class CooldownManager(
    private val config: CooldownConfig = CooldownConfig()
) {
    companion object {
        const val TAG = "CooldownManager"
    }
    
    // Cooldown state tracking
    private var cooldownStartTime: Long = 0L
    private var extendedCooldownStartTime: Long = 0L
    private var lastHarmfulFingerprint: FrameFingerprint? = null
    private var cooldownExtensionCount: Int = 0
    
    // Temporal window tracking
    private val temporalWindows = mutableMapOf<String, TemporalWindow>()
    private val safeContentWindow = TemporalWindow("safe_content", config.safeContentWindowMs)
    
    // Performance tracking
    private val cooldownMetrics = CooldownMetrics()
    
    /**
     * Start cooldown period after harmful content detection
     */
    fun startCooldown(harmfulFingerprint: FrameFingerprint, reason: String = "Harmful content detected") {
        val currentTime = System.currentTimeMillis()
        
        cooldownStartTime = currentTime
        lastHarmfulFingerprint = harmfulFingerprint
        cooldownExtensionCount = 0
        
        // Reset extended cooldown
        extendedCooldownStartTime = 0L
        
        Log.d(TAG, "Cooldown started: $reason")
        cooldownMetrics.recordCooldownStart(currentTime, reason)
    }
    
    /**
     * Check if currently in cooldown period
     */
    fun isInCooldown(): Boolean {
        if (cooldownStartTime == 0L) return false
        
        val currentTime = System.currentTimeMillis()
        val baseCooldownElapsed = currentTime - cooldownStartTime
        
        // Check base cooldown period
        if (baseCooldownElapsed < config.baseCooldownPeriodMs) {
            return true
        }
        
        // Check extended cooldown period
        if (extendedCooldownStartTime > 0L) {
            val extendedCooldownElapsed = currentTime - extendedCooldownStartTime
            return extendedCooldownElapsed < config.extendedCooldownPeriodMs
        }
        
        return false
    }
    
    /**
     * Check if cooldown should be extended for persistent similar content
     */
    fun shouldExtendCooldown(currentFingerprint: FrameFingerprint): Boolean {
        if (!isInCooldown() || lastHarmfulFingerprint == null) return false
        
        val similarity = calculateFrameSimilarity(currentFingerprint, lastHarmfulFingerprint!!)
        
        // Extend cooldown if content is still very similar
        if (similarity > config.similarContentThreshold) {
            extendCooldown("Similar content detected: ${(similarity * 100).toInt()}%")
            return true
        }
        
        return false
    }
    
    /**
     * Extend cooldown period for persistent similar content
     */
    fun extendCooldown(reason: String) {
        if (extendedCooldownStartTime == 0L) {
            extendedCooldownStartTime = System.currentTimeMillis()
            cooldownExtensionCount++
            
            Log.d(TAG, "Cooldown extended: $reason (extension #$cooldownExtensionCount)")
            cooldownMetrics.recordCooldownExtension(System.currentTimeMillis(), reason)
        }
    }
    
    /**
     * Get remaining cooldown time in milliseconds
     */
    fun getRemainingCooldownMs(): Long {
        if (!isInCooldown()) return 0L
        
        val currentTime = System.currentTimeMillis()
        
        // Calculate remaining base cooldown
        val baseCooldownRemaining = maxOf(0L, config.baseCooldownPeriodMs - (currentTime - cooldownStartTime))
        
        // Calculate remaining extended cooldown
        val extendedCooldownRemaining = if (extendedCooldownStartTime > 0L) {
            maxOf(0L, config.extendedCooldownPeriodMs - (currentTime - extendedCooldownStartTime))
        } else 0L
        
        return kotlin.math.max(baseCooldownRemaining, extendedCooldownRemaining)
    }
    
    /**
     * Clear cooldown state
     */
    fun clearCooldown(reason: String = "Cooldown cleared") {
        if (cooldownStartTime > 0L) {
            val duration = System.currentTimeMillis() - cooldownStartTime
            Log.d(TAG, "Cooldown cleared: $reason (duration: ${duration}ms)")
            cooldownMetrics.recordCooldownEnd(System.currentTimeMillis(), reason, duration)
        }
        
        cooldownStartTime = 0L
        extendedCooldownStartTime = 0L
        lastHarmfulFingerprint = null
        cooldownExtensionCount = 0
    }
    
    /**
     * Update safe content temporal window
     */
    fun updateSafeContentWindow(fingerprint: FrameFingerprint, isSafe: Boolean) {
        val currentTime = System.currentTimeMillis()
        
        if (isSafe) {
            safeContentWindow.addSafeFrame(fingerprint, currentTime)
        } else {
            safeContentWindow.addUnsafeFrame(fingerprint, currentTime)
        }
        
        // Clean old entries
        safeContentWindow.cleanOldEntries(currentTime)
    }
    
    /**
     * Check if in safe content optimization window (10 seconds of safe content)
     */
    fun isInSafeContentWindow(): Boolean {
        val currentTime = System.currentTimeMillis()
        return safeContentWindow.isSafeWindowActive(currentTime, config.safeContentWindowMs)
    }
    
    /**
     * Get safe content window statistics
     */
    fun getSafeContentStats(): SafeContentStats {
        val currentTime = System.currentTimeMillis()
        return safeContentWindow.getStats(currentTime)
    }
    
    /**
     * Get comprehensive cooldown status
     */
    fun getCooldownStatus(): CooldownStatus {
        return CooldownStatus(
            isInCooldown = isInCooldown(),
            cooldownType = when {
                extendedCooldownStartTime > 0L -> CooldownType.EXTENDED
                cooldownStartTime > 0L -> CooldownType.BASE
                else -> CooldownType.NONE
            },
            remainingTimeMs = getRemainingCooldownMs(),
            extensionCount = cooldownExtensionCount,
            startTime = cooldownStartTime,
            extendedStartTime = extendedCooldownStartTime,
            lastHarmfulSimilarity = 0f, // Would need current frame to calculate
            safeContentWindowActive = isInSafeContentWindow(),
            metrics = cooldownMetrics.getMetrics()
        )
    }
    
    /**
     * Get cooldown manager statistics
     */
    fun getCooldownManagerStats(): CooldownManagerStats {
        return CooldownManagerStats(
            totalCooldowns = cooldownMetrics.totalCooldowns,
            totalExtensions = cooldownMetrics.totalExtensions,
            averageCooldownDuration = cooldownMetrics.getAverageCooldownDuration(),
            safeContentStats = getSafeContentStats(),
            temporalWindowCount = temporalWindows.size,
            config = config
        )
    }
    
    /**
     * Clear all temporal data
     */
    fun clearAll() {
        clearCooldown("Manager reset")
        safeContentWindow.clear()
        temporalWindows.clear()
        cooldownMetrics.reset()
        
        Log.d(TAG, "CooldownManager cleared")
    }
    
    // Private helper methods
    
    private fun calculateFrameSimilarity(fp1: FrameFingerprint, fp2: FrameFingerprint): Float {
        // Use Hamming distance for similarity calculation
        val xor = fp1.hash xor fp2.hash
        val hammingDistance = java.lang.Long.bitCount(xor)
        return 1.0f - (hammingDistance / 64.0f) // 64 bits in hash
    }
}

/**
 * Temporal window for tracking content over time
 */
class TemporalWindow(
    val windowId: String,
    private val windowDurationMs: Long
) {
    private val safeFrames = mutableListOf<Pair<FrameFingerprint, Long>>()
    private val unsafeFrames = mutableListOf<Pair<FrameFingerprint, Long>>()
    private var lastUpdateTime: Long = 0L
    
    fun addSafeFrame(fingerprint: FrameFingerprint, timestamp: Long) {
        safeFrames.add(Pair(fingerprint, timestamp))
        lastUpdateTime = timestamp
    }
    
    fun addUnsafeFrame(fingerprint: FrameFingerprint, timestamp: Long) {
        unsafeFrames.add(Pair(fingerprint, timestamp))
        lastUpdateTime = timestamp
    }
    
    fun cleanOldEntries(currentTime: Long) {
        val cutoffTime = currentTime - windowDurationMs
        safeFrames.removeAll { it.second < cutoffTime }
        unsafeFrames.removeAll { it.second < cutoffTime }
    }
    
    fun isSafeWindowActive(currentTime: Long, requiredDuration: Long): Boolean {
        cleanOldEntries(currentTime)
        
        // Check if we have enough safe content and no recent unsafe content
        val recentUnsafeFrames = unsafeFrames.filter { currentTime - it.second < requiredDuration }
        val recentSafeFrames = safeFrames.filter { currentTime - it.second < requiredDuration }
        
        return recentUnsafeFrames.isEmpty() && recentSafeFrames.size >= 3
    }
    
    fun getStats(currentTime: Long): SafeContentStats {
        cleanOldEntries(currentTime)
        
        val totalFrames = safeFrames.size + unsafeFrames.size
        val safeRatio = if (totalFrames > 0) safeFrames.size.toFloat() / totalFrames else 0f
        
        return SafeContentStats(
            totalFrames = totalFrames,
            safeFrames = safeFrames.size,
            unsafeFrames = unsafeFrames.size,
            safeRatio = safeRatio,
            windowDurationMs = windowDurationMs,
            oldestFrameAge = if (totalFrames > 0) {
                val oldestTime = minOf(
                    safeFrames.minOfOrNull { it.second } ?: Long.MAX_VALUE,
                    unsafeFrames.minOfOrNull { it.second } ?: Long.MAX_VALUE
                )
                currentTime - oldestTime
            } else 0L
        )
    }
    
    fun getLastUpdateTime(): Long = lastUpdateTime
    
    fun clear() {
        safeFrames.clear()
        unsafeFrames.clear()
        lastUpdateTime = 0L
    }
}

/**
 * Cooldown performance metrics
 */
class CooldownMetrics {
    var totalCooldowns: Int = 0
    var totalExtensions: Int = 0
    private val cooldownDurations = mutableListOf<Long>()
    private val cooldownReasons = mutableMapOf<String, Int>()
    
    fun recordCooldownStart(timestamp: Long, reason: String) {
        totalCooldowns++
        cooldownReasons[reason] = cooldownReasons.getOrDefault(reason, 0) + 1
    }
    
    fun recordCooldownExtension(timestamp: Long, reason: String) {
        totalExtensions++
    }
    
    fun recordCooldownEnd(timestamp: Long, reason: String, duration: Long) {
        cooldownDurations.add(duration)
    }
    
    fun getAverageCooldownDuration(): Float {
        return if (cooldownDurations.isNotEmpty()) {
            cooldownDurations.average().toFloat()
        } else 0f
    }
    
    fun getMetrics(): Map<String, Any> {
        return mapOf(
            "totalCooldowns" to totalCooldowns,
            "totalExtensions" to totalExtensions,
            "averageDuration" to getAverageCooldownDuration(),
            "reasonCounts" to cooldownReasons.toMap()
        )
    }
    
    fun reset() {
        totalCooldowns = 0
        totalExtensions = 0
        cooldownDurations.clear()
        cooldownReasons.clear()
    }
}

/**
 * Cooldown status information
 */
data class CooldownStatus(
    val isInCooldown: Boolean,
    val cooldownType: CooldownType,
    val remainingTimeMs: Long,
    val extensionCount: Int,
    val startTime: Long,
    val extendedStartTime: Long,
    val lastHarmfulSimilarity: Float,
    val safeContentWindowActive: Boolean,
    val metrics: Map<String, Any>
) {
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("isInCooldown", isInCooldown)
            put("cooldownType", cooldownType.name)
            put("remainingTimeMs", remainingTimeMs)
            put("extensionCount", extensionCount)
            put("startTime", startTime)
            put("extendedStartTime", extendedStartTime)
            put("lastHarmfulSimilarity", lastHarmfulSimilarity)
            put("safeContentWindowActive", safeContentWindowActive)
            
            val metricsJson = JSONObject()
            metrics.forEach { (key, value) -> metricsJson.put(key, value) }
            put("metrics", metricsJson)
        }
    }
}

/**
 * Cooldown manager statistics
 */
data class CooldownManagerStats(
    val totalCooldowns: Int,
    val totalExtensions: Int,
    val averageCooldownDuration: Float,
    val safeContentStats: SafeContentStats,
    val temporalWindowCount: Int,
    val config: CooldownConfig
) {
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("totalCooldowns", totalCooldowns)
            put("totalExtensions", totalExtensions)
            put("averageCooldownDuration", averageCooldownDuration)
            put("safeContentStats", safeContentStats.toJson())
            put("temporalWindowCount", temporalWindowCount)
            put("config", config.toJson())
        }
    }
}