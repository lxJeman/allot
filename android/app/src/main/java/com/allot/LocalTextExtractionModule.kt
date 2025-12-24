package com.allot

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.allot.detection.TextExtractionResult
import com.allot.detection.TextRegion
import com.allot.detection.TextType

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
            
            // First ensure screen capture service is running
            Log.d(TAG, "🔍 Checking ScreenCaptureService availability...")
            val screenCaptureService = ScreenCaptureService.getInstance()
            if (screenCaptureService == null) {
                Log.w(TAG, "⚠️ ScreenCaptureService not running, local text extraction may not work properly")
            } else {
                Log.d(TAG, "✅ ScreenCaptureService is available")
                
                // IMPORTANT: Disable native backend to prevent sending to Rust backend
                screenCaptureService.setNativeBackendEnabled(false)
                Log.d(TAG, "🔧 Disabled native backend - frames will only be processed locally")
            }
            
            // Create intent for LocalTextExtractionService
            val serviceIntent = Intent(context, LocalTextExtractionService::class.java).apply {
                putExtra("captureInterval", captureInterval.toLong())
                putExtra("enableValidation", enableValidation)
                putExtra("enableCaching", false) // Force disable caching
                putExtra("enableROIOptimization", enableROIOptimization)
                putExtra("logToRustBackend", true)
                putExtra("forceNoCaching", true) // Force no caching to prevent eco mode
            }
            
            // Start the service
            context.startForegroundService(serviceIntent)
            
            // Wait a moment for service to initialize
            moduleScope.launch {
                delay(1000) // Give more time for service initialization
                
                val service = LocalTextExtractionService.getInstance()
                if (service != null) {
                    // Connect screen capture callbacks
                    connectScreenCaptureCallbacks(service)
                    
                    // CRITICAL: Disable native backend AFTER callbacks are connected
                    // This prevents ScreenCaptureModule from overriding our setting
                    delay(100) // Small delay to ensure callbacks are fully connected
                    screenCaptureService?.setNativeBackendEnabled(false)
                    Log.d(TAG, "🔧 Native backend disabled AFTER callback connection")
                    
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
                            putBoolean("screenCaptureServiceAvailable", screenCaptureService != null)
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
                
                // Re-enable native backend for normal operation
                val screenCaptureService = ScreenCaptureService.getInstance()
                screenCaptureService?.setNativeBackendEnabled(true)
                Log.d(TAG, "🔧 Re-enabled native backend for normal operation")
                
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
            val screenCaptureService = ScreenCaptureService.getInstance()
            
            val result = Arguments.createMap().apply {
                putBoolean("isRunning", isRunning)
                putBoolean("serviceAvailable", service != null)
                putBoolean("screenCaptureServiceAvailable", screenCaptureService != null)
                putBoolean("callbacksConnected", service?.onTriggerCapture != null && service?.onGetCapturedFrame != null)
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
            Log.d(TAG, "🧪 Starting single text extraction test...")
            
            // Check if screen capture service is available
            val screenCaptureService = ScreenCaptureService.getInstance()
            if (screenCaptureService == null) {
                promise.reject("SCREEN_CAPTURE_NOT_AVAILABLE", "Screen capture service is not running. Please start screen capture first.")
                return
            }
            
            moduleScope.launch {
                try {
                    // Trigger a screen capture
                    Log.d(TAG, "📸 Triggering screen capture...")
                    screenCaptureService.onTriggerCapture?.invoke()
                    
                    // Wait for capture to complete
                    delay(500)
                    
                    // Get the captured frame
                    val frame = screenCaptureService.onGetCapturedFrame?.invoke()
                    
                    if (frame != null) {
                        Log.d(TAG, "✅ Frame captured: ${frame.width}x${frame.height}")
                        
                        // Extract text using ML Kit directly
                        val bitmap = convertBase64ToBitmap(frame.base64)
                        val extractionResult = testMLKitDirectly(bitmap)
                        
                        withContext(Dispatchers.Main) {
                            val result = Arguments.createMap().apply {
                                putBoolean("success", true)
                                putString("message", "Single text extraction completed")
                                putString("extractedText", extractionResult.extractedText)
                                putDouble("confidence", extractionResult.confidence.toDouble())
                                putInt("textRegions", extractionResult.textRegions.size)
                                putDouble("processingTime", extractionResult.processingTimeMs.toDouble())
                                putBoolean("screenCaptureAvailable", true)
                                putInt("frameWidth", frame.width)
                                putInt("frameHeight", frame.height)
                            }
                            promise.resolve(result)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            promise.reject("CAPTURE_FAILED", "Failed to capture screen frame")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "❌ Single extraction failed: ${e.message}", e)
                        promise.reject("EXTRACTION_ERROR", "Single extraction failed: ${e.message}")
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in single text extraction: ${e.message}", e)
            promise.reject("SINGLE_EXTRACTION_ERROR", e.message)
        }
    }
    
    /**
     * Convert base64 string to bitmap
     */
    private fun convertBase64ToBitmap(base64String: String): android.graphics.Bitmap {
        val decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    /**
     * Force restart the extraction loop if it gets stuck
     */
    @ReactMethod
    fun forceRestartExtractionLoop(promise: Promise) {
        try {
            val service = LocalTextExtractionService.getInstance()
            
            if (service != null) {
                service.forceRestartExtractionLoop()
                
                val result = Arguments.createMap().apply {
                    putBoolean("success", true)
                    putString("message", "Extraction loop restart initiated")
                }
                promise.resolve(result)
            } else {
                promise.reject("SERVICE_NOT_RUNNING", "LocalTextExtractionService is not running")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error restarting extraction loop: ${e.message}", e)
            promise.reject("RESTART_ERROR", e.message)
        }
    }

    /**
     * Test text extraction with a known test image
     */
    @ReactMethod
    fun testTextExtractionWithTestImage(promise: Promise) {
        try {
            Log.d(TAG, "🧪 Testing ML Kit directly with synthetic image...")
            
            moduleScope.launch {
                try {
                    // Create a simple test image with text (white background, black text)
                    val testBitmap = createSimpleTestBitmap()
                    
                    // Test ML Kit directly
                    val result = testMLKitDirectly(testBitmap)
                    
                    withContext(Dispatchers.Main) {
                        val resultMap = Arguments.createMap().apply {
                            putBoolean("success", true)
                            putString("message", "ML Kit test completed")
                            putString("extractedText", result.extractedText)
                            putDouble("confidence", result.confidence.toDouble())
                            putInt("textRegions", result.textRegions.size)
                            putDouble("processingTime", result.processingTimeMs.toDouble())
                        }
                        promise.resolve(resultMap)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "❌ ML Kit test failed: ${e.message}", e)
                        promise.reject("ML_KIT_TEST_ERROR", "ML Kit test failed: ${e.message}")
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in ML Kit test: ${e.message}", e)
            promise.reject("TEST_ERROR", e.message)
        }
    }
    
    /**
     * Test ML Kit directly without any complex infrastructure
     */
    private suspend fun testMLKitDirectly(bitmap: android.graphics.Bitmap): TextExtractionResult {
        return withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            
            Log.d(TAG, "🔍 Testing ML Kit with bitmap ${bitmap.width}x${bitmap.height}")
            
            // Create ML Kit input image
            val inputImage = com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, 0)
            Log.d(TAG, "✅ InputImage created")
            
            // Create text recognizer
            val textRecognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
            )
            
            // Perform text recognition
            val visionText = suspendCancellableCoroutine { continuation ->
                Log.d(TAG, "🔍 Processing with ML Kit...")
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { text ->
                        Log.d(TAG, "✅ ML Kit success: found ${text.textBlocks.size} blocks")
                        continuation.resume(text)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "❌ ML Kit failed: ${exception.message}")
                        continuation.resumeWithException(exception)
                    }
            }
            
            val processingTime = System.currentTimeMillis() - startTime
            
            // Extract text
            var totalText = ""
            val textRegions = mutableListOf<TextRegion>()
            
            for (block in visionText.textBlocks) {
                Log.d(TAG, "📝 Block: '${block.text}'")
                totalText += "${block.text} "
                
                textRegions.add(
                    TextRegion(
                        text = block.text,
                        boundingBox = block.boundingBox ?: android.graphics.Rect(0, 0, 0, 0),
                        confidence = 0.9f, // ML Kit doesn't provide confidence
                        textType = TextType.GENERAL
                    )
                )
            }
            
            val finalText = totalText.trim()
            Log.d(TAG, "✅ ML Kit result: '$finalText' (${textRegions.size} regions, ${processingTime}ms)")
            
            TextExtractionResult(
                extractedText = finalText,
                confidence = if (finalText.isNotEmpty()) 0.9f else 0f,
                textRegions = textRegions,
                textDensity = finalText.length.toFloat() / (bitmap.width * bitmap.height) * 10000f,
                processingTimeMs = processingTime,
                usedCache = false,
                roiDetected = false,
                roiArea = 0,
                preprocessingTimeMs = 0L,
                validationPassed = true,
                validationScore = if (finalText.isNotEmpty()) 0.9f else 0f,
                fallbackUsed = false,
                fallbackStrategy = null
            )
        }
    }
    
    /**
     * Create a simple test bitmap with text
     */
    private fun createSimpleTestBitmap(): android.graphics.Bitmap {
        return try {
            // Create a bitmap with clear text
            val bitmap = android.graphics.Bitmap.createBitmap(400, 200, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            
            // White background
            canvas.drawColor(android.graphics.Color.WHITE)
            
            // Black text
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 48f
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            
            // Draw clear, readable text
            canvas.drawText("Hello World", 50f, 80f, paint)
            canvas.drawText("ML Kit Test", 50f, 140f, paint)
            
            Log.d(TAG, "✅ Test bitmap created: ${bitmap.width}x${bitmap.height}")
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating test bitmap: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Create a simple test image with text for testing
     */
    private fun createSimpleTestImage(): String {
        return try {
            // Create a simple bitmap with text
            val bitmap = android.graphics.Bitmap.createBitmap(200, 100, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            
            // White background
            canvas.drawColor(android.graphics.Color.WHITE)
            
            // Black text
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 24f
                isAntiAlias = true
            }
            
            canvas.drawText("Test Text", 20f, 50f, paint)
            
            // Convert to base64
            val byteArrayOutputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            bitmap.recycle()
            
            android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating test image: ${e.message}", e)
            // Return a minimal 1x1 pixel image as fallback
            "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A"
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
                    Log.v(TAG, "🎯 Triggering screen capture...")
                    screenCaptureService.onTriggerCapture?.invoke()
                }
                
                service.onGetCapturedFrame = {
                    val frame = screenCaptureService.onGetCapturedFrame?.invoke()
                    if (frame != null) {
                        Log.v(TAG, "📸 Got captured frame: ${frame.width}x${frame.height}")
                        LocalTextExtractionService.CapturedFrame(
                            base64 = frame.base64,
                            width = frame.width,
                            height = frame.height,
                            timestamp = frame.timestamp
                        )
                    } else {
                        Log.w(TAG, "⚠️ No frame available from ScreenCaptureService")
                        null
                    }
                }
                
                Log.d(TAG, "✅ Screen capture callbacks connected to LocalTextExtractionService")
            } else {
                Log.w(TAG, "⚠️ ScreenCaptureService not available - callbacks not connected")
                
                // Set up fallback callbacks that log the issue
                service.onTriggerCapture = {
                    Log.w(TAG, "⚠️ Trigger capture called but ScreenCaptureService not available")
                }
                
                service.onGetCapturedFrame = {
                    Log.w(TAG, "⚠️ Get frame called but ScreenCaptureService not available")
                    null
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error connecting screen capture callbacks: ${e.message}", e)
        }
    }
}