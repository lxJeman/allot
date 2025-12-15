package com.allot.detection

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * PreFilterPipeline - Multi-stage filtering system for reducing expensive analysis calls
 * 
 * Implements a configurable pipeline with pluggable filter stages and early termination.
 * Provides comprehensive performance monitoring and decision tracking.
 */
class PreFilterPipeline(
    private val configuration: FilterConfiguration = FilterConfiguration()
) {
    companion object {
        const val TAG = "PreFilterPipeline"
        const val TARGET_TOTAL_PROCESSING_TIME_MS = 10L
    }
    
    // Pipeline stages (sorted by priority)
    private val stages = mutableListOf<FilterStage>()
    
    // Performance monitoring
    private val performanceMonitor = PerformanceMonitor()
    private val pipelineStats = PipelineStats()
    
    // Thread-safe counters
    private val totalExecutions = AtomicLong(0)
    private val totalFramesProcessed = AtomicLong(0)
    private val totalFramesSkipped = AtomicLong(0)
    
    /**
     * Add a filter stage to the pipeline
     */
    fun addFilterStage(stage: FilterStage) {
        synchronized(stages) {
            stages.add(stage)
            stages.sortBy { it.priority }
        }
        Log.d(TAG, "Added filter stage: ${stage.name} (priority: ${stage.priority})")
    }
    
    /**
     * Remove a filter stage from the pipeline
     */
    fun removeFilterStage(stageName: String): Boolean {
        synchronized(stages) {
            return stages.removeAll { it.name == stageName }
        }
    }
    
    /**
     * Process a frame through the filter pipeline
     * 
     * @param frame The frame to process
     * @param context Context for filtering decisions
     * @return FilterDecision indicating whether to proceed with analysis
     */
    fun shouldAnalyze(frame: CapturedFrame, context: FilterContext = FilterContext()): FilterDecision {
        val timer = performanceMonitor.startOperation("pipeline_execution")
        val executionId = totalExecutions.incrementAndGet()
        
        try {
            val stageResults = mutableListOf<FilterResult>()
            var shouldProceed = true
            var terminationReason: String? = null
            var totalProcessingTime = 0L
            
            if (configuration.enableVerboseLogging) {
                Log.d(TAG, "🔍 Pipeline execution #$executionId started with ${stages.size} stages")
            }
            
            // Execute each stage in priority order
            for (stage in stages) {
                val stageTimer = performanceMonitor.startOperation("stage_${stage.name}")
                
                try {
                    val result = stage.filter(frame, context)
                    stageResults.add(result)
                    totalProcessingTime += result.processingTimeMs
                    
                    if (configuration.enableVerboseLogging) {
                        Log.d(TAG, "  📊 Stage ${stage.name}: ${result.getSummary()}")
                    }
                    
                    // Check for early termination
                    if (!result.shouldContinue()) {
                        shouldProceed = false
                        terminationReason = "Stage ${stage.name} failed: ${result.reason}"
                        
                        if (configuration.enableEarlyTermination) {
                            if (configuration.enableVerboseLogging) {
                                Log.d(TAG, "  ⏹️ Early termination: $terminationReason")
                            }
                            break
                        }
                    }
                    
                    stageTimer.complete()
                    
                } catch (e: Exception) {
                    stageTimer.fail(e.message)
                    Log.e(TAG, "Error in stage ${stage.name}: ${e.message}", e)
                    
                    // Add error result
                    stageResults.add(FilterResult(
                        stageName = stage.name,
                        passed = false,
                        confidence = 0f,
                        processingTimeMs = stageTimer.getElapsedTime(),
                        reason = "Stage error: ${e.message}",
                        shouldTerminateEarly = true
                    ))
                    
                    shouldProceed = false
                    terminationReason = "Stage ${stage.name} error: ${e.message}"
                    
                    if (configuration.enableEarlyTermination) {
                        break
                    }
                }
            }
            
            timer.complete()
            
            // Update statistics
            if (shouldProceed) {
                totalFramesProcessed.incrementAndGet()
            } else {
                totalFramesSkipped.incrementAndGet()
            }
            
            // Create decision
            val decision = FilterDecision(
                shouldProceed = shouldProceed,
                reason = terminationReason ?: "All stages passed",
                stageResults = stageResults,
                processingTimeMs = totalProcessingTime,
                executionId = executionId,
                totalStagesExecuted = stageResults.size,
                averageConfidence = if (stageResults.isNotEmpty()) {
                    stageResults.map { it.confidence }.average().toFloat()
                } else 0f
            )
            
            // Performance monitoring
            if (configuration.enablePerformanceMonitoring) {
                pipelineStats.recordExecution(decision)
                
                // Check performance alerts
                if (totalProcessingTime > TARGET_TOTAL_PROCESSING_TIME_MS) {
                    Log.w(TAG, "⚠️ Pipeline execution #$executionId exceeded target time: ${totalProcessingTime}ms > ${TARGET_TOTAL_PROCESSING_TIME_MS}ms")
                }
            }
            
            if (configuration.enableVerboseLogging) {
                Log.d(TAG, "✅ Pipeline execution #$executionId completed: ${decision.getSummary()}")
            }
            
            return decision
            
        } catch (e: Exception) {
            timer.fail(e.message)
            Log.e(TAG, "Pipeline execution error: ${e.message}", e)
            
            return FilterDecision(
                shouldProceed = true, // Fail open for safety
                reason = "Pipeline error: ${e.message}",
                stageResults = emptyList(),
                processingTimeMs = timer.getElapsedTime(),
                executionId = executionId,
                totalStagesExecuted = 0,
                averageConfidence = 0f
            )
        }
    }
    
    /**
     * Get comprehensive pipeline statistics
     */
    fun getFilterStats(): PipelineFilterStats {
        val stageStats = stages.map { it.getPerformanceStats() }
        
        return PipelineFilterStats(
            totalExecutions = totalExecutions.get(),
            totalFramesProcessed = totalFramesProcessed.get(),
            totalFramesSkipped = totalFramesSkipped.get(),
            averageProcessingTimeMs = pipelineStats.getAverageProcessingTime(),
            maxProcessingTimeMs = pipelineStats.getMaxProcessingTime(),
            stageStats = stageStats,
            performanceSummary = performanceMonitor.getPerformanceSummary(),
            pipelineEfficiency = calculatePipelineEfficiency(),
            performanceAlerts = getPerformanceAlerts()
        )
    }
    
    /**
     * Update pipeline configuration
     */
    fun updateConfiguration(newConfig: FilterConfiguration) {
        // Note: In a full implementation, this would update the configuration
        // and notify all stages of the change
        Log.d(TAG, "Configuration updated")
    }
    
    /**
     * Reset all pipeline statistics and stage data
     */
    fun reset() {
        totalExecutions.set(0)
        totalFramesProcessed.set(0)
        totalFramesSkipped.set(0)
        pipelineStats.reset()
        performanceMonitor.clearAllData()
        
        stages.forEach { it.reset() }
        
        Log.d(TAG, "Pipeline reset completed")
    }
    
    /**
     * Get current pipeline configuration
     */
    fun getConfiguration(): FilterConfiguration = configuration
    
    /**
     * Get list of active stages
     */
    fun getActiveStages(): List<String> = stages.map { it.name }
    
    /**
     * Calculate pipeline efficiency metrics
     */
    private fun calculatePipelineEfficiency(): PipelineEfficiency {
        val totalExec = totalExecutions.get()
        val processed = totalFramesProcessed.get()
        val skipped = totalFramesSkipped.get()
        
        return PipelineEfficiency(
            skipRate = if (totalExec > 0) (skipped.toFloat() / totalExec.toFloat()) * 100f else 0f,
            processRate = if (totalExec > 0) (processed.toFloat() / totalExec.toFloat()) * 100f else 0f,
            averageStagesExecuted = pipelineStats.getAverageStagesExecuted(),
            earlyTerminationRate = pipelineStats.getEarlyTerminationRate()
        )
    }
    
    /**
     * Get performance alerts
     */
    private fun getPerformanceAlerts(): List<String> {
        val alerts = mutableListOf<String>()
        
        val avgTime = pipelineStats.getAverageProcessingTime()
        if (avgTime > TARGET_TOTAL_PROCESSING_TIME_MS) {
            alerts.add("Average processing time (${avgTime}ms) exceeds target (${TARGET_TOTAL_PROCESSING_TIME_MS}ms)")
        }
        
        val maxTime = pipelineStats.getMaxProcessingTime()
        if (maxTime > TARGET_TOTAL_PROCESSING_TIME_MS * 2) {
            alerts.add("Maximum processing time (${maxTime}ms) exceeds threshold (${TARGET_TOTAL_PROCESSING_TIME_MS * 2}ms)")
        }
        
        // Check individual stage performance
        stages.forEach { stage ->
            val stageStats = stage.getPerformanceStats()
            if (!stageStats.meetsPerformanceRequirements(stage.targetProcessingTimeMs)) {
                alerts.add("Stage ${stage.name} performance below target")
            }
        }
        
        return alerts
    }
}

/**
 * FilterDecision - Result of pipeline processing
 */
data class FilterDecision(
    val shouldProceed: Boolean,
    val reason: String,
    val stageResults: List<FilterResult>,
    val processingTimeMs: Long,
    val executionId: Long,
    val totalStagesExecuted: Int,
    val averageConfidence: Float
) {
    /**
     * Get a summary of the decision
     */
    fun getSummary(): String {
        return "Decision #$executionId: ${if (shouldProceed) "PROCEED" else "SKIP"} " +
                "(${totalStagesExecuted} stages, ${processingTimeMs}ms, ${(averageConfidence * 100).toInt()}% confidence) - $reason"
    }
    
    /**
     * Check if processing time meets performance requirements
     */
    fun meetsPerformanceRequirements(): Boolean {
        return processingTimeMs <= PreFilterPipeline.TARGET_TOTAL_PROCESSING_TIME_MS
    }
}

/**
 * PipelineFilterStats - Comprehensive pipeline statistics
 */
data class PipelineFilterStats(
    val totalExecutions: Long,
    val totalFramesProcessed: Long,
    val totalFramesSkipped: Long,
    val averageProcessingTimeMs: Float,
    val maxProcessingTimeMs: Long,
    val stageStats: List<FilterStageStats>,
    val performanceSummary: PerformanceSummary,
    val pipelineEfficiency: PipelineEfficiency,
    val performanceAlerts: List<String>
) {
    /**
     * Calculate overall skip rate
     */
    fun getSkipRate(): Float {
        return if (totalExecutions > 0) {
            (totalFramesSkipped.toFloat() / totalExecutions.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * Check if pipeline meets performance requirements
     */
    fun meetsPerformanceRequirements(): Boolean {
        return averageProcessingTimeMs <= PreFilterPipeline.TARGET_TOTAL_PROCESSING_TIME_MS &&
                performanceAlerts.isEmpty()
    }
}

/**
 * PipelineEfficiency - Efficiency metrics for the pipeline
 */
data class PipelineEfficiency(
    val skipRate: Float,
    val processRate: Float,
    val averageStagesExecuted: Float,
    val earlyTerminationRate: Float
)

/**
 * PipelineStats - Internal statistics tracking
 */
private class PipelineStats {
    private val executionHistory = mutableListOf<FilterDecision>()
    private val maxHistorySize = 1000
    
    fun recordExecution(decision: FilterDecision) {
        synchronized(executionHistory) {
            executionHistory.add(decision)
            if (executionHistory.size > maxHistorySize) {
                executionHistory.removeAt(0)
            }
        }
    }
    
    fun getAverageProcessingTime(): Float {
        synchronized(executionHistory) {
            return if (executionHistory.isNotEmpty()) {
                executionHistory.map { it.processingTimeMs }.average().toFloat()
            } else 0f
        }
    }
    
    fun getMaxProcessingTime(): Long {
        synchronized(executionHistory) {
            return executionHistory.maxOfOrNull { it.processingTimeMs } ?: 0L
        }
    }
    
    fun getAverageStagesExecuted(): Float {
        synchronized(executionHistory) {
            return if (executionHistory.isNotEmpty()) {
                executionHistory.map { it.totalStagesExecuted }.average().toFloat()
            } else 0f
        }
    }
    
    fun getEarlyTerminationRate(): Float {
        synchronized(executionHistory) {
            if (executionHistory.isEmpty()) return 0f
            
            val earlyTerminations = executionHistory.count { !it.shouldProceed }
            return (earlyTerminations.toFloat() / executionHistory.size.toFloat()) * 100f
        }
    }
    
    fun reset() {
        synchronized(executionHistory) {
            executionHistory.clear()
        }
    }
}