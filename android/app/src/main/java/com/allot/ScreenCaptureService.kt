package com.allot

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.allot.detection.LocalTextExtractor
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*
import org.json.JSONObject

class ScreenCaptureService : Service() {

    companion object {
        const val TAG = "ScreenCaptureService"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "screen_capture_service"
        private var instance: ScreenCaptureService? = null

        fun getInstance(): ScreenCaptureService? = instance
        fun isRunning(): Boolean = instance != null
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private var isProcessingActive = false
    private var isCaptureLoopActive = false
    private val handler = Handler(Looper.getMainLooper())
    private val isProcessingFrame = AtomicBoolean(false)

    // Backend configuration - KEEP GROQ BACKEND!
    private var backendUrl = "http://192.168.100.47:3000/analyze"
    private var enableNativeBackend = true
    
    // Local text extraction integration (REPLACES Google Vision API)
    private val localTextExtractor by lazy { com.allot.detection.LocalTextExtractor() }

    // Callback for capture events (set by ScreenCaptureModule)
    var onCaptureCallback: ((String, Int, Int, Long) -> Unit)? = null

    // Callback to trigger capture (set by ScreenCaptureModule)
    var onTriggerCapture: (() -> Unit)? = null

    // Callback to get captured frame (set by ScreenCaptureModule)
    var onGetCapturedFrame: (() -> CapturedFrame?)? = null

    // NEW: Direct bitmap callback for hot path (service-to-service)
    var onGetCapturedBitmap: (() -> android.graphics.Bitmap?)? = null

    data class CapturedFrame(
            val base64: String,
            val width: Int,
            val height: Int,
            val timestamp: Long
    )

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "✅ ScreenCaptureService created")
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🚀 ScreenCaptureService started")

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Start monitoring if not already active
        if (!isProcessingActive) {
            startBackgroundProcessing()
        }

        return START_STICKY // Restart if killed by system
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isProcessingActive = false
        serviceScope.cancel()
        releaseWakeLock()
        Log.d(TAG, "❌ ScreenCaptureService destroyed")
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock =
                    powerManager.newWakeLock(
                            PowerManager.PARTIAL_WAKE_LOCK,
                            "Allot::ScreenCaptureWakeLock"
                    )
            wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes
            Log.d(TAG, "🔋 WakeLock acquired")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to acquire WakeLock: ${e.message}")
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "🔋 WakeLock released")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to release WakeLock: ${e.message}")
        }
    }

    private fun startBackgroundProcessing() {
        isProcessingActive = true
        Log.d(TAG, "🔄 Background processing started")

        // Heartbeat to keep service alive
        serviceScope.launch {
            while (isActive && isProcessingActive) {
                delay(30000) // Check every 30 seconds

                // Renew wake lock if needed
                wakeLock?.let {
                    if (!it.isHeld) {
                        acquireWakeLock()
                    }
                }

                Log.v(TAG, "💓 Service heartbeat - still alive")
            }
        }
    }

    fun startCaptureLoop() {
        // Check if LocalTextExtractionService is running - if so, don't start our own loop
        if (com.allot.LocalTextExtractionService.isRunning()) {
            Log.w(TAG, "🛑 LocalTextExtractionService is running - ScreenCaptureService will NOT start its own loop")
            Log.w(TAG, "   LocalTextExtractionService will handle all processing to prevent conflicts")
            return
        }
        
        if (isCaptureLoopActive) {
            Log.w(TAG, "⚠️ Capture loop already active")
            return
        }

        isCaptureLoopActive = true
        Log.d(TAG, "🎬 Starting SEQUENTIAL processing loop (no fixed interval)")
        Log.d(TAG, "🌐 Backend: ${if (enableNativeBackend) "GROQ LLM ENABLED" else "DISABLED"}")
        Log.d(TAG, "🤖 Text Extraction: LOCAL ML KIT (replaces Google Vision API)")

        // TRULY SEQUENTIAL PROCESSING: One complete cycle at a time
        serviceScope.launch {
            Log.d(TAG, "🔄 Starting TRULY sequential processing - one cycle at a time")
            
            while (isActive && isCaptureLoopActive) {
                try {
                    if (enableNativeBackend) {
                        // Only start next cycle if not already processing
                        if (!isProcessingFrame.get()) {
                            val cycleStartTime = System.currentTimeMillis()
                            Log.d(TAG, "")
                            Log.d(TAG, "🔄 ═══ STARTING NEW CYCLE ═══")
                            
                            // Process one complete frame
                            processFrameNatively()
                            
                            // WAIT for this cycle to COMPLETELY finish
                            var waitTime = 0L
                            while (isProcessingFrame.get() && isCaptureLoopActive) {
                                delay(200) // Check every 200ms
                                waitTime += 200
                                if (waitTime > 30000) { // 30 second timeout
                                    Log.w(TAG, "⚠️ Cycle timeout - forcing reset")
                                    isProcessingFrame.set(false)
                                    break
                                }
                            }
                            
                            val cycleTime = System.currentTimeMillis() - cycleStartTime
                            Log.d(TAG, "✅ ═══ CYCLE COMPLETE (${cycleTime}ms) ═══")
                            Log.d(TAG, "")
                            
                            // LONGER delay between cycles to prevent overwhelming
                            val delayTime = if (cycleTime < 2000) {
                                3000L // If cycle was fast, wait 3 seconds
                            } else {
                                2000L // If cycle was slow, wait 2 seconds
                            }
                            
                            Log.d(TAG, "⏳ Waiting ${delayTime}ms before next cycle...")
                            delay(delayTime)
                        } else {
                            // If somehow still processing, wait longer
                            Log.w(TAG, "⚠️ Still processing previous cycle, waiting...")
                            delay(1000)
                        }
                    } else {
                        // React Native mode: much slower
                        handler.post { onTriggerCapture?.invoke() }
                        delay(5000) // 5 second delay in React Native mode
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error in sequential processing loop: ${e.message}")
                    isProcessingFrame.set(false) // Reset on error
                    delay(3000) // Wait 3 seconds before retry on error
                }
            }
            Log.d(TAG, "⏹️ Sequential processing loop stopped")
        }
    }

    private fun processFrameNatively() {
        serviceScope.launch(Dispatchers.IO) {
            if (!isProcessingFrame.compareAndSet(false, true)) {
                Log.w(TAG, "⚠️ Frame processing already in progress - skipping")
                return@launch
            }

            try {
                val overallStartTime = System.currentTimeMillis()
                Log.d(TAG, "🎬 Processing frame started...")

                // 🔥 HOT PATH: Try to get direct bitmap first (faster)
                var bitmap: android.graphics.Bitmap? = null
                var frame: CapturedFrame? = null

                try {
                    bitmap = onGetCapturedBitmap?.invoke()
                    if (bitmap != null && !bitmap.isRecycled) {
                        Log.d(TAG, "🔥 HOT PATH: Direct bitmap captured: ${bitmap.width}x${bitmap.height}")
                    } else {
                        Log.v(TAG, "⚠️ HOT PATH: No direct bitmap available, trying cold path...")
                        bitmap = null
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ HOT PATH failed: ${e.message}")
                    bitmap = null
                }

                // ❄️ COLD PATH: Fallback to base64 frame if hot path failed
                if (bitmap == null) {
                    Log.d(TAG, "❄️ COLD PATH: Triggering frame capture...")
                    frame = withContext(Dispatchers.Main) {
                        onTriggerCapture?.invoke()
                        delay(100) // Give more time for capture
                        onGetCapturedFrame?.invoke()
                    }

                    if (frame != null) {
                        Log.d(TAG, "❄️ COLD PATH: Frame captured: ${frame.width}x${frame.height}")
                        // Convert base64 to bitmap
                        bitmap = base64ToBitmap(frame.base64)
                    } else {
                        Log.w(TAG, "❄️ COLD PATH: No frame available")
                    }
                }

                if (bitmap != null && !bitmap.isRecycled) {
                    // Extract text using local ML Kit
                    Log.d(TAG, "🤖 Starting ML Kit text extraction...")
                    val mlStartTime = System.currentTimeMillis()
                    val textResult = localTextExtractor.extractText(bitmap)
                    val mlTime = System.currentTimeMillis() - mlStartTime

                    Log.d(TAG, "🔍 Local ML extraction complete:")
                    Log.d(TAG, "   📝 Text: '${textResult.extractedText.take(100)}${if (textResult.extractedText.length > 100) "..." else ""}'")
                    Log.d(TAG, "   📊 Confidence: ${(textResult.confidence * 100).toInt()}%")
                    Log.d(TAG, "   ⏱️ ML Time: ${mlTime}ms")

                    // Send extracted text to GROQ BACKEND for LLM analysis
                    if (textResult.extractedText.isNotBlank() && textResult.extractedText.length > 3) {
                        Log.d(TAG, "🌐 Sending text to Groq backend...")
                        val backendStartTime = System.currentTimeMillis()
                        val result = sendTextToBackend(textResult.extractedText, frame?.width ?: bitmap.width, frame?.height ?: bitmap.height)
                        val backendTime = System.currentTimeMillis() - backendStartTime
                        val totalTime = System.currentTimeMillis() - overallStartTime

                        if (result != null) {
                            Log.d(TAG, "")
                            Log.d(TAG, "📊 ═══════════════════════════════════════")
                            Log.d(TAG, "📊 HYBRID ANALYSIS RESULT")
                            Log.d(TAG, "📊 ═══════════════════════════════════════")
                            Log.d(TAG, "📝 Extracted Text: '${textResult.extractedText}'")
                            Log.d(TAG, "🏷️ Category: ${result.category}")
                            Log.d(TAG, "📊 Confidence: ${(result.confidence * 100).toInt()}%")
                            Log.d(TAG, "🚨 Harmful: ${if (result.harmful) "YES" else "NO"}")
                            Log.d(TAG, "🎯 Action: ${result.action}")
                            Log.d(TAG, "⏱️ ML Kit Time: ${mlTime}ms")
                            Log.d(TAG, "⏱️ Groq Backend Time: ${backendTime}ms")
                            Log.d(TAG, "⏱️ Total Time: ${totalTime}ms")
                            Log.d(TAG, "🚀 Path: ${if (frame == null) "HOT (direct bitmap)" else "COLD (base64 fallback)"}")
                            Log.d(TAG, "🌐 Processing: LOCAL ML Kit + GROQ LLM Backend")
                            Log.d(TAG, "📊 ═══════════════════════════════════════")
                            Log.d(TAG, "")

                            // Handle harmful content
                            if (result.harmful) {
                                handleHarmfulContent(result)
                            }
                        } else {
                            Log.w(TAG, "⚠️ No result from backend")
                        }
                    } else {
                        Log.v(TAG, "⏭️ No meaningful text extracted (${textResult.extractedText.length} chars), continuing...")
                    }

                    // Clean up bitmap if we created it from base64
                    if (frame != null) {
                        bitmap.recycle()
                    }
                } else {
                    Log.w(TAG, "⏭️ No frame/bitmap available for processing")
                }
                
                val totalProcessingTime = System.currentTimeMillis() - overallStartTime
                Log.d(TAG, "✅ Frame processing completed in ${totalProcessingTime}ms")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing frame: ${e.message}", e)
            } finally {
                isProcessingFrame.set(false)
                Log.d(TAG, "🏁 Frame processing flag cleared")
            }
        }
    }

    private suspend fun sendTextToBackend(extractedText: String, width: Int, height: Int): AnalysisResult? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(backendUrl)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000 // 10 seconds
                connection.readTimeout = 30000 // 30 seconds

                // Create JSON payload with extracted text instead of image
                val payload =
                        JSONObject().apply {
                            put("extracted_text", extractedText) // Send text instead of image
                            put("width", width)
                            put("height", height)
                            put("timestamp", System.currentTimeMillis())
                            put("source", "local_ml_kit") // Indicate source
                        }

                // Send request
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                // Read response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response =
                            BufferedReader(InputStreamReader(connection.inputStream)).use { reader
                                ->
                                reader.readText()
                            }

                    // Parse response
                    parseAnalysisResponse(response)
                } else {
                    Log.e(TAG, "❌ Backend error: HTTP $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Network error: ${e.message}")
                null
            }
        }
    }

    private fun parseAnalysisResponse(json: String): AnalysisResult? {
        return try {
            val obj = JSONObject(json)
            val analysis = obj.getJSONObject("analysis")

            AnalysisResult(
                    category = analysis.getString("category"),
                    confidence = analysis.getDouble("confidence").toFloat(),
                    harmful = analysis.getBoolean("harmful"),
                    action = analysis.getString("action")
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to parse response: ${e.message}")
            null
        }
    }

    private fun base64ToBitmap(base64String: String): android.graphics.Bitmap {
        return try {
            val decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ?: throw IllegalArgumentException("Failed to decode bitmap from base64")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error converting base64 to bitmap: ${e.message}")
            throw e
        }
    }

    private fun handleHarmfulContent(result: AnalysisResult) {
        Log.w(TAG, "")
        Log.w(TAG, "🚫 ═══════════════════════════════════════")
        Log.w(TAG, "🚫 HARMFUL CONTENT DETECTED!")
        Log.w(TAG, "🚫 ═══════════════════════════════════════")
        Log.w(TAG, "🏷️  Category: ${result.category}")
        Log.w(TAG, "📊 Confidence: ${(result.confidence * 100).toInt()}%")
        Log.w(TAG, "🎯 Action: ${result.action.uppercase()}")
        Log.w(TAG, "🚫 ═══════════════════════════════════════")
        Log.w(TAG, "")

        // Set a failsafe timeout to reset state in case something goes wrong
        handler.postDelayed(
                {
                    Log.w(TAG, "⏰ Failsafe timeout - forcing processing state reset")
                    isProcessingFrame.set(false)
                },
                10000
        ) // 10 second failsafe

        // Show content blocking (blur + warning overlay)
        handler.post {
            try {
                val accessibilityService = AllotAccessibilityService.getInstance()

                if (accessibilityService != null) {
                    Log.d(TAG, "🚫 Showing content warning overlay...")

                    // Show warning overlay with blur effect
                    accessibilityService.showContentWarningOverlay(
                            "⚠️ HARMFUL CONTENT DETECTED\n\n" +
                                    "Category: ${result.category.uppercase()}\n" +
                                    "Confidence: ${(result.confidence * 100).toInt()}%\n\n" +
                                    "Scrolling to next content..."
                    )

                    Log.d(TAG, "✅ Content warning overlay shown")

                    // Auto-scroll after showing warning
                    handler.postDelayed(
                            {
                                Log.d(TAG, "⏭️  Performing auto-scroll...")
                                val scrolled = accessibilityService.performAutoScroll()
                                if (scrolled) {
                                    Log.d(TAG, "✅ Auto-scroll successful")
                                } else {
                                    Log.w(TAG, "⚠️  Auto-scroll failed - will still reset state")
                                }

                                // Hide overlay after scroll and reset state
                                handler.postDelayed(
                                        {
                                            accessibilityService.removeOverlay()

                                            // Reset processing state to allow new content detection
                                            isProcessingFrame.set(false)

                                            Log.d(
                                                    TAG,
                                                    "🔄 Overlay removed and processing state reset - ready for new content"
                                            )
                                        },
                                        2000
                                ) // Hide 2 seconds after scroll
                            },
                            1500
                    ) // Wait 1.5 seconds before scrolling
                } else {
                    Log.e(TAG, "❌ Accessibility service not available!")
                    Log.e(TAG, "   Please enable accessibility service for content blocking")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error handling harmful content: ${e.message}", e)
            }
        }
    }

    data class AnalysisResult(
            val category: String,
            val confidence: Float,
            val harmful: Boolean,
            val action: String
    )

    fun setBackendUrl(url: String) {
        backendUrl = url
        Log.d(TAG, "🌐 Backend URL set to: $url")
    }

    fun setNativeBackendEnabled(enabled: Boolean) {
        enableNativeBackend = enabled
        Log.d(TAG, "🌐 Groq Backend: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    fun stopCaptureLoop() {
        isCaptureLoopActive = false
        Log.d(TAG, "🛑 Stopping sequential processing loop")
    }

    fun notifyCapture(base64: String, width: Int, height: Int, timestamp: Long) {
        // Notify callback (React Native) if available
        handler.post { onCaptureCallback?.invoke(base64, width, height, timestamp) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Screen Capture Service"
            val descriptionText = "Allot is capturing screen content for analysis"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel =
                    NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                        setShowBadge(false)
                        enableLights(false)
                        enableVibration(false)
                        setSound(null, null)
                    }

            val notificationManager: NotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        } else {
                            PendingIntent.FLAG_UPDATE_CURRENT
                        }
                )

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Allot Hybrid Analysis")
                .setContentText("Local ML Kit + Groq LLM • Privacy-enhanced")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setShowWhen(false)
                .build()
    }
}
