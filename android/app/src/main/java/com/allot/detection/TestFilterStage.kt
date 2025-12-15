package com.allot.detection

import android.util.Log

/**
 * TestFilterStage - Simple test implementation of a filter stage
 * 
 * This stage demonstrates the filter pipeline functionality and can be used
 * for testing and validation of the pipeline infrastructure.
 */
class TestFilterStage(
    name: String = "TestFilter",
    priority: Int = 100,
    private val passRate: Float = 0.8f, // 80% pass rate by default
    private val simulatedProcessingTimeMs: Long = 1L
) : BaseFilterStage(name, priority, targetProcessingTimeMs = 2L) {
    
    companion object {
        const val TAG = "TestFilterStage"
    }
    
    private var executionCount = 0
    
    override fun executeFilter(frame: CapturedFrame, context: FilterContext): FilterResult {
        val (result, processingTime) = measureTime {
            // Validate frame
            if (!validateFrame(frame)) {
                return@measureTime createFailResult(
                    confidence = 0f,
                    processingTimeMs = 0L,
                    reason = "Invalid frame data",
                    shouldTerminate = true
                )
            }
            
            // Simulate processing time
            if (simulatedProcessingTimeMs > 0) {
                Thread.sleep(simulatedProcessingTimeMs)
            }
            
            // Increment execution count
            executionCount++
            
            // Determine if this frame should pass based on pass rate
            val shouldPass = (executionCount % 10) < (passRate * 10).toInt()
            
            val confidence = if (shouldPass) {
                0.8f + (Math.random() * 0.2f).toFloat() // 80-100% confidence
            } else {
                (Math.random() * 0.3f).toFloat() // 0-30% confidence
            }
            
            val reason = if (shouldPass) {
                "Test filter passed (execution #$executionCount)"
            } else {
                "Test filter failed (execution #$executionCount)"
            }
            
            if (shouldPass) {
                createPassResult(
                    confidence = confidence,
                    processingTimeMs = simulatedProcessingTimeMs,
                    reason = reason,
                    metadata = mapOf(
                        "executionCount" to executionCount,
                        "passRate" to passRate,
                        "frameSize" to "${frame.bitmap.width}x${frame.bitmap.height}"
                    )
                )
            } else {
                createFailResult(
                    confidence = confidence,
                    processingTimeMs = simulatedProcessingTimeMs,
                    reason = reason,
                    metadata = mapOf(
                        "executionCount" to executionCount,
                        "passRate" to passRate
                    )
                )
            }
        }
        
        if (isVerboseLoggingEnabled(context)) {
            Log.d(TAG, "Test filter execution #$executionCount: ${result.getSummary()}")
        }
        
        return result
    }
    
    override fun onReset() {
        executionCount = 0
        Log.d(TAG, "Test filter stage reset - execution count reset to 0")
    }
    
    /**
     * Get current execution count for testing
     */
    fun getExecutionCount(): Int = executionCount
    
    /**
     * Get configured pass rate
     */
    fun getPassRate(): Float = passRate
}