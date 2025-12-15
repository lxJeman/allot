package com.allot.detection

import org.json.JSONObject

/**
 * DetectionState - Enum representing the current state of the content detection system
 * 
 * This enum defines all possible states in the Smart Content Detection System's state machine,
 * each with specific behavior patterns and analysis frequency adjustments.
 */
enum class DetectionState(
    val displayName: String,
    val description: String,
    val defaultAnalysisFrequency: Float = 1.0f,
    val defaultConfidenceThreshold: Float = 0.7f
) {
    /**
     * SCANNING - Normal monitoring state
     * Default state where the system actively monitors content at full frequency
     */
    SCANNING(
        displayName = "Scanning",
        description = "Normal monitoring - actively analyzing all frames",
        defaultAnalysisFrequency = 1.0f,
        defaultConfidenceThreshold = 0.7f
    ),
    
    /**
     * HARMFUL_DETECTED - Just found harmful content
     * State immediately after detecting harmful content, preparing for cooldown
     */
    HARMFUL_DETECTED(
        displayName = "Harmful Detected",
        description = "Harmful content just detected - entering protective mode",
        defaultAnalysisFrequency = 0.1f, // Reduced frequency
        defaultConfidenceThreshold = 0.9f // Higher confidence required
    ),
    
    /**
     * SCROLLING_AWAY - User moving past harmful content
     * State when user is actively scrolling away from harmful content
     */
    SCROLLING_AWAY(
        displayName = "Scrolling Away",
        description = "User scrolling past harmful content - reduced analysis",
        defaultAnalysisFrequency = 0.5f, // 50% reduction as per requirements
        defaultConfidenceThreshold = 0.8f
    ),
    
    /**
     * COOLDOWN - Waiting before next analysis
     * State during cooldown period after harmful content detection
     */
    COOLDOWN(
        displayName = "Cooldown",
        description = "Cooldown period - minimal analysis with similarity checks",
        defaultAnalysisFrequency = 0.2f, // Very reduced frequency
        defaultConfidenceThreshold = 0.9f // High confidence required
    ),
    
    /**
     * SAFE_CONTENT - Confirmed safe content area
     * State when content has been confirmed safe for extended period
     */
    SAFE_CONTENT(
        displayName = "Safe Content",
        description = "Safe content confirmed - optimized for performance",
        defaultAnalysisFrequency = 0.8f, // Slightly reduced for optimization
        defaultConfidenceThreshold = 0.6f // Lower threshold for efficiency
    );
    
    /**
     * Check if this state allows normal analysis
     */
    fun allowsNormalAnalysis(): Boolean {
        return this == SCANNING || this == SAFE_CONTENT
    }
    
    /**
     * Check if this state is in a protective mode
     */
    fun isProtectiveMode(): Boolean {
        return this == HARMFUL_DETECTED || this == COOLDOWN
    }
    
    /**
     * Check if this state indicates user activity
     */
    fun indicatesUserActivity(): Boolean {
        return this == SCROLLING_AWAY
    }
    
    /**
     * Get the recommended similarity threshold for this state
     */
    fun getSimilarityThreshold(): Float {
        return when (this) {
            SCANNING -> 0.90f
            HARMFUL_DETECTED -> 0.70f // Lower threshold to detect changes
            SCROLLING_AWAY -> 0.85f
            COOLDOWN -> 0.70f // Lower threshold during cooldown
            SAFE_CONTENT -> 0.95f // Higher threshold for optimization
        }
    }
    
    /**
     * Convert to JSON for logging and debugging
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("displayName", displayName)
            put("description", description)
            put("defaultAnalysisFrequency", defaultAnalysisFrequency)
            put("defaultConfidenceThreshold", defaultConfidenceThreshold)
            put("similarityThreshold", getSimilarityThreshold())
            put("allowsNormalAnalysis", allowsNormalAnalysis())
            put("isProtectiveMode", isProtectiveMode())
            put("indicatesUserActivity", indicatesUserActivity())
        }
    }
}

/**
 * StateTransition - Represents a transition between detection states
 * 
 * Tracks state changes with timestamps and reasons for debugging and optimization.
 */
data class StateTransition(
    val fromState: DetectionState,
    val toState: DetectionState,
    val timestamp: Long,
    val reason: String,
    val triggerData: Map<String, Any> = emptyMap(),
    val processingTimeMs: Long = 0L
) {
    /**
     * Get the age of this transition in milliseconds
     */
    fun getAgeMs(): Long {
        return System.currentTimeMillis() - timestamp
    }
    
    /**
     * Check if this transition is recent (within specified time)
     */
    fun isRecent(thresholdMs: Long): Boolean {
        return getAgeMs() < thresholdMs
    }
    
    /**
     * Convert to JSON for logging and debugging
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("fromState", fromState.name)
            put("toState", toState.name)
            put("timestamp", timestamp)
            put("reason", reason)
            put("ageMs", getAgeMs())
            put("processingTimeMs", processingTimeMs)
            
            val triggerDataJson = JSONObject()
            triggerData.forEach { (key, value) ->
                triggerDataJson.put(key, value)
            }
            put("triggerData", triggerDataJson)
        }
    }
    
    /**
     * Get a compact string representation for logging
     */
    fun toCompactString(): String {
        return "${fromState.name} -> ${toState.name} (${reason}) [${getAgeMs()}ms ago]"
    }
}

/**
 * StateTransitionRule - Defines rules for state transitions
 * 
 * Encapsulates the logic for when and how state transitions should occur.
 */
data class StateTransitionRule(
    val fromState: DetectionState,
    val toState: DetectionState,
    val condition: String,
    val priority: Int = 0,
    val validator: (StateContext) -> Boolean
) {
    /**
     * Check if this rule applies to the given context
     */
    fun applies(context: StateContext): Boolean {
        return context.currentState == fromState && validator(context)
    }
    
    /**
     * Get rule description for debugging
     */
    fun getDescription(): String {
        return "${fromState.name} -> ${toState.name}: $condition (priority: $priority)"
    }
}

/**
 * StateContext - Context information for state machine decisions
 * 
 * Contains all the information needed to make state transition decisions.
 */
data class StateContext(
    val currentState: DetectionState,
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val motionDetected: Boolean,
    val timeSinceLastAnalysis: Long,
    val timeSinceLastHarmful: Long,
    val timeSinceStateChange: Long,
    val isHarmfulContent: Boolean = false,
    val confidenceScore: Float = 0f,
    val additionalData: Map<String, Any> = emptyMap()
) {
    /**
     * Check if enough time has passed since last harmful detection
     */
    fun isCooldownExpired(cooldownMs: Long): Boolean {
        return timeSinceLastHarmful > cooldownMs
    }
    
    /**
     * Check if enough time has passed since last state change
     */
    fun isStateStable(stabilityMs: Long): Boolean {
        return timeSinceStateChange > stabilityMs
    }
    
    /**
     * Check if content appears to be changing (low similarity)
     */
    fun isContentChanging(threshold: Float = 0.7f): Boolean {
        return similarity < threshold
    }
    
    /**
     * Convert to JSON for logging
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("currentState", currentState.name)
            put("similarity", similarity)
            put("motionDetected", motionDetected)
            put("timeSinceLastAnalysis", timeSinceLastAnalysis)
            put("timeSinceLastHarmful", timeSinceLastHarmful)
            put("timeSinceStateChange", timeSinceStateChange)
            put("isHarmfulContent", isHarmfulContent)
            put("confidenceScore", confidenceScore)
            
            val additionalDataJson = JSONObject()
            additionalData.forEach { (key, value) ->
                additionalDataJson.put(key, value)
            }
            put("additionalData", additionalDataJson)
        }
    }
}