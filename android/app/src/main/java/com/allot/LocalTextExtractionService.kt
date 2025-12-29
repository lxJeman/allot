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
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*

/**
 * LocalTextExtractionService - SIMPLIFIED VERSION Sequential screen capture with local ML text
 * extraction
 */
class LocalTextExtractionService : Service() {

    companion object {
        const val TAG = "LocalTextExtractionService"
        const val NOTIFICATION_ID = 2002
        const val CHANNEL_ID = "local_text_extraction_service"
        private var instance: LocalTextExtractionService? = null

        fun getInstance(): LocalTextExtractionService? = instance
        fun isRunning(): Boolean = instance != null
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private var isProcessingActive = false
    private var isCaptureLoopActive = false
    private val handler = Handler(Looper.getMainLooper())
    private val isProcessingFrame = AtomicBoolean(false)

    // Local text extraction system
    private val localTextExtractor by lazy { LocalTextExtractor() }

    // Configuration
    private var enableValidation = true
    private var logToRustBackend = true

    // Statistics
    private var totalCaptures = 0L
    private var successfulExtractions = 0L
    private var totalProcessingTime = 0L
    // Callbacks for screen capture integration
    var onTriggerCapture: (() -> Unit)? = null
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
        Log.d(TAG, "✅ LocalTextExtractionService created")
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🚀 LocalTextExtractionService started")

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Parse configuration
        intent?.let { parseConfiguration(it) }

        // Start processing if not already active
        if (!isProcessingActive) {
            startBackgroundProcessing()
        }

        // Connect to screen capture service and start extraction
        serviceScope.launch {
            delay(2000) // Give time for ScreenCaptureService to initialize
            connectToScreenCaptureService()
            startLocalTextExtractionLoop()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onCreate()
        instance = null
        isProcessingActive = false
        isCaptureLoopActive = false
        serviceScope.cancel()
        releaseWakeLock()
        Log.d(TAG, "❌ LocalTextExtractionService destroyed")
    }
    private fun parseConfiguration(intent: Intent) {
        enableValidation = intent.getBooleanExtra("enableValidation", true)
        logToRustBackend = intent.getBooleanExtra("logToRustBackend", true)
        Log.d(TAG, "🔧 Configuration: validation=$enableValidation")
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock =
                    powerManager.newWakeLock(
                            PowerManager.PARTIAL_WAKE_LOCK,
                            "Allot::LocalTextExtractionWakeLock"
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
                wakeLock?.let {
                    if (!it.isHeld) {
                        acquireWakeLock()
                    }
                }
                Log.v(TAG, "💓 Service heartbeat - still alive")
            }
        }
    }
    private fun connectToScreenCaptureService() {
        try {
            val screenCaptureService = ScreenCaptureService.getInstance()

            if (screenCaptureService != null) {
                Log.d(TAG, "✅ Connecting to ScreenCaptureService...")

                // COMPLETELY DISABLE ScreenCaptureService's own loop to prevent conflicts
                screenCaptureService.stopCaptureLoop()
                screenCaptureService.setNativeBackendEnabled(false)
                Log.d(
                        TAG,
                        "🛑 DISABLED ScreenCaptureService loop - LocalTextExtractionService will handle everything"
                )

                // Set up callbacks for screen capture integration
                onTriggerCapture = {
                    Log.v(TAG, "🎯 Triggering screen capture...")
                    screenCaptureService.onTriggerCapture?.invoke()
                }

                onGetCapturedFrame = {
                    val frame = screenCaptureService.onGetCapturedFrame?.invoke()
                    if (frame != null) {
                        Log.v(TAG, "📸 Got captured frame: ${frame.width}x${frame.height}")
                        CapturedFrame(
                                base64 = frame.base64,
                                width = frame.width,
                                height = frame.height,
                                timestamp = frame.timestamp
                        )
                    } else {
                        Log.v(TAG, "⚠️ No frame available from ScreenCaptureService")
                        null
                    }
                }

                Log.d(TAG, "✅ Screen capture callbacks connected")
            } else {
                Log.w(TAG, "⚠️ ScreenCaptureService not available - will retry later")

                // Retry connection after delay
                serviceScope.launch {
                    delay(5000)
                    if (isProcessingActive) {
                        connectToScreenCaptureService()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error connecting to ScreenCaptureService: ${e.message}", e)
        }
    }
    fun startLocalTextExtractionLoop(singleExtraction: Boolean = false) {
        if (isCaptureLoopActive) {
            Log.w(TAG, "⚠️ Local text extraction loop already active")
            return
        }

        isCaptureLoopActive = true
        Log.d(
                TAG,
                "🎬 Starting NATURAL CYCLE system ${if (singleExtraction) "single extraction" else "loop"}"
        )
        Log.d(TAG, "🤖 Local ML Kit: ENABLED")
        Log.d(TAG, "⏱️ Mode: ONE CAPTURE → EXTRACT → ANALYZE → NEXT CYCLE (natural timing)")

        // TRULY SEQUENTIAL processing loop
        serviceScope.launch {
            var consecutiveErrors = 0
            val maxConsecutiveErrors = 5

            try {
                // If single extraction, stop immediately after first frame
                if (singleExtraction) {
                    if (!isProcessingFrame.get()) {
                        Log.d(TAG, "🔄 ═══ STARTING SINGLE EXTRACTION ═══")
                        processFrameWithLocalML()
                    }
                    Log.d(TAG, "✅ Single extraction completed")
                    isCaptureLoopActive = false
                    return@launch
                }

                // SEQUENTIAL LOOP: Wait for each cycle to complete before starting next
                while (isActive && isCaptureLoopActive) {
                    try {
                        // Only start next cycle if not already processing
                        if (!isProcessingFrame.get()) {
                            val cycleStartTime = System.currentTimeMillis()
                            Log.d(TAG, "")
                            Log.d(TAG, "🔄 ═══ STARTING NEW NATURAL CYCLE ═══")

                            try {
                                processFrameWithLocalML()
                                consecutiveErrors = 0 // Reset on success
                            } catch (e: Exception) {
                                consecutiveErrors++
                                Log.w(
                                        TAG,
                                        "⚠️ Error in frame processing (attempt $consecutiveErrors): ${e.message}"
                                )
                            }

                            // WAIT for this cycle to COMPLETELY finish
                            var waitTime = 0L
                            while (isProcessingFrame.get() && isCaptureLoopActive) {
                                delay(300) // Check every 300ms
                                waitTime += 300
                                if (waitTime > 30000) { // 30 second timeout
                                    Log.w(TAG, "⚠️ Cycle timeout - forcing reset")
                                    isProcessingFrame.set(false)
                                    break
                                }
                            }

                            val cycleTime = System.currentTimeMillis() - cycleStartTime
                            Log.d(TAG, "✅ ═══ CYCLE COMPLETE (${cycleTime}ms) ═══")
                            Log.d(TAG, "")

                            // NO ARTIFICIAL DELAYS - natural cycle timing
                            // Next cycle starts immediately after this one completes
                        } else {
                            // If somehow still processing, wait longer
                            Log.w(TAG, "⚠️ Still processing previous cycle, waiting...")
                            delay(2000)
                        }

                        // Handle consecutive errors
                        if (consecutiveErrors >= maxConsecutiveErrors) {
                            Log.w(TAG, "⚠️ Too many consecutive errors, taking longer break...")
                            delay(10000) // 10 second break
                            consecutiveErrors = 0
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "⚠️ Error in sequential loop iteration: ${e.message}")
                        isProcessingFrame.set(false) // Reset on error
                        delay(3000) // Wait 3 seconds before retry
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Critical error in sequential text extraction loop: ${e.message}")
                isProcessingFrame.set(false)
            } finally {
                isCaptureLoopActive = false
                Log.d(TAG, "⏹️ Sequential text extraction loop stopped")
            }
        }
    }

    private fun processFrameWithLocalML() {
        serviceScope.launch(Dispatchers.IO) {
            if (!isProcessingFrame.compareAndSet(false, true)) {
                return@launch // Already processing
            }

            try {
                val overallStartTime = System.currentTimeMillis()
                Log.d(TAG, "🎯 Starting SINGLE on-demand capture...")

                // SINGLE ON-DEMAND CAPTURE - No continuous stream
                val frame =
                        withContext(Dispatchers.Main) {
                            // Instead of triggering continuous capture, request ONE frame
                            Log.d(TAG, "📸 Requesting SINGLE frame capture...")

                            // Trigger ONE capture and wait for result
                            onTriggerCapture?.invoke()

                            // Wait longer for the single frame to be captured
                            delay(500) // Give more time for single capture

                            // Get the captured frame
                            val capturedFrame = onGetCapturedFrame?.invoke()

                            if (capturedFrame != null) {
                                Log.d(
                                        TAG,
                                        "✅ SINGLE frame captured successfully: ${capturedFrame.width}x${capturedFrame.height}"
                                )
                            } else {
                                Log.w(TAG, "⚠️ SINGLE frame capture failed - no frame returned")
                            }

                            capturedFrame
                        }

                if (frame != null) {
                    totalCaptures++

                    Log.d(
                            TAG,
                            "📸 Processing SINGLE captured frame: ${frame.width}x${frame.height}"
                    )

                    // Convert base64 to bitmap
                    val bitmap = base64ToBitmap(frame.base64)

                    // Extract text using local ML Kit
                    val mlStartTime = System.currentTimeMillis()
                    val textResult = localTextExtractor.extractText(bitmap)
                    val mlTime = System.currentTimeMillis() - mlStartTime

                    // TODO: Send to LLM for analysis here
                    // val llmResult = sendToLLM(textResult.extractedText)

                    val totalTime = System.currentTimeMillis() - overallStartTime

                    // Update statistics
                    if (textResult.extractedText.isNotEmpty()) {
                        successfulExtractions++
                    }
                    totalProcessingTime += totalTime

                    // Log results
                    if (logToRustBackend) {
                        Log.d(TAG, "")
                        Log.d(TAG, "🔍 ═══════════════════════════════════════")
                        Log.d(TAG, "🔍 SINGLE FRAME ANALYSIS COMPLETE")
                        Log.d(TAG, "🔍 ═══════════════════════════════════════")
                        Log.d(TAG, "📱 Frame: ${frame.width}x${frame.height}")
                        Log.d(
                                TAG,
                                "📝 Extracted Text: \"${textResult.extractedText.take(200)}${if (textResult.extractedText.length > 200) "..." else ""}\""
                        )
                        Log.d(TAG, "📊 Confidence: ${(textResult.confidence * 100).toInt()}%")
                        Log.d(TAG, "⏱️ ML Processing Time: ${mlTime}ms")
                        Log.d(TAG, "⏱️ Total Time: ${totalTime}ms")
                        Log.d(TAG, "🎯 Captures: SINGLE on-demand (not continuous)")
                        Log.d(TAG, "🔒 Processing: LOCAL ML Kit → [TODO: LLM Analysis]")
                        Log.d(TAG, "🔍 ═══════════════════════════════════════")
                        Log.d(TAG, "")
                    }

                    // Clean up bitmap
                    bitmap.recycle()
                } else {
                    Log.w(TAG, "⚠️ No frame captured - skipping this cycle")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing SINGLE frame: ${e.message}")
            } finally {
                isProcessingFrame.set(false)
                Log.d(TAG, "✅ SINGLE frame processing complete - ready for next cycle")
            }
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

    fun performSingleTextExtraction() {
        Log.d(TAG, "🧪 Performing single text extraction test...")
        startLocalTextExtractionLoop(singleExtraction = true)
    }

    fun stopLocalTextExtractionLoop() {
        isCaptureLoopActive = false
        Log.d(TAG, "🛑 Stopping local text extraction loop")
    }

    fun getStatistics(): Map<String, Any> {
        return mapOf(
                "totalCaptures" to totalCaptures,
                "successfulExtractions" to successfulExtractions,
                "averageProcessingTime" to
                        if (totalCaptures > 0) totalProcessingTime.toFloat() / totalCaptures
                        else 0f,
                "isActive" to isCaptureLoopActive
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Local Text Extraction Service"
            val descriptionText = "Allot is extracting text from screen content using local ML"
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
                .setContentTitle("Local Text Extraction Active")
                .setContentText("Analyzing screen content locally • 100% private")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setShowWhen(false)
                .build()
    }

    fun setCaptureInterval(intervalMs: Long) {
        // No-op: Natural cycle timing, no artificial intervals
        Log.d(TAG, "⏱️ Natural cycle timing - ignoring interval setting")
    }

    fun setValidationEnabled(enabled: Boolean) {
        enableValidation = enabled
        Log.d(TAG, "🛡️ Validation: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    fun setCachingEnabled(enabled: Boolean) {
        // Caching disabled in simplified version
        Log.d(TAG, "💾 Caching: DISABLED (simplified version)")
    }

    fun forceRestartExtractionLoop() {
        Log.d(TAG, "🔄 Force restarting extraction loop...")
        stopLocalTextExtractionLoop()

        // Wait a moment then restart
        serviceScope.launch {
            delay(2000)
            if (isProcessingActive) {
                startLocalTextExtractionLoop()
            }
        }
    }
}
