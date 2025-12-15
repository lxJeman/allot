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
    private var captureInterval = 100L // 100ms = 10 FPS
    private val isProcessingFrame = AtomicBoolean(false)

    // Backend configuration
    private var backendUrl = "http://192.168.100.47:3000/analyze"
    private var enableNativeBackend = true

    // Callback for capture events (set by ScreenCaptureModule)
    var onCaptureCallback: ((String, Int, Int, Long) -> Unit)? = null

    // Callback to trigger capture (set by ScreenCaptureModule)
    var onTriggerCapture: (() -> Unit)? = null

    // Callback to get captured frame (set by ScreenCaptureModule)
    var onGetCapturedFrame: (() -> CapturedFrame?)? = null

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
        if (isCaptureLoopActive) {
            Log.w(TAG, "⚠️ Capture loop already active")
            return
        }

        isCaptureLoopActive = true
        Log.d(TAG, "🎬 Starting native capture loop (${captureInterval}ms interval)")
        Log.d(TAG, "🌐 Native backend: ${if (enableNativeBackend) "ENABLED" else "DISABLED"}")

        // Native capture loop running in service scope
        serviceScope.launch {
            while (isActive && isCaptureLoopActive) {
                try {
                    if (enableNativeBackend) {
                        // Native backend mode: capture and send directly
                        if (!isProcessingFrame.get()) {
                            processFrameNatively()
                        }
                    } else {
                        // React Native mode: just trigger capture
                        handler.post { onTriggerCapture?.invoke() }
                    }

                    // Wait for next interval
                    delay(captureInterval)
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error in capture loop: ${e.message}")
                }
            }
            Log.d(TAG, "⏹️ Native capture loop stopped")
        }
    }

    private fun processFrameNatively() {
        serviceScope.launch(Dispatchers.IO) {
            if (!isProcessingFrame.compareAndSet(false, true)) {
                return@launch // Already processing
            }

            try {
                // Trigger capture and get frame
                val frame =
                        withContext(Dispatchers.Main) {
                            onTriggerCapture?.invoke()
                            delay(50) // Give time for capture
                            onGetCapturedFrame?.invoke()
                        }

                if (frame != null) {
                    Log.d(TAG, "📸 Frame captured: ${frame.width}x${frame.height}")

                    // Send to backend
                    val startTime = System.currentTimeMillis()
                    val result = sendFrameToBackend(frame)
                    val duration = System.currentTimeMillis() - startTime

                    if (result != null) {
                        Log.d(
                                TAG,
                                "📊 Analysis complete (${duration}ms): ${result.category} (${result.confidence})"
                        )

                        // Handle harmful content
                        if (result.harmful) {
                            handleHarmfulContent(result)
                        }
                    }
                } else {
                    Log.v(TAG, "⏭️ No frame available")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing frame: ${e.message}")
            } finally {
                isProcessingFrame.set(false)
            }
        }
    }

    private suspend fun sendFrameToBackend(frame: CapturedFrame): AnalysisResult? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(backendUrl)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000 // 10 seconds
                connection.readTimeout = 30000 // 30 seconds

                // Create JSON payload
                val payload =
                        JSONObject().apply {
                            put("image", frame.base64)
                            put("width", frame.width)
                            put("height", frame.height)
                            put("timestamp", frame.timestamp)
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
        Log.d(TAG, "🌐 Native backend: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    fun stopCaptureLoop() {
        isCaptureLoopActive = false
        Log.d(TAG, "🛑 Stopping native capture loop")
    }

    fun setCaptureInterval(intervalMs: Long) {
        captureInterval = intervalMs
        Log.d(TAG, "⏱️ Capture interval set to ${intervalMs}ms")
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
                .setContentTitle("Allot Screen Analysis")
                .setContentText("Analyzing screen content for harmful material")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setShowWhen(false)
                .build()
    }
}
