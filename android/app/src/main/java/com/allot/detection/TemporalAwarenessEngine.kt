package com.allot.detection

import android.util.Log
import org.json.JSONObject

/**
 * TemporalAwarenessEngine - Enhanced temporal pattern analysis and state-based analysis control
 * 
 * Implements sophisticated temporal window tracking, pattern recognition,
 * and state-based analysis skipping and resumption functionality.
 */
class TemporalAwarenessEngine {
    companion object {
        const val TAG = "TemporalAwarenessEngine"
    }
    
    // Temporal pattern tracking
    private val contentPatterns = mutableMapOf<String, ContentPattern>()
    private val analysisSkipTracker = AnalysisSkipTracker()
    
    // State-based temporal windows
    private val stateTemporalWindows = mutableMapOf<DetectionState, StateTemporalWindow>()
    
    init {
        initializeStateWindows()
        Log.d(TAG, "TemporalAwarenessEngine initialized")
    }
    
    /**
     * Process frame with temporal awareness and determine analysis adjustments
     */
    fun processFrameWithTemporalAwareness(
        context: StateContext,
        cooldownStatus: CooldownStatus,
        recentDecisions: List<StateDecision>
    ): TemporalAnalysisResult {
        
        val startTime = System.currentTimeMillis()
        
        try {
            // Update temporal windows for current state
            updateStateTemporalWindow(context.currentState, context.fingerprint, context.isHarmfulContent)
            
            // Analyze content patterns
            val patternAnalysis = analyzeContentPatterns(context)
            
            // Determine if analysis should be skipped based on temporal patterns
            val shouldSkip = shouldSkipAnalysisBasedOnTemporal(context, cooldownStatus, patternAnalysis)
            val skipReason = if (shouldSkip) "Temporal pattern analysis" else "Normal analysis flow"
            
            // Calculate temporal-based frequency adjustment
            val frequencyAdjustment = calculateTemporalFrequencyAdjustment(context, cooldownStatus, patternAnalysis)
            
            // Calculate similarity threshold adjustment
            val similarityAdjustment = calculateTemporalSimilarityAdjustment(context, patternAnalysis)
            
            // Record decision
            analysisSkipTracker.recordDecision(shouldSkip, skipReason, context)
            
            val result = TemporalAnalysisResult(
                shouldSkipAnalysis = shouldSkip,
                skipReason = skipReason,
                temporalFrequencyAdjustment = frequencyAdjustment,
                temporalSimilarityAdjustment = similarityAdjustment,
                patternAnalysis = patternAnalysis,
                processingTimeMs = System.currentTimeMillis() - startTime
            )
            
            Log.d(TAG, "Temporal analysis: ${result.toCompactString()}")
            return result
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in temporal awareness processing: ${e.message}", e)
            
            // Return safe fallback
            return TemporalAnalysisResult(
                shouldSkipAnalysis = false,
                skipReason = "Error in temporal processing",
                temporalFrequencyAdjustment = 1.0f,
                temporalSimilarityAdjustment = 0f,
                patternAnalysis = ContentPatternAnalysis.empty(),
                processingTimeMs = System.currentTimeMillis() - startTime
            )
        }
    }
    
    /**
     * Get temporal awareness statistics
     */
    fun getTemporalAwarenessStats(): TemporalAwarenessStats {
        return TemporalAwarenessStats(
            totalPatterns = contentPatterns.size,
            activePatterns = contentPatterns.values.count { it.isActive() },
            analysisSkipStats = analysisSkipTracker.getStats(),
            stateWindowStats = stateTemporalWindows.mapValues { (_, window) ->
                window.getStats()
            }
        )
    }
    
    /**
     * Get state-specific temporal statistics
     */
    fun getStateTemporalStats(state: DetectionState): StateTemporalStats? {
        return stateTemporalWindows[state]?.getStats()
    }
    
    /**
     * Clear all temporal data
     */
    fun clearAll() {
        contentPatterns.clear()
        analysisSkipTracker.clear()
        stateTemporalWindows.values.forEach { it.clear() }
        
        Log.d(TAG, "TemporalAwarenessEngine cleared")
    }
    
    // Private helper methods
    
    private fun initializeStateWindows() {
        DetectionState.values().forEach { state ->
            val windowDuration = when (state) {
                DetectionState.SCANNING -> 30_000L
                DetectionState.HARMFUL_DETECTED -> 10_000L
                DetectionState.SCROLLING_AWAY -> 15_000L
                DetectionState.COOLDOWN -> 20_000L
                DetectionState.SAFE_CONTENT -> 60_000L
            }
            
            stateTemporalWindows[state] = StateTemporalWindow(state.name, windowDuration)
        }
    }
    
    private fun updateStateTemporalWindow(
        state: DetectionState,
        fingerprint: FrameFingerprint,
        isHarmful: Boolean
    ) {
        val window = stateTemporalWindows[state] ?: return
        window.addFrame(fingerprint, isHarmful)
        window.cleanOldEntries()
    }
    
    private fun analyzeContentPatterns(context: StateContext): ContentPatternAnalysis {
        val patternId = generatePatternId(context.fingerprint)
        
        // Get or create content pattern
        val pattern = contentPatterns.getOrPut(patternId) {
            ContentPattern(patternId, context.fingerprint)
        }
        
        // Update pattern with current context
        pattern.addObservation(context)
        
        // Clean old patterns
        cleanOldPatterns()
        
        return ContentPatternAnalysis(
            patternId = patternId,
            stability = pattern.calculateStability(),
            frequency = pattern.calculateFrequency(),
            harmfulnessRatio = pattern.calculateHarmfulnessRatio(),
            observationCount = pattern.getObservationCount()
        )
    }
    
    private fun shouldSkipAnalysisBasedOnTemporal(
        context: StateContext,
        cooldownStatus: CooldownStatus,
        patternAnalysis: ContentPatternAnalysis
    ): Boolean {
        
        // Never skip if in critical states
        if (context.currentState == DetectionState.HARMFUL_DETECTED) {
            return false
        }
        
        // Skip if in cooldown and content is very similar
        if (cooldownStatus.isInCooldown && context.similarity > 0.85f) {
            return true
        }
        
        // Skip if content pattern is very stable and safe
        if (patternAnalysis.stability > 0.8f && patternAnalysis.harmfulnessRatio < 0.1f) {
            return true
        }
        
        // Skip if in safe content window and no significant changes
        if (cooldownStatus.safeContentWindowActive && context.similarity > 0.90f) {
            return true
        }
        
        // Force analysis if too much time has passed
        if (context.timeSinceLastAnalysis > 30_000L) {
            return false
        }
        
        return false
    }
    
    private fun calculateTemporalFrequencyAdjustment(
        context: StateContext,
        cooldownStatus: CooldownStatus,
        patternAnalysis: ContentPatternAnalysis
    ): Float {
        
        var adjustment = 1.0f
        
        // Adjust based on cooldown status
        when (cooldownStatus.cooldownType) {
            CooldownType.BASE -> adjustment *= 0.3f
            CooldownType.EXTENDED -> adjustment *= 0.1f
            CooldownType.NONE -> { /* No adjustment */ }
        }
        
        // Adjust based on safe content window
        if (cooldownStatus.safeContentWindowActive) {
            adjustment *= 0.7f
        }
        
        // Adjust based on pattern stability
        if (patternAnalysis.stability > 0.8f) {
            adjustment *= 0.6f
        }
        
        // Adjust based on pattern harmfulness
        if (patternAnalysis.harmfulnessRatio > 0.3f) {
            adjustment *= 1.5f
        }
        
        // Ensure adjustment is within bounds
        return adjustment.coerceIn(0.05f, 3.0f)
    }
    
    private fun calculateTemporalSimilarityAdjustment(
        context: StateContext,
        patternAnalysis: ContentPatternAnalysis
    ): Float {
        
        var adjustment = 0f
        
        // Adjust based on pattern stability (more stable = higher threshold)
        adjustment += patternAnalysis.stability * 0.1f
        
        // Adjust based on harmfulness ratio (more harmful = lower threshold)
        adjustment -= patternAnalysis.harmfulnessRatio * 0.15f
        
        // Ensure adjustment is within bounds
        return adjustment.coerceIn(-0.2f, 0.2f)
    }
    
    private fun generatePatternId(fingerprint: FrameFingerprint): String {
        // Generate a pattern ID based on fingerprint hash
        val hashGroup = fingerprint.hash shr 8 // Group by upper bits
        return "pattern_${hashGroup.toString(16)}"
    }
    
    private fun cleanOldPatterns() {
        val currentTime = System.currentTimeMillis()
        val cutoffTime = currentTime - 300_000L // 5 minutes
        
        contentPatterns.entries.removeAll { (_, pattern) ->
            pattern.getLastSeenTime() < cutoffTime
        }
    }
}

/**
 * Content pattern for tracking repeated content over time
 */
class ContentPattern(
    val patternId: String,
    private val baseFingerprint: FrameFingerprint
) {
    private val observations = mutableListOf<PatternObservation>()
    
    fun addObservation(context: StateContext) {
        val observation = PatternObservation(
            timestamp = System.currentTimeMillis(),
            fingerprint = context.fingerprint,
            similarity = calculateSimilarity(context.fingerprint, baseFingerprint),
            isHarmful = context.isHarmfulContent,
            state = context.currentState
        )
        
        observations.add(observation)
        
        // Limit observation history
        if (observations.size > 100) {
            observations.removeAt(0)
        }
    }
    
    fun calculateStability(): Float {
        if (observations.size < 2) return 0f
        
        val similarities = observations.map { it.similarity }
        val average = similarities.average()
        val variance = similarities.map { (it - average) * (it - average) }.average()
        return (1f - variance.toFloat()).coerceIn(0f, 1f)
    }
    
    fun calculateFrequency(): Float {
        if (observations.isEmpty()) return 0f
        
        val timeSpan = getLastSeenTime() - getFirstSeenTime()
        return if (timeSpan > 0) {
            observations.size.toFloat() / (timeSpan / 1000f) // observations per second
        } else 0f
    }
    
    fun calculateHarmfulnessRatio(): Float {
        if (observations.isEmpty()) return 0f
        return observations.count { it.isHarmful }.toFloat() / observations.size
    }
    
    fun getObservationCount(): Int = observations.size
    fun getFirstSeenTime(): Long = observations.firstOrNull()?.timestamp ?: 0L
    fun getLastSeenTime(): Long = observations.lastOrNull()?.timestamp ?: 0L
    
    fun isActive(): Boolean {
        return (System.currentTimeMillis() - getLastSeenTime()) < 30_000L // Active if seen in last 30 seconds
    }
    
    private fun calculateSimilarity(fp1: FrameFingerprint, fp2: FrameFingerprint): Float {
        val xor = fp1.hash xor fp2.hash
        val hammingDistance = java.lang.Long.bitCount(xor)
        return 1.0f - (hammingDistance / 64.0f)
    }
}

/**
 * Pattern observation data
 */
data class PatternObservation(
    val timestamp: Long,
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val isHarmful: Boolean,
    val state: DetectionState
)

/**
 * Analysis skip tracking
 */
class AnalysisSkipTracker {
    private val skipDecisions = mutableListOf<SkipDecisionRecord>()
    
    fun recordDecision(wasSkipped: Boolean, reason: String, context: StateContext) {
        val record = SkipDecisionRecord(
            timestamp = System.currentTimeMillis(),
            wasSkipped = wasSkipped,
            reason = reason,
            state = context.currentState,
            similarity = context.similarity
        )
        
        skipDecisions.add(record)
        
        // Limit history
        if (skipDecisions.size > 1000) {
            skipDecisions.removeAt(0)
        }
    }
    
    fun getStats(): AnalysisSkipStats {
        val totalDecisions = skipDecisions.size
        val totalSkipped = skipDecisions.count { it.wasSkipped }
        val skipRate = if (totalDecisions > 0) totalSkipped.toFloat() / totalDecisions else 0f
        
        return AnalysisSkipStats(
            totalDecisions = totalDecisions,
            totalSkipped = totalSkipped,
            skipRate = skipRate,
            recentSkipRate = getRecentSkipRate(60_000L) // Last minute
        )
    }
    
    private fun getRecentSkipRate(windowMs: Long): Float {
        val currentTime = System.currentTimeMillis()
        val recentDecisions = skipDecisions.filter { 
            currentTime - it.timestamp < windowMs 
        }
        
        return if (recentDecisions.isNotEmpty()) {
            recentDecisions.count { it.wasSkipped }.toFloat() / recentDecisions.size
        } else 0f
    }
    
    fun clear() {
        skipDecisions.clear()
    }
}

/**
 * State-specific temporal window
 */
class StateTemporalWindow(
    val stateId: String,
    private val windowDurationMs: Long
) {
    private val frames = mutableListOf<StateFrameRecord>()
    
    fun addFrame(fingerprint: FrameFingerprint, isHarmful: Boolean) {
        frames.add(StateFrameRecord(fingerprint, isHarmful, System.currentTimeMillis()))
    }
    
    fun cleanOldEntries() {
        val currentTime = System.currentTimeMillis()
        val cutoffTime = currentTime - windowDurationMs
        frames.removeAll { it.timestamp < cutoffTime }
    }
    
    fun getStats(): StateTemporalStats {
        cleanOldEntries()
        
        val totalFrames = frames.size
        val harmfulFrames = frames.count { it.isHarmful }
        val safeFrames = totalFrames - harmfulFrames
        
        return StateTemporalStats(
            stateId = stateId,
            totalFrames = totalFrames,
            harmfulFrames = harmfulFrames,
            safeFrames = safeFrames,
            harmfulRatio = if (totalFrames > 0) harmfulFrames.toFloat() / totalFrames else 0f,
            windowDurationMs = windowDurationMs,
            oldestFrameAge = frames.minOfOrNull { System.currentTimeMillis() - it.timestamp } ?: 0L
        )
    }
    
    fun clear() {
        frames.clear()
    }
}

// Data classes

/**
 * Skip decision record
 */
data class SkipDecisionRecord(
    val timestamp: Long,
    val wasSkipped: Boolean,
    val reason: String,
    val state: DetectionState,
    val similarity: Float
)

/**
 * State frame record
 */
data class StateFrameRecord(
    val fingerprint: FrameFingerprint,
    val isHarmful: Boolean,
    val timestamp: Long
)

/**
 * Temporal analysis result
 */
data class TemporalAnalysisResult(
    val shouldSkipAnalysis: Boolean,
    val skipReason: String,
    val temporalFrequencyAdjustment: Float,
    val temporalSimilarityAdjustment: Float,
    val patternAnalysis: ContentPatternAnalysis,
    val processingTimeMs: Long
) {
    fun toCompactString(): String {
        return "Skip: $shouldSkipAnalysis (${(temporalFrequencyAdjustment * 100).toInt()}% freq) - $skipReason"
    }
    
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("shouldSkipAnalysis", shouldSkipAnalysis)
            put("skipReason", skipReason)
            put("temporalFrequencyAdjustment", temporalFrequencyAdjustment)
            put("temporalSimilarityAdjustment", temporalSimilarityAdjustment)
            put("processingTimeMs", processingTimeMs)
        }
    }
}

/**
 * Content pattern analysis
 */
data class ContentPatternAnalysis(
    val patternId: String,
    val stability: Float,
    val frequency: Float,
    val harmfulnessRatio: Float,
    val observationCount: Int
) {
    companion object {
        fun empty(): ContentPatternAnalysis {
            return ContentPatternAnalysis(
                patternId = "unknown",
                stability = 0f,
                frequency = 0f,
                harmfulnessRatio = 0f,
                observationCount = 0
            )
        }
    }
    
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("patternId", patternId)
            put("stability", stability)
            put("frequency", frequency)
            put("harmfulnessRatio", harmfulnessRatio)
            put("observationCount", observationCount)
        }
    }
}

/**
 * Analysis skip statistics
 */
data class AnalysisSkipStats(
    val totalDecisions: Int,
    val totalSkipped: Int,
    val skipRate: Float,
    val recentSkipRate: Float
)

/**
 * State temporal statistics
 */
data class StateTemporalStats(
    val stateId: String,
    val totalFrames: Int,
    val harmfulFrames: Int,
    val safeFrames: Int,
    val harmfulRatio: Float,
    val windowDurationMs: Long,
    val oldestFrameAge: Long
)

/**
 * Temporal awareness statistics
 */
data class TemporalAwarenessStats(
    val totalPatterns: Int,
    val activePatterns: Int,
    val analysisSkipStats: AnalysisSkipStats,
    val stateWindowStats: Map<DetectionState, StateTemporalStats>
)