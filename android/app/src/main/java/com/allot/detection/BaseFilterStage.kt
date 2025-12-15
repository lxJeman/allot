package com.allot.detection

import android.util.Log
import java.util.concurrent.atomic.AtomicLong

/**
 * BaseFilterStage - Abstract base class for filter stages
 * 
 * Provides common functionality for performance monitoring, statistics tracking,
 * and error handling that all filter stages can use.
 */
abstract class BaseFilterStage(
    override val name: String,
    override val priority: Int,
    override val targetProcessingTimeMs: Long = 2L
) : FilterStage {
    
    companion object {
        const val TAG = "BaseFilterStage"
    }
    
    // Performance tracking
    private val performanceMonitor = PerformanceMonitor()
    private val totalExecutions = AtomicLong(0)
    private val totalPassed = AtomicLong(0)
    private val totalFailed = AtomicLong(0)
    private val confidenceSum = AtomicLong(0) // Store as long (confidence * 1000)
    private var maxProcessingTime = 0L
    
    /**
     * Template method that handles performance monitoring and error handling
     */
    final override fun filter(frame: CapturedFrame, context: FilterContext): FilterResult {
        val timer = performanceMonitor.startOperation("filter_execution")
        val executionCount = totalExecutions.incrementAndGet()
        
        try {
            if (isVerboseLoggingEnabled(context)) {
                Log.d(TAG, "🔍 Executing filter stage: $name (#$executionCount)")
            }
            
            // Execute the actual filtering logic
            val result = executeFilter(frame, context)
            
            // Update statistics
            if (result.passed) {
                totalPassed.incrementAndGet()
            } else {
                totalFailed.incrementAndGet()
            }
            
            // Update confidence sum (store as long for thread safety)
            confidenceSum.addAndGet((result.confidence * 1000).toLong())
            
            // Update max processing time
            synchronized(this) {
                if (result.processingTimeMs > maxProcessingTime) {
                    maxProcessingTime = result.processingTimeMs
                }
            }
            
            timer.complete()
            
            if (isVerboseLoggingEnabled(context)) {
                Log.d(TAG, "  ✅ Stage $name result: ${result.getSummary()}")
            }
            
            // Performance warning
            if (result.processingTimeMs > targetProcessingTimeMs) {
                Log.w(TAG, "⚠️ Stage $name exceeded target time: ${result.processingTimeMs}ms > ${targetProcessingTimeMs}ms")
            }
            
            return result
            
        } catch (e: Exception) {
            timer.fail(e.message)
            totalFailed.incrementAndGet()
            
            Log.e(TAG, "Error in filter stage $name: ${e.message}", e)
            
            return FilterResult(
                stageName = name,
                passed = false,
                confidence = 0f,
                processingTimeMs = timer.getElapsedTime(),
                reason = "Stage error: ${e.message}",
                shouldTerminateEarly = true
            )
        }
    }
    
    /**
     * Abstract method for subclasses to implement their filtering logic
     */
    protected abstract fun executeFilter(frame: CapturedFrame, context: FilterContext): FilterResult
    
    /**
     * Get performance statistics for this stage
     */
    override fun getPerformanceStats(): FilterStageStats {
        val executions = totalExecutions.get()
        val passed = totalPassed.get()
        val failed = totalFailed.get()
        val confSum = confidenceSum.get()
        
        val averageConfidence = if (executions > 0) {
            (confSum.toFloat() / 1000f) / executions.toFloat()
        } else 0f
        
        val performanceSummary = performanceMonitor.getPerformanceSummary()
        val alerts = mutableListOf<String>()
        
        // Generate performance alerts
        if (performanceSummary.averageProcessingTimeMs > targetProcessingTimeMs) {
            alerts.add("Average processing time exceeds target")
        }
        
        if (maxProcessingTime > targetProcessingTimeMs * 2) {
            alerts.add("Maximum processing time exceeds threshold")
        }
        
        if (executions > 100 && (failed.toFloat() / executions.toFloat()) > 0.1f) {
            alerts.add("High failure rate detected")
        }
        
        return FilterStageStats(
            stageName = name,
            totalExecutions = executions,
            totalPassedFrames = passed,
            totalFailedFrames = failed,
            averageProcessingTimeMs = performanceSummary.averageProcessingTimeMs,
            maxProcessingTimeMs = maxProcessingTime,
            averageConfidence = averageConfidence,
            lastExecutionTimeMs = System.currentTimeMillis(),
            performanceAlerts = alerts
        )
    }
    
    /**
     * Reset all statistics and performance data
     */
    override fun reset() {
        totalExecutions.set(0)
        totalPassed.set(0)
        totalFailed.set(0)
        confidenceSum.set(0)
        maxProcessingTime = 0L
        performanceMonitor.clearAllData()
        
        // Allow subclasses to reset their own state
        onReset()
        
        Log.d(TAG, "Filter stage $name reset completed")
    }
    
    /**
     * Hook for subclasses to perform additional reset operations
     */
    protected open fun onReset() {
        // Default implementation does nothing
    }
    
    /**
     * Helper method to create a successful filter result
     */
    protected fun createPassResult(
        confidence: Float,
        processingTimeMs: Long,
        reason: String,
        metadata: Map<String, Any> = emptyMap()
    ): FilterResult {
        return FilterResult(
            stageName = name,
            passed = true,
            confidence = confidence,
            processingTimeMs = processingTimeMs,
            reason = reason,
            metadata = metadata
        )
    }
    
    /**
     * Helper method to create a failed filter result
     */
    protected fun createFailResult(
        confidence: Float,
        processingTimeMs: Long,
        reason: String,
        shouldTerminate: Boolean = false,
        metadata: Map<String, Any> = emptyMap()
    ): FilterResult {
        return FilterResult(
            stageName = name,
            passed = false,
            confidence = confidence,
            processingTimeMs = processingTimeMs,
            reason = reason,
            metadata = metadata,
            shouldTerminateEarly = shouldTerminate
        )
    }
    
    /**
     * Helper method to check if verbose logging is enabled
     */
    protected fun isVerboseLoggingEnabled(context: FilterContext): Boolean {
        return context.configuration.enableVerboseLogging
    }
    
    /**
     * Helper method to get stage-specific configuration
     */
    protected fun getStageConfig(context: FilterContext): Map<String, Any> {
        return context.configuration.getStageConfig(name)
    }
    
    /**
     * Helper method to measure execution time of a block
     */
    protected inline fun <T> measureTime(block: () -> T): Pair<T, Long> {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        return Pair(result, endTime - startTime)
    }
    
    /**
     * Helper method to validate frame data
     */
    protected fun validateFrame(frame: CapturedFrame): Boolean {
        return !frame.bitmap.isRecycled && 
               frame.bitmap.width > 0 && 
               frame.bitmap.height > 0 &&
               frame.timestamp > 0
    }
    
    /**
     * Helper method to log performance metrics
     */
    protected fun logPerformanceMetrics(processingTime: Long, confidence: Float, reason: String) {
        if (processingTime > targetProcessingTimeMs) {
            Log.w(TAG, "Stage $name performance warning: ${processingTime}ms (target: ${targetProcessingTimeMs}ms) - $reason")
        } else {
            Log.d(TAG, "Stage $name: ${processingTime}ms, ${(confidence * 100).toInt()}% confidence - $reason")
        }
    }
}