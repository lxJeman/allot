package com.allot.detection

import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Data classes and enums for performance monitoring
 */
data class PerformanceSummary(
    val uptimeMs: Long,
    val totalOperations: Long,
    val totalProcessingTimeMs: Long,
    val averageProcessingTimeMs: Float,
    val operationSummaries: List<OperationSummary>,
    val alertCount: Long,
    val memoryUsageMB: Long
)

data class OperationSummary(
    val operationName: String,
    val totalExecutions: Long,
    val successfulExecutions: Long,
    val totalTimeMs: Long,
    val averageTimeMs: Float,
    val minTimeMs: Long,
    val maxTimeMs: Long,
    val successRate: Float
)

data class ExecutionRecord(
    val timestamp: Long,
    val durationMs: Long,
    val success: Boolean,
    val metadata: Map<String, Any>
)

data class MemorySnapshot(
    val timestamp: Long,
    val component: String,
    val memoryUsageBytes: Long,
    val additionalInfo: Map<String, Long>
)

data class PerformanceAlert(
    val timestamp: Long = System.currentTimeMillis(),
    val type: AlertType,
    val message: String,
    val operationName: String? = null,
    val value: Float? = null,
    val threshold: Float? = null
)

enum class AlertType {
    SLOW_OPERATION,
    OPERATION_FAILURE,
    HIGH_MEMORY_USAGE,
    BUFFER_OVERFLOW,
    SYSTEM_OVERLOAD
}

data class AlertThresholds(
    val defaultOperationThresholdMs: Long = 10L,
    val memoryThresholdMB: Long = 50L,
    val operationThresholds: Map<String, Long> = mapOf(
        "hash_generation" to 5L,
        "similarity_search" to 5L,
        "frame_analysis" to 10L
    )
)

/**
 * PerformanceMonitor - Comprehensive performance tracking for Smart Content Detection
 * 
 * Monitors and collects detailed performance metrics for all components of the detection
 * system. Provides real-time monitoring, alerting, and historical analysis capabilities.
 */
class PerformanceMonitor {
    
    // Timing metrics
    private val operationTimings = ConcurrentHashMap<String, OperationMetrics>()
    private val memorySnapshots = mutableListOf<MemorySnapshot>()
    private val performanceAlerts = mutableListOf<PerformanceAlert>()
    
    // Global counters
    private val totalOperations = AtomicLong(0)
    private val totalProcessingTime = AtomicLong(0)
    private val alertCount = AtomicLong(0)
    
    // Configuration
    private var alertThresholds = AlertThresholds()
    private var maxHistorySize = 1000
    
    // Thread safety
    private val lock = ReentrantReadWriteLock()
    
    // Start time for uptime calculation
    private val startTime = SystemClock.elapsedRealtime()
    
    /**
     * Record the start of an operation
     */
    fun startOperation(operationName: String): OperationTimer {
        return OperationTimer(operationName, this)
    }
    
    /**
     * Record completion of an operation
     */
    fun recordOperation(
        operationName: String,
        durationMs: Long,
        success: Boolean = true,
        metadata: Map<String, Any> = emptyMap()
    ) {
        totalOperations.incrementAndGet()
        totalProcessingTime.addAndGet(durationMs)
        
        // Update operation-specific metrics
        val metrics = operationTimings.computeIfAbsent(operationName) {
            OperationMetrics(operationName)
        }
        
        metrics.recordExecution(durationMs, success, metadata)
        
        // Check for performance alerts
        checkPerformanceAlerts(operationName, durationMs, success)
    }
    
    /**
     * Record memory usage snapshot
     */
    fun recordMemoryUsage(
        component: String,
        memoryUsageBytes: Long,
        additionalInfo: Map<String, Long> = emptyMap()
    ) {
        lock.write {
            val snapshot = MemorySnapshot(
                timestamp = System.currentTimeMillis(),
                component = component,
                memoryUsageBytes = memoryUsageBytes,
                additionalInfo = additionalInfo
            )
            
            memorySnapshots.add(snapshot)
            
            // Limit history size
            if (memorySnapshots.size > maxHistorySize) {
                memorySnapshots.removeAt(0)
            }
            
            // Check memory alerts
            checkMemoryAlerts(component, memoryUsageBytes)
        }
    }
    
    /**
     * Get performance summary for all operations
     */
    fun getPerformanceSummary(): PerformanceSummary {
        val uptimeMs = SystemClock.elapsedRealtime() - startTime
        val operations = totalOperations.get()
        val totalTime = totalProcessingTime.get()
        
        val operationSummaries = operationTimings.values.map { metrics ->
            OperationSummary(
                operationName = metrics.operationName,
                totalExecutions = metrics.totalExecutions.get(),
                successfulExecutions = metrics.successfulExecutions.get(),
                totalTimeMs = metrics.totalTimeMs.get(),
                averageTimeMs = metrics.getAverageTimeMs(),
                minTimeMs = metrics.minTimeMs.get(),
                maxTimeMs = metrics.maxTimeMs.get(),
                successRate = metrics.getSuccessRate()
            )
        }.sortedByDescending { it.totalExecutions }
        
        return PerformanceSummary(
            uptimeMs = uptimeMs,
            totalOperations = operations,
            totalProcessingTimeMs = totalTime,
            averageProcessingTimeMs = if (operations > 0) totalTime.toFloat() / operations else 0f,
            operationSummaries = operationSummaries,
            alertCount = alertCount.get(),
            memoryUsageMB = getCurrentMemoryUsage() / (1024 * 1024)
        )
    }
    
    /**
     * Check for performance alerts
     */
    private fun checkPerformanceAlerts(operationName: String, durationMs: Long, success: Boolean) {
        val alerts = mutableListOf<PerformanceAlert>()
        
        // Check duration threshold
        val threshold = alertThresholds.operationThresholds[operationName] 
            ?: alertThresholds.defaultOperationThresholdMs
        
        if (durationMs > threshold) {
            alerts.add(
                PerformanceAlert(
                    type = AlertType.SLOW_OPERATION,
                    message = "Operation '$operationName' took ${durationMs}ms (threshold: ${threshold}ms)",
                    operationName = operationName,
                    value = durationMs.toFloat(),
                    threshold = threshold.toFloat()
                )
            )
        }
        
        // Check failure
        if (!success) {
            alerts.add(
                PerformanceAlert(
                    type = AlertType.OPERATION_FAILURE,
                    message = "Operation '$operationName' failed",
                    operationName = operationName
                )
            )
        }
        
        // Add alerts
        if (alerts.isNotEmpty()) {
            lock.write {
                performanceAlerts.addAll(alerts)
                alertCount.addAndGet(alerts.size.toLong())
                
                // Limit alert history
                if (performanceAlerts.size > maxHistorySize) {
                    val excess = performanceAlerts.size - maxHistorySize
                    repeat(excess) { performanceAlerts.removeAt(0) }
                }
            }
        }
    }
    
    /**
     * Check for memory alerts
     */
    private fun checkMemoryAlerts(component: String, memoryUsageBytes: Long) {
        val memoryUsageMB = memoryUsageBytes / (1024 * 1024)
        
        if (memoryUsageMB > alertThresholds.memoryThresholdMB) {
            val alert = PerformanceAlert(
                type = AlertType.HIGH_MEMORY_USAGE,
                message = "Component '$component' using ${memoryUsageMB}MB (threshold: ${alertThresholds.memoryThresholdMB}MB)",
                operationName = component,
                value = memoryUsageMB.toFloat(),
                threshold = alertThresholds.memoryThresholdMB.toFloat()
            )
            
            lock.write {
                performanceAlerts.add(alert)
                alertCount.incrementAndGet()
            }
        }
    }
    
    /**
     * Get current memory usage estimate
     */
    private fun getCurrentMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }
    
    /**
     * Clear all performance data
     */
    fun clearAllData() {
        lock.write {
            operationTimings.clear()
            memorySnapshots.clear()
            performanceAlerts.clear()
            totalOperations.set(0)
            totalProcessingTime.set(0)
            alertCount.set(0)
        }
    }
}

/**
 * Operation timer for measuring execution time
 */
class OperationTimer(
    private val operationName: String,
    private val monitor: PerformanceMonitor
) {
    private val startTime = System.currentTimeMillis()
    private var metadata = mutableMapOf<String, Any>()
    
    /**
     * Add metadata to the operation
     */
    fun addMetadata(key: String, value: Any): OperationTimer {
        metadata[key] = value
        return this
    }
    
    /**
     * Complete the operation successfully
     */
    fun complete() {
        val duration = System.currentTimeMillis() - startTime
        monitor.recordOperation(operationName, duration, true, metadata)
    }
    
    /**
     * Complete the operation with failure
     */
    fun fail(error: String? = null) {
        val duration = System.currentTimeMillis() - startTime
        if (error != null) {
            metadata["error"] = error
        }
        monitor.recordOperation(operationName, duration, false, metadata)
    }
}

/**
 * Metrics for a specific operation type
 */
class OperationMetrics(val operationName: String) {
    val totalExecutions = AtomicLong(0)
    val successfulExecutions = AtomicLong(0)
    val totalTimeMs = AtomicLong(0)
    val minTimeMs = AtomicLong(Long.MAX_VALUE)
    val maxTimeMs = AtomicLong(0)
    
    private val recentExecutions = mutableListOf<ExecutionRecord>()
    private val lock = ReentrantReadWriteLock()
    
    fun recordExecution(durationMs: Long, success: Boolean, metadata: Map<String, Any>) {
        totalExecutions.incrementAndGet()
        if (success) {
            successfulExecutions.incrementAndGet()
        }
        
        totalTimeMs.addAndGet(durationMs)
        
        // Update min/max atomically
        updateMinTime(durationMs)
        updateMaxTime(durationMs)
        
        // Store recent execution
        lock.write {
            recentExecutions.add(
                ExecutionRecord(
                    timestamp = System.currentTimeMillis(),
                    durationMs = durationMs,
                    success = success,
                    metadata = metadata
                )
            )
            
            // Keep only recent executions (last 100)
            if (recentExecutions.size > 100) {
                recentExecutions.removeAt(0)
            }
        }
    }
    
    fun getAverageTimeMs(): Float {
        val executions = totalExecutions.get()
        return if (executions > 0) totalTimeMs.get().toFloat() / executions else 0f
    }
    
    fun getSuccessRate(): Float {
        val total = totalExecutions.get()
        return if (total > 0) successfulExecutions.get().toFloat() / total else 1f
    }
    
    private fun updateMinTime(durationMs: Long) {
        var currentMin = minTimeMs.get()
        while (durationMs < currentMin) {
            if (minTimeMs.compareAndSet(currentMin, durationMs)) {
                break
            }
            currentMin = minTimeMs.get()
        }
    }
    
    private fun updateMaxTime(durationMs: Long) {
        var currentMax = maxTimeMs.get()
        while (durationMs > currentMax) {
            if (maxTimeMs.compareAndSet(currentMax, durationMs)) {
                break
            }
            currentMax = maxTimeMs.get()
        }
    }
}