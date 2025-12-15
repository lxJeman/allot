package com.allot.detection

import android.util.Log
import org.json.JSONObject
import kotlin.math.*

/**
 * AdvancedDecisionEngine - Sophisticated decision-making algorithms for content analysis
 * 
 * Implements context-aware analysis frequency adjustment, confidence threshold modification,
 * and advanced timing logic for the Smart Content Detection System.
 */
class AdvancedDecisionEngine(
    private val config: DecisionEngineConfig = DecisionEngineConfig()
) {
    companion object {
        const val TAG = "AdvancedDecisionEngine"
    }
    
    // Decision history for learning and adaptation
    private val decisionHistory = mutableListOf<DecisionRecord>()
    private val performanceMetrics = DecisionPerformanceMetrics()
    
    /**
     * Make an advanced analysis decision based on comprehensive context
     */
    fun makeAdvancedDecision(
        currentState: DetectionState,
        context: StateContext,
        recentDecisions: List<StateDecision>
    ): AdvancedStateDecision {
        
        val startTime = System.currentTimeMillis()
        
        try {
            // Calculate context-aware analysis frequency
            val analysisFrequency = calculateContextAwareFrequency(currentState, context, recentDecisions)
            
            // Calculate dynamic confidence threshold
            val confidenceThreshold = calculateDynamicConfidenceThreshold(currentState, context)
            
            // Determine if analysis should proceed
            val shouldAnalyze = shouldProceedWithAnalysis(currentState, context, analysisFrequency)
            
            // Calculate priority and urgency
            val priority = calculateAnalysisPriority(currentState, context)
            val urgency = calculateAnalysisUrgency(currentState, context)
            
            // Generate reasoning
            val reasoning = generateDecisionReasoning(currentState, context, shouldAnalyze, analysisFrequency)
            
            // Create advanced decision
            val decision = AdvancedStateDecision(
                shouldAnalyze = shouldAnalyze,
                analysisFrequency = analysisFrequency,
                confidenceThreshold = confidenceThreshold,
                reason = reasoning.primaryReason,
                priority = priority,
                urgency = urgency,
                adaptiveFactors = reasoning.adaptiveFactors,
                contextualModifiers = reasoning.contextualModifiers,
                processingTimeMs = System.currentTimeMillis() - startTime,
                metadata = mapOf(
                    "state" to currentState.name,
                    "similarity" to context.similarity,
                    "motionDetected" to context.motionDetected,
                    "timeSinceLastHarmful" to context.timeSinceLastHarmful,
                    "recentDecisionCount" to recentDecisions.size
                )
            )
            
            // Record decision for learning
            recordDecision(decision, context)
            
            Log.d(TAG, "Advanced decision: ${decision.toCompactString()}")
            return decision
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in advanced decision making: ${e.message}", e)
            
            // Fallback to simple decision
            return AdvancedStateDecision(
                shouldAnalyze = true,
                analysisFrequency = 1.0f,
                confidenceThreshold = 0.7f,
                reason = DecisionReason.ERROR_RECOVERY.description,
                priority = AnalysisPriority.MEDIUM,
                urgency = AnalysisUrgency.NORMAL,
                adaptiveFactors = emptyMap(),
                contextualModifiers = emptyMap(),
                processingTimeMs = System.currentTimeMillis() - startTime
            )
        }
    }
    
    /**
     * Calculate context-aware analysis frequency with 50% reduction in SCROLLING_AWAY
     */
    private fun calculateContextAwareFrequency(
        currentState: DetectionState,
        context: StateContext,
        recentDecisions: List<StateDecision>
    ): Float {
        
        // Base frequency from state
        var frequency = currentState.defaultAnalysisFrequency
        
        // Apply state-specific adjustments
        when (currentState) {
            DetectionState.SCANNING -> {
                // Normal scanning - adjust based on recent activity
                if (context.timeSinceLastHarmful < config.recentHarmfulThresholdMs) {
                    frequency *= config.recentHarmfulFrequencyMultiplier // Increase frequency
                }
                
                // Reduce frequency if many recent similar decisions
                val recentSimilarDecisions = recentDecisions.takeLast(5).count { !it.shouldAnalyze }
                if (recentSimilarDecisions >= 3) {
                    frequency *= config.repetitiveSkipReduction // Reduce frequency
                }
            }
            
            DetectionState.HARMFUL_DETECTED -> {
                // High priority - maintain full frequency but adjust based on content change
                if (context.isContentChanging()) {
                    frequency = 1.0f // Force full analysis
                } else {
                    frequency *= config.harmfulContentStableReduction
                }
            }
            
            DetectionState.SCROLLING_AWAY -> {
                // 50% reduction as per requirements
                frequency *= 0.5f
                
                // Further adjust based on motion velocity
                if (context.motionDetected) {
                    val motionAdjustment = calculateMotionBasedAdjustment(context)
                    frequency *= motionAdjustment
                }
            }
            
            DetectionState.COOLDOWN -> {
                // Minimal frequency during cooldown
                frequency *= config.cooldownFrequencyReduction
                
                // Increase slightly if content is changing significantly
                if (context.similarity < config.significantChangeThreshold) {
                    frequency *= config.significantChangeBoost
                }
            }
            
            DetectionState.SAFE_CONTENT -> {
                // Optimized frequency for safe content
                frequency *= config.safeContentOptimization
                
                // Boost if motion detected
                if (context.motionDetected) {
                    frequency *= config.safeContentMotionBoost
                }
            }
        }
        
        // Apply temporal adjustments
        frequency = applyTemporalAdjustments(frequency, context)
        
        // Apply adaptive learning adjustments
        frequency = applyAdaptiveLearning(frequency, context, recentDecisions)
        
        // Ensure frequency is within bounds
        return frequency.coerceIn(config.minAnalysisFrequency, config.maxAnalysisFrequency)
    }
    
    /**
     * Calculate dynamic confidence threshold based on current state and context
     */
    private fun calculateDynamicConfidenceThreshold(
        currentState: DetectionState,
        context: StateContext
    ): Float {
        
        var threshold = currentState.defaultConfidenceThreshold
        
        // State-specific threshold adjustments
        when (currentState) {
            DetectionState.SCANNING -> {
                // Standard threshold, adjust based on recent history
                if (context.timeSinceLastHarmful < config.recentHarmfulThresholdMs) {
                    threshold += config.recentHarmfulThresholdIncrease
                }
            }
            
            DetectionState.HARMFUL_DETECTED -> {
                // Higher threshold for confirmation
                threshold += config.harmfulDetectedThresholdBoost
            }
            
            DetectionState.SCROLLING_AWAY -> {
                // Moderate threshold during scrolling
                threshold += config.scrollingThresholdAdjustment
            }
            
            DetectionState.COOLDOWN -> {
                // Very high threshold during cooldown
                threshold += config.cooldownThresholdBoost
            }
            
            DetectionState.SAFE_CONTENT -> {
                // Lower threshold for efficiency
                threshold -= config.safeContentThresholdReduction
            }
        }
        
        // Adjust based on confidence score
        if (context.confidenceScore > 0) {
            val confidenceAdjustment = (context.confidenceScore - 0.5f) * config.confidenceBasedAdjustment
            threshold += confidenceAdjustment
        }
        
        // Adjust based on similarity patterns
        if (context.similarity > config.highSimilarityThreshold) {
            threshold += config.highSimilarityThresholdBoost
        }
        
        // Ensure threshold is within bounds
        return threshold.coerceIn(config.minConfidenceThreshold, config.maxConfidenceThreshold)
    }
    
    /**
     * Determine if analysis should proceed based on comprehensive factors
     */
    private fun shouldProceedWithAnalysis(
        currentState: DetectionState,
        context: StateContext,
        analysisFrequency: Float
    ): Boolean {
        
        // Always analyze if frequency is 1.0 (100%)
        if (analysisFrequency >= 1.0f) return true
        
        // Never analyze if frequency is 0
        if (analysisFrequency <= 0f) return false
        
        // Force analysis for certain conditions
        if (shouldForceAnalysis(currentState, context)) return true
        
        // Skip analysis for certain conditions
        if (shouldSkipAnalysis(currentState, context)) return false
        
        // Use probabilistic decision based on frequency
        val random = kotlin.random.Random.nextFloat()
        return random < analysisFrequency
    }
    
    /**
     * Check if analysis should be forced regardless of frequency
     */
    private fun shouldForceAnalysis(currentState: DetectionState, context: StateContext): Boolean {
        return when {
            // Force if content is changing significantly
            context.similarity < config.forceAnalysisThreshold -> true
            
            // Force if motion detected in certain states
            context.motionDetected && (currentState == DetectionState.SAFE_CONTENT || 
                                     currentState == DetectionState.COOLDOWN) -> true
            
            // Force if too much time has passed since last analysis
            context.timeSinceLastAnalysis > config.maxTimeBetweenAnalysisMs -> true
            
            // Force if harmful content was detected recently and content changed
            context.timeSinceLastHarmful < config.recentHarmfulThresholdMs && 
            context.isContentChanging() -> true
            
            else -> false
        }
    }
    
    /**
     * Check if analysis should be skipped regardless of frequency
     */
    private fun shouldSkipAnalysis(currentState: DetectionState, context: StateContext): Boolean {
        return when {
            // Skip if similarity is very high (identical content)
            context.similarity > config.skipAnalysisThreshold -> true
            
            // Skip during cooldown if content hasn't changed
            currentState == DetectionState.COOLDOWN && 
            context.similarity > currentState.getSimilarityThreshold() -> true
            
            // Skip if analysis was very recent and content is similar
            context.timeSinceLastAnalysis < config.minTimeBetweenAnalysisMs &&
            context.similarity > config.recentAnalysisSimilarityThreshold -> true
            
            else -> false
        }
    }
    
    /**
     * Calculate motion-based frequency adjustment
     */
    private fun calculateMotionBasedAdjustment(context: StateContext): Float {
        // This would integrate with motion detection data if available
        // For now, use a simple adjustment based on motion presence
        return if (context.motionDetected) {
            config.motionDetectedFrequencyMultiplier
        } else {
            1.0f
        }
    }
    
    /**
     * Apply temporal adjustments based on timing patterns
     */
    private fun applyTemporalAdjustments(frequency: Float, context: StateContext): Float {
        var adjustedFrequency = frequency
        
        // Reduce frequency if analysis was very recent
        if (context.timeSinceLastAnalysis < config.recentAnalysisThresholdMs) {
            adjustedFrequency *= config.recentAnalysisReduction
        }
        
        // Increase frequency if analysis is overdue
        if (context.timeSinceLastAnalysis > config.overdueAnalysisThresholdMs) {
            adjustedFrequency *= config.overdueAnalysisBoost
        }
        
        return adjustedFrequency
    }
    
    /**
     * Apply adaptive learning based on recent decision patterns
     */
    private fun applyAdaptiveLearning(
        frequency: Float,
        context: StateContext,
        recentDecisions: List<StateDecision>
    ): Float {
        
        if (recentDecisions.isEmpty()) return frequency
        
        // Analyze recent decision patterns
        val recentAnalysisRate = recentDecisions.takeLast(10).count { it.shouldAnalyze } / 10.0f
        val targetAnalysisRate = config.targetAnalysisRate
        
        // Adjust frequency to move towards target rate
        val adjustment = when {
            recentAnalysisRate > targetAnalysisRate -> config.learningReductionFactor
            recentAnalysisRate < targetAnalysisRate -> config.learningBoostFactor
            else -> 1.0f
        }
        
        return frequency * adjustment
    }
    
    /**
     * Calculate analysis priority
     */
    private fun calculateAnalysisPriority(currentState: DetectionState, context: StateContext): AnalysisPriority {
        return when {
            currentState == DetectionState.HARMFUL_DETECTED -> AnalysisPriority.CRITICAL
            context.isHarmfulContent -> AnalysisPriority.HIGH
            context.motionDetected && currentState == DetectionState.SAFE_CONTENT -> AnalysisPriority.HIGH
            context.similarity < config.significantChangeThreshold -> AnalysisPriority.MEDIUM
            currentState == DetectionState.COOLDOWN -> AnalysisPriority.LOW
            else -> AnalysisPriority.MEDIUM
        }
    }
    
    /**
     * Calculate analysis urgency
     */
    private fun calculateAnalysisUrgency(currentState: DetectionState, context: StateContext): AnalysisUrgency {
        return when {
            context.timeSinceLastAnalysis > config.maxTimeBetweenAnalysisMs -> AnalysisUrgency.URGENT
            context.isContentChanging() && context.timeSinceLastHarmful < config.recentHarmfulThresholdMs -> AnalysisUrgency.HIGH
            context.motionDetected -> AnalysisUrgency.NORMAL
            currentState == DetectionState.COOLDOWN -> AnalysisUrgency.LOW
            else -> AnalysisUrgency.NORMAL
        }
    }
    
    /**
     * Generate comprehensive decision reasoning
     */
    private fun generateDecisionReasoning(
        currentState: DetectionState,
        context: StateContext,
        shouldAnalyze: Boolean,
        analysisFrequency: Float
    ): DecisionReasoning {
        
        val adaptiveFactors = mutableMapOf<String, Float>()
        val contextualModifiers = mutableMapOf<String, String>()
        
        // Record adaptive factors
        adaptiveFactors["baseFrequency"] = currentState.defaultAnalysisFrequency
        adaptiveFactors["calculatedFrequency"] = analysisFrequency
        adaptiveFactors["similarity"] = context.similarity
        adaptiveFactors["timeSinceLastHarmful"] = context.timeSinceLastHarmful.toFloat()
        adaptiveFactors["timeSinceLastAnalysis"] = context.timeSinceLastAnalysis.toFloat()
        
        // Record contextual modifiers
        contextualModifiers["state"] = currentState.name
        contextualModifiers["motionDetected"] = context.motionDetected.toString()
        contextualModifiers["contentChanging"] = context.isContentChanging().toString()
        
        // Determine primary reason
        val primaryReason = when {
            !shouldAnalyze && context.similarity > config.skipAnalysisThreshold -> 
                DecisionReason.HIGH_SIMILARITY_SKIP.description
            
            shouldAnalyze && context.similarity < config.forceAnalysisThreshold -> 
                DecisionReason.LOW_SIMILARITY_ANALYZE.description
            
            shouldAnalyze && context.motionDetected -> 
                DecisionReason.MOTION_DETECTED.description
            
            !shouldAnalyze && currentState == DetectionState.COOLDOWN -> 
                DecisionReason.HARMFUL_CONTENT_COOLDOWN.description
            
            shouldAnalyze && currentState == DetectionState.SCANNING -> 
                DecisionReason.NORMAL_SCANNING.description
            
            else -> DecisionReason.SIMILARITY_THRESHOLD_MET.description
        }
        
        return DecisionReasoning(
            primaryReason = primaryReason,
            adaptiveFactors = adaptiveFactors,
            contextualModifiers = contextualModifiers
        )
    }
    
    /**
     * Record decision for learning and analysis
     */
    private fun recordDecision(decision: AdvancedStateDecision, context: StateContext) {
        val record = DecisionRecord(
            timestamp = System.currentTimeMillis(),
            decision = decision,
            context = context,
            outcome = null // Will be updated later if feedback is available
        )
        
        decisionHistory.add(record)
        
        // Limit history size
        if (decisionHistory.size > config.maxDecisionHistorySize) {
            decisionHistory.removeAt(0)
        }
        
        // Update performance metrics
        performanceMetrics.recordDecision(decision)
    }
    
    /**
     * Get decision engine statistics
     */
    fun getDecisionEngineStats(): DecisionEngineStats {
        return DecisionEngineStats(
            totalDecisions = decisionHistory.size,
            averageProcessingTime = decisionHistory.map { it.decision.processingTimeMs }.average().toFloat(),
            analysisRate = performanceMetrics.getAnalysisRate(),
            adaptiveFactorStats = performanceMetrics.getAdaptiveFactorStats(),
            config = config
        )
    }
    
    /**
     * Clear decision history
     */
    fun clearHistory() {
        decisionHistory.clear()
        performanceMetrics.reset()
    }
}

/**
 * Advanced state decision with additional context and reasoning
 */
data class AdvancedStateDecision(
    val shouldAnalyze: Boolean,
    val analysisFrequency: Float,
    val confidenceThreshold: Float,
    val reason: String,
    val priority: AnalysisPriority,
    val urgency: AnalysisUrgency,
    val adaptiveFactors: Map<String, Float>,
    val contextualModifiers: Map<String, String>,
    val processingTimeMs: Long,
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Convert to basic StateDecision for compatibility
     */
    fun toStateDecision(): StateDecision {
        return StateDecision(
            shouldAnalyze = shouldAnalyze,
            analysisFrequency = analysisFrequency,
            confidenceThreshold = confidenceThreshold,
            reason = reason,
            processingTimeMs = processingTimeMs,
            metadata = metadata + mapOf(
                "priority" to priority.name,
                "urgency" to urgency.name
            )
        )
    }
    
    /**
     * Get compact string representation
     */
    fun toCompactString(): String {
        return "Analyze: $shouldAnalyze (${(analysisFrequency * 100).toInt()}%) " +
               "Priority: ${priority.name} Urgency: ${urgency.name} - $reason"
    }
    
    /**
     * Convert to JSON for logging
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("shouldAnalyze", shouldAnalyze)
            put("analysisFrequency", analysisFrequency)
            put("confidenceThreshold", confidenceThreshold)
            put("reason", reason)
            put("priority", priority.name)
            put("urgency", urgency.name)
            put("processingTimeMs", processingTimeMs)
            
            val adaptiveFactorsJson = JSONObject()
            adaptiveFactors.forEach { (key, value) -> adaptiveFactorsJson.put(key, value) }
            put("adaptiveFactors", adaptiveFactorsJson)
            
            val contextualModifiersJson = JSONObject()
            contextualModifiers.forEach { (key, value) -> contextualModifiersJson.put(key, value) }
            put("contextualModifiers", contextualModifiersJson)
        }
    }
}

/**
 * Analysis priority levels
 */
enum class AnalysisPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Analysis urgency levels
 */
enum class AnalysisUrgency {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

/**
 * Decision reasoning with adaptive factors
 */
data class DecisionReasoning(
    val primaryReason: String,
    val adaptiveFactors: Map<String, Float>,
    val contextualModifiers: Map<String, String>
)

/**
 * Decision record for learning and analysis
 */
data class DecisionRecord(
    val timestamp: Long,
    val decision: AdvancedStateDecision,
    val context: StateContext,
    val outcome: DecisionOutcome?
)

/**
 * Decision outcome for feedback learning
 */
data class DecisionOutcome(
    val wasCorrect: Boolean,
    val actualResult: String,
    val feedbackScore: Float
)

/**
 * Configuration for the advanced decision engine
 */
data class DecisionEngineConfig(
    // Frequency adjustment parameters
    val recentHarmfulThresholdMs: Long = 5_000L,
    val recentHarmfulFrequencyMultiplier: Float = 1.5f,
    val repetitiveSkipReduction: Float = 0.8f,
    val harmfulContentStableReduction: Float = 0.3f,
    val cooldownFrequencyReduction: Float = 0.2f,
    val significantChangeBoost: Float = 1.3f,
    val safeContentOptimization: Float = 0.8f,
    val safeContentMotionBoost: Float = 1.2f,
    
    // Confidence threshold parameters
    val recentHarmfulThresholdIncrease: Float = 0.1f,
    val harmfulDetectedThresholdBoost: Float = 0.2f,
    val scrollingThresholdAdjustment: Float = 0.05f,
    val cooldownThresholdBoost: Float = 0.15f,
    val safeContentThresholdReduction: Float = 0.1f,
    val confidenceBasedAdjustment: Float = 0.2f,
    val highSimilarityThresholdBoost: Float = 0.1f,
    
    // Analysis decision thresholds
    val forceAnalysisThreshold: Float = 0.3f,
    val skipAnalysisThreshold: Float = 0.95f,
    val significantChangeThreshold: Float = 0.7f,
    val highSimilarityThreshold: Float = 0.9f,
    val recentAnalysisSimilarityThreshold: Float = 0.85f,
    
    // Timing parameters
    val maxTimeBetweenAnalysisMs: Long = 10_000L,
    val minTimeBetweenAnalysisMs: Long = 500L,
    val recentAnalysisThresholdMs: Long = 2_000L,
    val overdueAnalysisThresholdMs: Long = 8_000L,
    
    // Frequency bounds
    val minAnalysisFrequency: Float = 0.0f,
    val maxAnalysisFrequency: Float = 1.0f,
    
    // Confidence bounds
    val minConfidenceThreshold: Float = 0.3f,
    val maxConfidenceThreshold: Float = 0.95f,
    
    // Motion adjustments
    val motionDetectedFrequencyMultiplier: Float = 1.1f,
    
    // Temporal adjustments
    val recentAnalysisReduction: Float = 0.7f,
    val overdueAnalysisBoost: Float = 1.4f,
    
    // Adaptive learning
    val targetAnalysisRate: Float = 0.6f,
    val learningReductionFactor: Float = 0.9f,
    val learningBoostFactor: Float = 1.1f,
    
    // History management
    val maxDecisionHistorySize: Int = 1000
) {
    /**
     * Convert to JSON for serialization
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("recentHarmfulThresholdMs", recentHarmfulThresholdMs)
            put("recentHarmfulFrequencyMultiplier", recentHarmfulFrequencyMultiplier)
            put("cooldownFrequencyReduction", cooldownFrequencyReduction)
            put("safeContentOptimization", safeContentOptimization)
            put("forceAnalysisThreshold", forceAnalysisThreshold)
            put("skipAnalysisThreshold", skipAnalysisThreshold)
            put("maxTimeBetweenAnalysisMs", maxTimeBetweenAnalysisMs)
            put("minTimeBetweenAnalysisMs", minTimeBetweenAnalysisMs)
            put("targetAnalysisRate", targetAnalysisRate)
        }
    }
}

/**
 * Performance metrics for decision engine
 */
class DecisionPerformanceMetrics {
    private var totalDecisions = 0
    private var analysisDecisions = 0
    private val adaptiveFactorSums = mutableMapOf<String, Float>()
    private val adaptiveFactorCounts = mutableMapOf<String, Int>()
    
    fun recordDecision(decision: AdvancedStateDecision) {
        totalDecisions++
        if (decision.shouldAnalyze) {
            analysisDecisions++
        }
        
        // Record adaptive factors
        decision.adaptiveFactors.forEach { (key, value) ->
            adaptiveFactorSums[key] = adaptiveFactorSums.getOrDefault(key, 0f) + value
            adaptiveFactorCounts[key] = adaptiveFactorCounts.getOrDefault(key, 0) + 1
        }
    }
    
    fun getAnalysisRate(): Float {
        return if (totalDecisions > 0) analysisDecisions.toFloat() / totalDecisions else 0f
    }
    
    fun getAdaptiveFactorStats(): Map<String, Float> {
        return adaptiveFactorSums.mapValues { (key, sum) ->
            val count = adaptiveFactorCounts[key] ?: 1
            sum / count
        }
    }
    
    fun reset() {
        totalDecisions = 0
        analysisDecisions = 0
        adaptiveFactorSums.clear()
        adaptiveFactorCounts.clear()
    }
}

/**
 * Statistics for decision engine performance
 */
data class DecisionEngineStats(
    val totalDecisions: Int,
    val averageProcessingTime: Float,
    val analysisRate: Float,
    val adaptiveFactorStats: Map<String, Float>,
    val config: DecisionEngineConfig
) {
    /**
     * Convert to JSON for reporting
     */
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("totalDecisions", totalDecisions)
            put("averageProcessingTime", averageProcessingTime)
            put("analysisRate", analysisRate)
            
            val adaptiveStatsJson = JSONObject()
            adaptiveFactorStats.forEach { (key, value) -> adaptiveStatsJson.put(key, value) }
            put("adaptiveFactorStats", adaptiveStatsJson)
            
            put("config", config.toJson())
        }
    }
}