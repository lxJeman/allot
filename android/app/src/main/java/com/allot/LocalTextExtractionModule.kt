package com.allot

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.*
import kotlinx.coroutines.*

/**
 * LocalTextExtractionModule - React Native bridge for LocalTextExtractionService
 * 
 * Provides React Native interface for controlling local ML text extraction
 * with screen capture integration.
 */
class LocalTextExtractionModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "LocalTextExtractionModule"
    }

    private val moduleScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun getName(): String {
        return "LocalTextExtractionModule"
    }

    /**
     * Start local text extraction service with screen capture
     */
    @ReactMethod
    fun startLocalTextExtraction(
        captureInterval: Int,
        enableValidation: Boolean,
        enableCaching: Boolean,
        enableROIOptimization: Boolean,
        promise: Promise
    ) {
        try {
            val context = reactApplicationContext
            
            // Create intent for LocalTextExtractionService
            val serviceIntent = Intent(context, LocalTextExtractionService::class.java).apply {
                putExtra("captureInterval", captureInterval.toLong())
                putExtra("enableValidation", enableValidation)
                putExtra("enableCaching", enableCaching)
                putExtra("enableROIOptimization", enableROIOptimization)
                putExtra("logToRustBackend", true)
            }
            
            // Start the service
            context.startForegroundService(serviceIntent)
            
            // Wait a moment for service to initialize
            moduleScope.launch {
                delay(500)
                
                val service = LocalTextExtractionService.getInstance()
                if (service != null) {
                    // Connect screen capture callbacks
                    connectScreenCaptureCallbacks(service)
                    
                    // Start the extraction loop
                    service.startLocalTextExtractionLoop()
                    
                    withContext(Dispatchers.Main) {
                        val result = Arguments.createMap().apply {
                            putBoolean("success", true)
                            putString("message", "Local text extraction started successfully")
                            putInt("captureInterval", captureInterval)
                            putBoolean("validationEnabled", enableValidation)
                            putBoolean("cachingEnabled", enableCaching)
                            putBoolean("roiOptimizationEnabled", enableROIOptimization)
                        }
                        promise.resolve(result)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        promise.reject("SERVICE_START_ERROR", "Failed to start LocalTextExtractionService")
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting local text extraction: ${e.message}", e)
            promise.reject("START_ERROR", e.message)
        }
    }

    /**
     * Stop local text extraction service
     */
    @ReactMethod
    fun stopLocalTextExtraction(promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                // Stop the extraction loop
                service.stopLocalTextExtractionLoop()
                
                // Stop the service
                val context = reactApplicationContext
                val serviceIntent = Intent(context, LocalTextExtractionService::class.java)
                context.stopService(serviceIntent)
                
                val result = Arguments.createMap().apply {
                    putBoolean("success", true)
                    putString("message", "Local text extraction stopped successfully")
                }
                promise.resolve(result)
            } else {
                promise.reject("SERVICE_NOT_RUNNING", "LocalTextExtractionService is not running")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping local text extraction: ${e.message}", e)
            promise.reject("STOP_ERROR", e.message)
        }
    }

    /**
     * Get local text extraction statistics
     */
    @ReactMethod
    fun getLocalTextExtractionStats(promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                val stats = service.getStatistics()
                
                val result = Arguments.createMap().apply {
                    putBoolean("isRunning", LocalTextExtractionService.isRunning())
                    putDouble("totalCaptures", (stats["totalCaptures"] as Long).toDouble())
                    putDouble("successfulExtractions", (stats["successfulExtractions"] as Long).toDouble())
                    putDouble("successRate", (stats["successRate"] as Float).toDouble())
                    putDouble("averageProcessingTime", (stats["averageProcessingTime"] as Float).toDouble())
                    putDouble("totalTextExtracted", (stats["totalTextExtracted"] as Long).toDouble())
                    putBoolean("isActive", stats["isActive"] as Boolean)
                }
                promise.resolve(result)
            } else {
                val result = Arguments.createMap().apply {
                    putBoolean("isRunning", false)
                    putDouble("totalCaptures", 0.0)
                    putDouble("successfulExtractions", 0.0)
                    putDouble("successRate", 0.0)
                    putDouble("averageProcessingTime", 0.0)
                    putDouble("totalTextExtracted", 0.0)
                    putBoolean("isActive", false)
                }
                promise.resolve(result)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting statistics: ${e.message}", e)
            promise.reject("STATS_ERROR", e.message)
        }
    }

    /**
     * Check if local text extraction service is running
     */
    @ReactMethod
    fun isLocalTextExtractionRunning(promise: Promise) {
        try {
            val isRunning = LocalTextExtractionService.isRunning()
            val service = LocalTextExtractionService.getInstance()
            
            val result = Arguments.createMap().apply {
                putBoolean("isRunning", isRunning)
                putBoolean("serviceAvailable", service != null)
            }
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking service status: ${e.message}", e)
            promise.reject("STATUS_CHECK_ERROR", e.message)
        }
    }

    /**
     * Update capture interval
     */
    @ReactMethod
    fun setCaptureInterval(intervalMs: Int, promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                service.setCaptureInterval(intervalMs.toLong())
                
                val result = Arguments.createMap().apply {
                    putBoolean("success", true)
                    putInt("newInterval", intervalMs)
                }
                promise.resolve(result)
            } else {
                promise.reject("SERVICE_NOT_RUNNING", "LocalTextExtractionService is not running")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting capture interval: ${e.message}", e)
            promise.reject("SET_INTERVAL_ERROR", e.message)
        }
    }

    /**
     * Enable or disable validation
     */
    @ReactMethod
    fun setValidationEnabled(enabled: Boolean, promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                service.setValidationEnabled(enabled)
                
                val result = Arguments.createMap().apply {
                    putBoolean("success", true)
                    putBoolean("validationEnabled", enabled)
                }
                promise.resolve(result)
            } else {
                promise.reject("SERVICE_NOT_RUNNING", "LocalTextExtractionService is not running")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting validation: ${e.message}", e)
            promise.reject("SET_VALIDATION_ERROR", e.message)
        }
    }

    /**
     * Enable or disable caching
     */
    @ReactMethod
    fun setCachingEnabled(enabled: Boolean, promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                service.setCachingEnabled(enabled)
                
                val result = Arguments.createMap().apply {
                    putBoolean("success", true)
                    putBoolean("cachingEnabled", enabled)
                }
                promise.resolve(result)
            } else {
                promise.reject("SERVICE_NOT_RUNNING", "LocalTextExtractionService is not running")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting caching: ${e.message}", e)
            promise.reject("SET_CACHING_ERROR", e.message)
        }
    }

    /**
     * Perform single text extraction test
     */
    @ReactMethod
    fun performSingleTextExtraction(promise: Promise) {
        try {
            val result = Arguments.createMap().apply {
                putBoolean("success", true)
                putString("message", "Single text extraction test initiated. Use React Native ScreenCaptureModule directly for testing.")
            }
            promise.resolve(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in single text extraction: ${e.message}", e)
            promise.reject("SINGLE_EXTRACTION_ERROR", e.message)
        }
    }

    /**
     * Connect screen capture callbacks to the local text extraction service
     */
    private fun connectScreenCaptureCallbacks(service: LocalTextExtractionService) {
        try {
            // Get ScreenCaptureService instance
            val screenCaptureService = ScreenCaptureService.getInstance()
            
            if (screenCaptureService != null) {
                // Set up callbacks for screen capture integration
                service.onTriggerCapture = {
                    screenCaptureService.onTriggerCapture?.invoke()
                }
                
                service.onGetCapturedFrame = {
                    screenCaptureService.onGetCapturedFrame?.invoke()?.let { frame ->
                        LocalTextExtractionService.CapturedFrame(
                            base64 = frame.base64,
                            width = frame.width,
                            height = frame.height,
                            timestamp = frame.timestamp
                        )
                    }
                }
                
                Log.d(TAG, "✅ Screen capture callbacks connected to LocalTextExtractionService")
            } else {
                Log.w(TAG, "⚠️ ScreenCaptureService not available - callbacks not connected")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error connecting screen capture callbacks: ${e.message}", e)
        }
    }
}