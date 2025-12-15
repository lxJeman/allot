package com.allot.detection

import android.util.Log
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * ContentStateMachine - Core state machine for intelligent content detection
 * 
 * Implements the finite state machine that tracks detection state and adapts behavior
 * based on content analysis, user activity, and temporal patterns.
 */
class ContentStateMachine(
    private val config: StateMachineConfig = StateMachineConfig()
) {
    companion object {
        const val TAG = "ContentStateMachine"
    }
    
    // Current state and timing
    @Volatile
    private var currentState: DetectionState = DetectionState.SCANNING
    private var lastStateChangeTime: Long = System.currentTimeMillis()
    private var lastHarmfulDetectionTime: Long = 0L
    private var lastAnalysisTime: Long = 0L
    
    // State history and tracking
    private val stateHistory = StateHistory(config.maxHistorySize)
    private val transitionRules = mutableListOf<StateTransitionRule>()
    
    // Advanced decision making
    private val advancedDecisionEngine = AdvancedDecisionEngine()
    
    // Cooldown and temporal awareness
    private val cooldownManager = CooldownManager()
    private val temporalAwarenessEngine = TemporalAwarenessEngine()
    
    // Performance monitoring
    private val performanceMonitor = PerformanceMonitor()
    
    init {
        initializeTransitionRules()
        Log.d(TAG, "ContentStateMachine initialized with state: ${currentState.name}")
    }
    
    /**
     * Process a frame and make a decision about analysis
     */
    fun processFrame(context: FrameContext): StateDecision {
        val timer = performanceMonitor.startOperation("process_frame")
        
        try {
            // Create state context
            val stateContext = createStateContext(context)
            
            // Update cooldown manager with current frame
            if (context.isHarmfulContent) {
                cooldownManager.startCooldown(context.fingerprint, "Harmful content detected")
            } else {
                cooldownManager.updateSafeContentWindow(context.fingerprint, true)
            }
            
            // Check for cooldown extension
            if (cooldownManager.isInCooldown()) {
                cooldownManager.shouldExtendCooldown(context.fingerprint)
            }
            
            // Check for state transitions
            val newState = evaluateStateTransitions(stateContext)
            
            // Apply state transition if needed
            if (newState != null && newState != currentState) {
                transitionToState(newState, "Frame processing triggered transition", stateContext)
                
                // Handle cooldown state changes
                if (newState == DetectionState.HARMFUL_DETECTED) {
                    cooldownManager.startCooldown(context.fingerprint, "State transition to HARMFUL_DETECTED")
                } else if (currentState == DetectionState.COOLDOWN && newState == DetectionState.SCANNING) {
                    cooldownManager.clearCooldown("Cooldown period expired")
                }
            }
            
            // Get recent decisions for context
            val recentDecisions = stateHistory.getLastDecisions(10)
            
            // Get cooldown status for temporal awareness
            val cooldownStatus = cooldownManager.getCooldownStatus()
            
            // Process with temporal awareness
            val temporalResult = temporalAwarenessEngine.processFrameWithTemporalAwareness(
                context = stateContext,
                cooldownStatus = cooldownStatus,
                recentDecisions = recentDecisions
            )
            
            // Make advanced analysis decision with temporal awareness
            val advancedDecision = advancedDecisionEngine.makeAdvancedDecision(
                currentState = currentState,
                context = stateContext,
                recentDecisions = recentDecisions
            )
            
            // Apply temporal adjustments to the decision
            val adjustedDecision = applyTemporalAdjustments(advancedDecision, temporalResult)
            
            // Convert to basic decision for compatibility
            val decision = adjustedDecision.toStateDecision()
            
            // Update timing
            if (decision.shouldAnalyze) {
                lastAnalysisTime = System.currentTimeMillis()
            }
            
            // Record decision in history
            stateHistory.addDecision(decision)
            
            timer.complete()
            
            Log.d(TAG, "Frame processed: ${decision.toCompactString()}")
            return decision
            
        } catch (e: Exception) {
            timer.fail(e.message)
            Log.e(TAG, "Error processing frame: ${e.message}", e)
            
            // Return safe fallback decision
            return StateDecision(
                shouldAnalyze = true,
                analysisFrequency = 1.0f,
                confidenceThreshold = 0.7f,
                reason = DecisionReason.ERROR_RECOVERY.withContext(e.message ?: "Unknown error"),
                processingTimeMs = 0L
            )
        }
    }
    
    /**
     * Force a state transition with reason
     */
    fun forceStateTransition(newState: DetectionState, reason: String) {
        val context = StateContext(
            currentState = currentState,
            fingerprint = FrameFingerprint(0L, System.currentTimeMillis(), 0, 0),
            similarity = 0f,
            motionDetected = false,
            timeSinceLastAnalysis = System.currentTimeMillis() - lastAnalysisTime,
            timeSinceLastHarmful = System.currentTimeMillis() - lastHarmfulDetectionTime,
            timeSinceStateChange = System.currentTimeMillis() - lastStateChangeTime
        )
        
        transitionToState(newState, "Forced transition: $reason", context)
    }
    
    /**
     * Get current state
     */
    fun getCurrentState(): DetectionState {
        return currentState
    }
    
    /**
     * Get state history
     */
    fun getStateHistory(): List<StateTransition> {
        return stateHistory.getLastTransitions(20) // Return last 20 transitions
    }
    
    /**
     * Get state machine statistics
     */
    fun getStateMachineStats(): StateMachineStats {
        val historyInfo = stateHistory.getHistoryInfo()
        val transitionStats = stateHistory.getTransitionStats()
        val performanceSummary = performanceMonitor.getPerformanceSummary()
        
        return StateMachineStats(
            currentState = currentState,
            timeSinceLastStateChange = System.currentTimeMillis() - lastStateChangeTime,
            timeSinceLastHarmful = System.currentTimeMillis() - lastHarmfulDetectionTime,
            timeSinceLastAnalysis = System.currentTimeMillis() - lastAnalysisTime,
            totalTransitions = transitionStats.totalTransitions,
            averageProcessingTimeMs = performanceSummary.averageProcessingTimeMs,
            historyUtilization = historyInfo.getUtilization(),
            mostFrequentState = transitionStats.getMostFrequentState(),
            config = config
        )
    }
    
    /**
     * Get advanced decision engine statistics
     */
    fun getAdvancedDecisionStats(): DecisionEngineStats {
        return advancedDecisionEngine.getDecisionEngineStats()
    }
    
    /**
     * Get cooldown status
     */
    fun getCooldownStatus(): CooldownStatus {
        return cooldownManager.getCooldownStatus()
    }
    
    /**
     * Get cooldown manager statistics
     */
    fun getCooldownManagerStats(): CooldownManagerStats {
        return cooldownManager.getCooldownManagerStats()
    }
    
    /**
     * Get temporal awareness statistics
     */
    fun getTemporalAwarenessStats(): TemporalAwarenessStats {
        return temporalAwarenessEngine.getTemporalAwarenessStats()
    }
    
    /**
     * Get state-specific temporal statistics
     */
    fun getStateTemporalStats(state: DetectionState): StateTemporalStats? {
        return temporalAwarenessEngine.getStateTemporalStats(state)
    }
    
    /**
     * Update configuration
     */
    fun updateConfiguration(newConfig: StateMachineConfig) {
        // Update internal config (this would be implemented based on specific needs)
        Log.d(TAG, "Configuration updated")
    }
    
    /**
     * Clear all history and reset to initial state
     */
    fun reset() {
        currentState = DetectionState.SCANNING
        lastStateChangeTime = System.currentTimeMillis()
        lastHarmfulDetectionTime = 0L
        lastAnalysisTime = 0L
        stateHistory.clear()
        performanceMonitor.clearAllData()
        advancedDecisionEngine.clearHistory()
        cooldownManager.clearAll()
        temporalAwarenessEngine.clearAll()
        
        Log.d(TAG, "State machine reset to initial state")
    }
    
    // Private helper methods
    
    /**
     * Apply temporal adjustments to advanced decision
     */
    private fun applyTemporalAdjustments(
        advancedDecision: AdvancedStateDecision,
        temporalResult: TemporalAnalysisResult
    ): AdvancedStateDecision {
        
        // If temporal analysis suggests skipping, override the decision
        if (temporalResult.shouldSkipAnalysis) {
            return advancedDecision.copy(
                shouldAnalyze = false,
                analysisFrequency = 0f,
                reason = "Temporal analysis: ${temporalResult.skipReason}",
                metadata = advancedDecision.metadata + mapOf(
                    "temporalOverride" to true,
                    "originalReason" to advancedDecision.reason
                )
            )
        }
        
        // Apply temporal frequency adjustment
        val adjustedFrequency = (advancedDecision.analysisFrequency * temporalResult.temporalFrequencyAdjustment)
            .coerceIn(0f, 1f)
        
        // Apply temporal similarity threshold adjustment
        val adjustedThreshold = (advancedDecision.confidenceThreshold + temporalResult.temporalSimilarityAdjustment)
            .coerceIn(0.1f, 0.95f)
        
        return advancedDecision.copy(
            analysisFrequency = adjustedFrequency,
            confidenceThreshold = adjustedThreshold,
            reason = "${advancedDecision.reason} (temporal adjusted)",
            metadata = advancedDecision.metadata + mapOf(
                "temporalFrequencyAdjustment" to temporalResult.temporalFrequencyAdjustment,
                "temporalSimilarityAdjustment" to temporalResult.temporalSimilarityAdjustment,
                "patternAnalysis" to temporalResult.patternAnalysis.patternId
            )
        )
    }
    
    // Private helper methods
    
    private fun createStateContext(context: FrameContext): StateContext {
        return StateContext(
            currentState = currentState,
            fingerprint = context.fingerprint,
            similarity = context.similarity,
            motionDetected = context.motionDetected,
            timeSinceLastAnalysis = System.currentTimeMillis() - lastAnalysisTime,
            timeSinceLastHarmful = System.currentTimeMillis() - lastHarmfulDetectionTime,
            timeSinceStateChange = System.currentTimeMillis() - lastStateChangeTime,
            isHarmfulContent = context.isHarmfulContent,
            confidenceScore = context.confidenceScore,
            additionalData = context.additionalData
        )
    }
    
    private fun evaluateStateTransitions(context: StateContext): DetectionState? {
        // Check each transition rule in priority order
        for (rule in transitionRules.sortedByDescending { it.priority }) {
            if (rule.applies(context)) {
                Log.d(TAG, "Transition rule matched: ${rule.getDescription()}")
                return rule.toState
            }
        }
        return null
    }
    
    private fun transitionToState(newState: DetectionState, reason: String, context: StateContext) {
        val timer = performanceMonitor.startOperation("state_transition")
        
        val transition = StateTransition(
            fromState = currentState,
            toState = newState,
            timestamp = System.currentTimeMillis(),
            reason = reason,
            triggerData = mapOf(
                "similarity" to context.similarity,
                "motionDetected" to context.motionDetected,
                "timeSinceLastHarmful" to context.timeSinceLastHarmful
            ),
            processingTimeMs = 0L // Will be updated when timer completes
        )
        
        // Update state
        val previousState = currentState
        currentState = newState
        lastStateChangeTime = System.currentTimeMillis()
        
        // Update harmful detection time if transitioning to HARMFUL_DETECTED
        if (newState == DetectionState.HARMFUL_DETECTED) {
            lastHarmfulDetectionTime = System.currentTimeMillis()
        }
        
        timer.complete()
        
        // Update transition with actual processing time
        val processingTime = System.currentTimeMillis() - lastStateChangeTime
        val completedTransition = transition.copy(processingTimeMs = processingTime)
        stateHistory.addTransition(completedTransition)
        
        Log.d(TAG, "State transition: ${completedTransition.toCompactString()}")
    }
    
    private fun makeAnalysisDecision(context: StateContext): StateDecision {
        val startTime = System.currentTimeMillis()
        
        val decision = when (currentState) {
            DetectionState.SCANNING -> {
                if (context.similarity > currentState.getSimilarityThreshold()) {
                    StateDecision(
                        shouldAnalyze = false,
                        analysisFrequency = 0f,
                        confidenceThreshold = currentState.defaultConfidenceThreshold,
                        reason = DecisionReason.HIGH_SIMILARITY_SKIP.description,
                        processingTimeMs = System.currentTimeMillis() - startTime
                    )
                } else {
                    StateDecision(
                        shouldAnalyze = true,
                        analysisFrequency = currentState.defaultAnalysisFrequency,
                        confidenceThreshold = currentState.defaultConfidenceThreshold,
                        reason = DecisionReason.NORMAL_SCANNING.description,
                        processingTimeMs = System.currentTimeMillis() - startTime
                    )
                }
            }
            
            DetectionState.HARMFUL_DETECTED -> {
                StateDecision(
                    shouldAnalyze = context.isContentChanging(),
                    analysisFrequency = currentState.defaultAnalysisFrequency,
                    confidenceThreshold = currentState.defaultConfidenceThreshold,
                    reason = if (context.isContentChanging()) {
                        DecisionReason.LOW_SIMILARITY_ANALYZE.description
                    } else {
                        DecisionReason.HARMFUL_CONTENT_COOLDOWN.description
                    },
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }
            
            DetectionState.SCROLLING_AWAY -> {
                StateDecision(
                    shouldAnalyze = true,
                    analysisFrequency = currentState.defaultAnalysisFrequency, // 50% reduction
                    confidenceThreshold = currentState.defaultConfidenceThreshold,
                    reason = DecisionReason.USER_SCROLLING.description,
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }
            
            DetectionState.COOLDOWN -> {
                val shouldAnalyze = context.similarity < currentState.getSimilarityThreshold()
                StateDecision(
                    shouldAnalyze = shouldAnalyze,
                    analysisFrequency = if (shouldAnalyze) currentState.defaultAnalysisFrequency else 0f,
                    confidenceThreshold = currentState.defaultConfidenceThreshold,
                    reason = if (shouldAnalyze) {
                        DecisionReason.LOW_SIMILARITY_ANALYZE.description
                    } else {
                        DecisionReason.HARMFUL_CONTENT_COOLDOWN.description
                    },
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }
            
            DetectionState.SAFE_CONTENT -> {
                StateDecision(
                    shouldAnalyze = !context.motionDetected || context.similarity < currentState.getSimilarityThreshold(),
                    analysisFrequency = currentState.defaultAnalysisFrequency,
                    confidenceThreshold = currentState.defaultConfidenceThreshold,
                    reason = DecisionReason.SAFE_CONTENT_OPTIMIZATION.description,
                    processingTimeMs = System.currentTimeMillis() - startTime
                )
            }
        }
        
        // Record performance
        performanceMonitor.recordOperation("analysis_decision", decision.processingTimeMs, true)
        
        return decision
    }
    
    private fun initializeTransitionRules() {
        // Rule 1: Harmful content detected -> HARMFUL_DETECTED
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.SCANNING,
            toState = DetectionState.HARMFUL_DETECTED,
            condition = "Harmful content detected",
            priority = 100
        ) { context -> context.isHarmfulContent })
        
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.SAFE_CONTENT,
            toState = DetectionState.HARMFUL_DETECTED,
            condition = "Harmful content detected in safe area",
            priority = 100
        ) { context -> context.isHarmfulContent })
        
        // Rule 2: Motion detected after harmful content -> SCROLLING_AWAY
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.HARMFUL_DETECTED,
            toState = DetectionState.SCROLLING_AWAY,
            condition = "Motion detected after harmful content",
            priority = 90
        ) { context -> context.motionDetected && context.isContentChanging() })
        
        // Rule 3: Scrolling continues -> COOLDOWN
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.SCROLLING_AWAY,
            toState = DetectionState.COOLDOWN,
            condition = "Scrolling stabilized, entering cooldown",
            priority = 80
        ) { context -> !context.motionDetected && context.timeSinceStateChange > 1000L })
        
        // Rule 4: Cooldown expired -> SCANNING
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.COOLDOWN,
            toState = DetectionState.SCANNING,
            condition = "Cooldown period expired",
            priority = 70
        ) { context -> context.isCooldownExpired(config.cooldownPeriodMs) })
        
        // Rule 5: Extended safe content -> SAFE_CONTENT
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.SCANNING,
            toState = DetectionState.SAFE_CONTENT,
            condition = "Extended safe content period",
            priority = 60
        ) { context -> 
            context.timeSinceLastHarmful > config.safeContentThresholdMs && 
            context.timeSinceStateChange > config.safeContentThresholdMs
        })
        
        // Rule 6: Motion in safe content -> SCANNING
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.SAFE_CONTENT,
            toState = DetectionState.SCANNING,
            condition = "Motion detected in safe content",
            priority = 50
        ) { context -> context.motionDetected && context.isContentChanging() })
        
        // Rule 7: Extended cooldown -> Extended cooldown
        transitionRules.add(StateTransitionRule(
            fromState = DetectionState.COOLDOWN,
            toState = DetectionState.COOLDOWN,
            condition = "Extending cooldown for persistent similar content",
            priority = 40
        ) { context -> 
            context.similarity > 0.8f && 
            context.timeSinceStateChange > config.cooldownPeriodMs &&
            context.timeSinceStateChange < (config.cooldownPeriodMs + config.extendedCooldownMs)
        })
        
        Log.d(TAG, "Initialized ${transitionRules.size} state transition rules")
    }
}

/**
 * FrameContext - Context information for frame processing
 * 
 * Contains the information needed by the state machine to make decisions about a frame.
 */
data class FrameContext(
    val fingerprint: FrameFingerprint,
    val similarity: Float,
    val motionDetected: Boolean,
    val isHarmfulContent: Boolean = false,
    val confidenceScore: Float = 0f,
    val additionalData: Map<String, Any> = emptyMap()
)

/**
 * StateMachineConfig - Configuration for the state machine
 */
data class StateMachineConfig(
    val cooldownPeriodMs: Long = 3_000L, // 3 seconds as per requirements
    val extendedCooldownMs: Long = 2_000L, // Additional 2 seconds as per requirements
    val safeContentThresholdMs: Long = 10_000L, // 10 seconds as per requirements
    val maxHistorySize: Int = 100,
    val enableVerboseLogging: Boolean = false
) {
    /**
     * Convert to JSON for serialization
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("cooldownPeriodMs", cooldownPeriodMs)
            put("extendedCooldownMs", extendedCooldownMs)
            put("safeContentThresholdMs", safeContentThresholdMs)
            put("maxHistorySize", maxHistorySize)
            put("enableVerboseLogging", enableVerboseLogging)
        }
    }
}

/**
 * StateMachineStats - Statistics about state machine performance
 */
data class StateMachineStats(
    val currentState: DetectionState,
    val timeSinceLastStateChange: Long,
    val timeSinceLastHarmful: Long,
    val timeSinceLastAnalysis: Long,
    val totalTransitions: Int,
    val averageProcessingTimeMs: Float,
    val historyUtilization: Float,
    val mostFrequentState: DetectionState?,
    val config: StateMachineConfig
) {
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("currentState", currentState.name)
            put("timeSinceLastStateChange", timeSinceLastStateChange)
            put("timeSinceLastHarmful", timeSinceLastHarmful)
            put("timeSinceLastAnalysis", timeSinceLastAnalysis)
            put("totalTransitions", totalTransitions)
            put("averageProcessingTimeMs", averageProcessingTimeMs)
            put("historyUtilization", historyUtilization)
            put("mostFrequentState", mostFrequentState?.name)
            put("config", config.toJson())
        }
    }
}