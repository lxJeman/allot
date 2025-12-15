package com.allot.detection

import org.json.JSONObject

/**
 * StateDecision - Represents a decision made by the state machine
 * 
 * Contains all the information about what action should be taken based on the current state
 * and context analysis.
 */
data class StateDecision(
    val shouldAnalyze: Boolean,
    val analysisFrequency: Float,
    val confidenceThreshold: Float,
    val reason: String,
    val newState: DetectionState? = null,
    val processingTimeMs: Long = 0L,
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Check if this decision recommends a state change
     */
    fun recommendsStateChange(): Boolean {
        return newState != null
    }
    
    /**
     * Get the effective analysis frequency (0.0 to 1.0)
     */
    fun getEffectiveFrequency(): Float {
        return if (shouldAnalyze) analysisFrequency else 0f
    }
    
    /**
     * Check if this decision allows immediate analysis
     */
    fun allowsImmediateAnalysis(): Boolean {
        return shouldAnalyze && analysisFrequency >= 1.0f
    }
    
    /**
     * Convert to JSON for logging and debugging
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("shouldAnalyze", shouldAnalyze)
            put("analysisFrequency", analysisFrequency)
            put("confidenceThreshold", confidenceThreshold)
            put("reason", reason)
            put("newState", newState?.name)
            put("processingTimeMs", processingTimeMs)
            put("effectiveFrequency", getEffectiveFrequency())
            put("allowsImmediateAnalysis", allowsImmediateAnalysis())
            
            val metadataJson = JSONObject()
            metadata.forEach { (key, value) ->
                metadataJson.put(key, value)
            }
            put("metadata", metadataJson)
        }
    }
    
    /**
     * Get a compact string representation for logging
     */
    fun toCompactString(): String {
        val stateChange = if (newState != null) " -> ${newState.name}" else ""
        return "Analyze: $shouldAnalyze (${(analysisFrequency * 100).toInt()}%)$stateChange - $reason"
    }
}

/**
 * DecisionReason - Enumeration of possible decision reasons
 * 
 * Provides standardized reasons for state machine decisions to improve debugging
 * and system understanding.
 */
enum class DecisionReason(val description: String) {
    // Normal operation reasons
    NORMAL_SCANNING("Normal scanning mode - full analysis"),
    SAFE_CONTENT_OPTIMIZATION("Safe content detected - optimized analysis"),
    
    // Similarity-based reasons
    HIGH_SIMILARITY_SKIP("High similarity detected - skipping analysis"),
    LOW_SIMILARITY_ANALYZE("Low similarity detected - forcing analysis"),
    SIMILARITY_THRESHOLD_MET("Similarity threshold met - proceeding with analysis"),
    
    // Motion-based reasons
    MOTION_DETECTED("Significant motion detected - forcing analysis"),
    NO_MOTION_DETECTED("No motion detected - reducing analysis frequency"),
    
    // Harmful content reasons
    HARMFUL_CONTENT_DETECTED("Harmful content detected - entering protective mode"),
    HARMFUL_CONTENT_COOLDOWN("Harmful content cooldown active - reduced analysis"),
    COOLDOWN_EXPIRED("Cooldown period expired - resuming normal analysis"),
    
    // User activity reasons
    USER_SCROLLING("User scrolling detected - adaptive analysis"),
    USER_IDLE("User appears idle - maintaining current analysis"),
    
    // Time-based reasons
    TIME_THRESHOLD_MET("Time threshold met - proceeding with analysis"),
    ANALYSIS_OVERDUE("Analysis overdue - forcing immediate analysis"),
    STATE_TIMEOUT("State timeout reached - transitioning state"),
    
    // Performance reasons
    PERFORMANCE_OPTIMIZATION("Performance optimization - reducing analysis"),
    RESOURCE_CONSTRAINT("Resource constraint detected - limiting analysis"),
    
    // Error and fallback reasons
    INVALID_STATE("Invalid state detected - falling back to scanning"),
    ERROR_RECOVERY("Error recovery - resetting to safe state"),
    CONFIGURATION_CHANGE("Configuration change - updating behavior");
    
    /**
     * Create a detailed reason string with additional context
     */
    fun withContext(context: String): String {
        return "$description ($context)"
    }
}

/**
 * StateHistory - Tracks the history of state transitions and decisions
 * 
 * Maintains a rolling history of state changes and decisions for debugging,
 * optimization, and system analysis.
 */
class StateHistory(private val maxHistorySize: Int = 100) {
    private val transitions = mutableListOf<StateTransition>()
    private val decisions = mutableListOf<Pair<Long, StateDecision>>()
    
    /**
     * Add a state transition to the history
     */
    fun addTransition(transition: StateTransition) {
        synchronized(transitions) {
            transitions.add(transition)
            if (transitions.size > maxHistorySize) {
                transitions.removeAt(0)
            }
        }
    }
    
    /**
     * Add a decision to the history
     */
    fun addDecision(decision: StateDecision) {
        synchronized(decisions) {
            decisions.add(Pair(System.currentTimeMillis(), decision))
            if (decisions.size > maxHistorySize) {
                decisions.removeAt(0)
            }
        }
    }
    
    /**
     * Get recent transitions within the specified time window
     */
    fun getRecentTransitions(timeWindowMs: Long): List<StateTransition> {
        val cutoffTime = System.currentTimeMillis() - timeWindowMs
        return synchronized(transitions) {
            transitions.filter { it.timestamp > cutoffTime }
        }
    }
    
    /**
     * Get recent decisions within the specified time window
     */
    fun getRecentDecisions(timeWindowMs: Long): List<StateDecision> {
        val cutoffTime = System.currentTimeMillis() - timeWindowMs
        return synchronized(decisions) {
            decisions.filter { it.first > cutoffTime }.map { it.second }
        }
    }
    
    /**
     * Get the last N transitions
     */
    fun getLastTransitions(count: Int): List<StateTransition> {
        return synchronized(transitions) {
            transitions.takeLast(count)
        }
    }
    
    /**
     * Get the last N decisions
     */
    fun getLastDecisions(count: Int): List<StateDecision> {
        return synchronized(decisions) {
            decisions.takeLast(count).map { it.second }
        }
    }
    
    /**
     * Get transition statistics
     */
    fun getTransitionStats(): TransitionStats {
        return synchronized(transitions) {
            val stateFrequency = mutableMapOf<DetectionState, Int>()
            val transitionFrequency = mutableMapOf<Pair<DetectionState, DetectionState>, Int>()
            var totalProcessingTime = 0L
            
            transitions.forEach { transition ->
                // Count state frequencies
                stateFrequency[transition.fromState] = stateFrequency.getOrDefault(transition.fromState, 0) + 1
                stateFrequency[transition.toState] = stateFrequency.getOrDefault(transition.toState, 0) + 1
                
                // Count transition frequencies
                val transitionPair = Pair(transition.fromState, transition.toState)
                transitionFrequency[transitionPair] = transitionFrequency.getOrDefault(transitionPair, 0) + 1
                
                totalProcessingTime += transition.processingTimeMs
            }
            
            TransitionStats(
                totalTransitions = transitions.size,
                stateFrequency = stateFrequency,
                transitionFrequency = transitionFrequency,
                averageProcessingTimeMs = if (transitions.isNotEmpty()) totalProcessingTime / transitions.size else 0L,
                oldestTransitionAge = transitions.firstOrNull()?.getAgeMs() ?: 0L
            )
        }
    }
    
    /**
     * Clear all history
     */
    fun clear() {
        synchronized(transitions) { transitions.clear() }
        synchronized(decisions) { decisions.clear() }
    }
    
    /**
     * Get history size information
     */
    fun getHistoryInfo(): HistoryInfo {
        return HistoryInfo(
            transitionCount = synchronized(transitions) { transitions.size },
            decisionCount = synchronized(decisions) { decisions.size },
            maxHistorySize = maxHistorySize,
            oldestTransitionAge = synchronized(transitions) { 
                transitions.firstOrNull()?.getAgeMs() ?: 0L 
            },
            oldestDecisionAge = synchronized(decisions) { 
                decisions.firstOrNull()?.let { System.currentTimeMillis() - it.first } ?: 0L 
            }
        )
    }
}

/**
 * TransitionStats - Statistics about state transitions
 */
data class TransitionStats(
    val totalTransitions: Int,
    val stateFrequency: Map<DetectionState, Int>,
    val transitionFrequency: Map<Pair<DetectionState, DetectionState>, Int>,
    val averageProcessingTimeMs: Long,
    val oldestTransitionAge: Long
) {
    /**
     * Get the most frequent state
     */
    fun getMostFrequentState(): DetectionState? {
        return stateFrequency.maxByOrNull { it.value }?.key
    }
    
    /**
     * Get the most frequent transition
     */
    fun getMostFrequentTransition(): Pair<DetectionState, DetectionState>? {
        return transitionFrequency.maxByOrNull { it.value }?.key
    }
    
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("totalTransitions", totalTransitions)
            put("averageProcessingTimeMs", averageProcessingTimeMs)
            put("oldestTransitionAge", oldestTransitionAge)
            put("mostFrequentState", getMostFrequentState()?.name)
            put("mostFrequentTransition", getMostFrequentTransition()?.let { 
                "${it.first.name} -> ${it.second.name}" 
            })
        }
    }
}

/**
 * HistoryInfo - Information about the state history
 */
data class HistoryInfo(
    val transitionCount: Int,
    val decisionCount: Int,
    val maxHistorySize: Int,
    val oldestTransitionAge: Long,
    val oldestDecisionAge: Long
) {
    /**
     * Get utilization percentage
     */
    fun getUtilization(): Float {
        return maxOf(transitionCount, decisionCount).toFloat() / maxHistorySize
    }
    
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("transitionCount", transitionCount)
            put("decisionCount", decisionCount)
            put("maxHistorySize", maxHistorySize)
            put("utilization", getUtilization())
            put("oldestTransitionAge", oldestTransitionAge)
            put("oldestDecisionAge", oldestDecisionAge)
        }
    }
}