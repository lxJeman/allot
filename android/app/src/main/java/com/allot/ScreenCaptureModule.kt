package com.allot

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ScreenCaptureModule(reactContext: ReactApplicationContext) :
        ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "ScreenCaptureModule"
        const val SCREEN_CAPTURE_REQUEST_CODE = 1000
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "screen_capture_service"
        private var mediaProjection: MediaProjection? = null
        private var virtualDisplay: VirtualDisplay? = null
        private var imageReader: ImageReader? = null
        private var isCapturing = false
        private var captureHandler: Handler? = null
        private var captureRunnable: Runnable? = null
        private var captureInterval = 100L // 100ms = 10 FPS
        private var shouldProcessNextFrame = false // Control frame processing
    }

    override fun getName(): String {
        return "ScreenCaptureModule"
    }

    @ReactMethod
    fun startScreenCapture(promise: Promise) {
        Log.d(TAG, "🎬 Starting screen capture...")

        if (isCapturing) {
            Log.w(TAG, "Screen capture already running")
            promise.resolve(true)
            return
        }

        // Check if we have valid permission data from the shared holder
        val pendingIntent = ProjectionPermissionHolder.pendingDataIntent
        val pendingResultCode = ProjectionPermissionHolder.pendingResultCode

        if (pendingIntent == null || pendingResultCode != Activity.RESULT_OK) {
            Log.e(
                    TAG,
                    "❌ No valid permission data - resultCode: $pendingResultCode, intent: ${pendingIntent != null}"
            )
            promise.reject(
                    "NO_PERMISSION_INTENT",
                    "No saved MediaProjection intent. Request permission first."
            )
            return
        }

        // Start on background thread to avoid blocking main thread
        Thread {
                    try {
                        Log.d(TAG, "🔧 Setting up screen capture on background thread...")

                        // 1. Create notification channel first
                        createNotificationChannel()

                        // 2. Start foreground service BEFORE creating MediaProjection
                        Log.d(TAG, "🚀 Starting foreground service...")
                        val serviceIntent =
                                Intent(reactApplicationContext, ScreenCaptureService::class.java)
                        serviceIntent.putExtra("resultCode", pendingResultCode)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            reactApplicationContext.startForegroundService(serviceIntent)
                        } else {
                            reactApplicationContext.startService(serviceIntent)
                        }

                        // Give service time to start
                        Thread.sleep(500)

                        // 3. Create MediaProjection with stored permission data
                        Log.d(
                                TAG,
                                "📱 Creating MediaProjection with resultCode: $pendingResultCode"
                        )
                        val mediaProjectionManager =
                                reactApplicationContext.getSystemService(
                                        Context.MEDIA_PROJECTION_SERVICE
                                ) as
                                        MediaProjectionManager

                        mediaProjection =
                                mediaProjectionManager.getMediaProjection(
                                        pendingResultCode,
                                        pendingIntent
                                )

                        if (mediaProjection == null) {
                            Log.e(TAG, "❌ Failed to create MediaProjection")
                            promise.reject(
                                    "PROJECTION_ERROR",
                                    "Failed to create MediaProjection with valid permission data"
                            )
                            return@Thread
                        }

                        Log.d(TAG, "✅ MediaProjection created successfully")

                        // 4. Set up image capture on main thread
                        Handler(Looper.getMainLooper()).post {
                            try {
                                Log.d(TAG, "🖼️ Setting up image capture...")
                                setupImageCapture()

                                // 5. Start capture loop
                                startCaptureLoop()

                                isCapturing = true
                                Log.d(TAG, "✅ Screen capture started successfully")
                                promise.resolve(true)
                            } catch (e: Exception) {
                                Log.e(TAG, "❌ Error in image capture setup: ${e.message}", e)
                                cleanupCapture()
                                promise.reject(
                                        "CAPTURE_SETUP_ERROR",
                                        "Failed to setup image capture: ${e.message}"
                                )
                            }
                        }
                    } catch (e: SecurityException) {
                        Log.e(TAG, "❌ SecurityException: ${e.message}", e)
                        promise.reject(
                                "SECURITY_ERROR",
                                "Security error - check permissions: ${e.message}"
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Unexpected error starting screen capture: ${e.message}", e)
                        cleanupCapture()
                        promise.reject(
                                "CAPTURE_ERROR",
                                "Failed to start screen capture: ${e.message}"
                        )
                    }
                }
                .start()
    }

    @ReactMethod
    fun stopScreenCapture(promise: Promise) {
        try {
            Log.d(TAG, "Stopping screen capture...")

            stopCaptureLoop()
            cleanupCapture()

            // Stop foreground service
            val serviceIntent = Intent(reactApplicationContext, ScreenCaptureService::class.java)
            reactApplicationContext.stopService(serviceIntent)

            isCapturing = false
            Log.d(TAG, "Screen capture stopped")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping screen capture: ${e.message}", e)
            promise.reject("STOP_ERROR", e.message)
        }
    }

    @ReactMethod
    fun isCapturing(promise: Promise) {
        promise.resolve(isCapturing)
    }

    @ReactMethod
    fun setCaptureInterval(intervalMs: Int, promise: Promise) {
        try {
            captureInterval = intervalMs.toLong()
            Log.d(TAG, "Capture interval set to ${intervalMs}ms")
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("INTERVAL_ERROR", e.message)
        }
    }

    @ReactMethod
    fun captureNextFrame(promise: Promise) {
        try {
            if (!isCapturing) {
                promise.reject("NOT_CAPTURING", "Screen capture is not active")
                return
            }
            
            Log.d(TAG, "🎯 Requesting next frame capture...")
            
            // Set flag to process the next available frame
            shouldProcessNextFrame = true
            
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting next frame: ${e.message}", e)
            promise.reject("CAPTURE_ERROR", e.message)
        }
    }

    private fun setupImageCapture() {
        try {
            Log.d(TAG, "🖼️ Setting up image capture...")

            val windowManager =
                    reactApplicationContext.getSystemService(Context.WINDOW_SERVICE) as
                            WindowManager
            val displayMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = windowManager.defaultDisplay
                display.getRealMetrics(displayMetrics)
            } else {
                @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
            }

            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val density = displayMetrics.densityDpi

            Log.d(TAG, "📐 Screen dimensions: ${width}x${height}, density: $density")

            // Validate dimensions
            if (width <= 0 || height <= 0) {
                throw IllegalStateException("Invalid screen dimensions: ${width}x${height}")
            }

            // Create ImageReader for capturing screenshots
            Log.d(TAG, "📷 Creating ImageReader...")
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

            if (imageReader?.surface == null) {
                throw IllegalStateException("Failed to create ImageReader surface")
            }

            // Create virtual display
            Log.d(TAG, "🖥️ Creating VirtualDisplay...")
            virtualDisplay =
                    mediaProjection?.createVirtualDisplay(
                            "AllotScreenCapture",
                            width,
                            height,
                            density,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            imageReader?.surface,
                            null,
                            null
                    )

            if (virtualDisplay == null) {
                throw IllegalStateException("Failed to create VirtualDisplay")
            }

            Log.d(TAG, "✅ VirtualDisplay created successfully")

            // Set up image available listener on background thread
            val backgroundHandler = Handler(Looper.getMainLooper())
            imageReader?.setOnImageAvailableListener(
                    { reader ->
                        try {
                            val image = reader.acquireLatestImage()
                            if (image != null) {
                                // Only process if we're actively capturing AND a frame was requested
                                if (isCapturing && shouldProcessNextFrame) {
                                    shouldProcessNextFrame = false // Reset flag
                                    Log.d(TAG, "🎯 Processing requested frame")
                                    Thread {
                                            try {
                                                processImage(image)
                                            } finally {
                                                image.close()
                                            }
                                        }
                                        .start()
                                } else {
                                    image.close() // Discard if not requested
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error in image listener: ${e.message}", e)
                        }
                    },
                    backgroundHandler
            )

            Log.d(TAG, "✅ Image capture setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error setting up image capture: ${e.message}", e)
            cleanupCapture()
            throw e
        }
    }

    private fun processImage(image: Image) {
        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width

            // Create bitmap from image
            val bitmap =
                    Bitmap.createBitmap(
                            image.width + rowPadding / pixelStride,
                            image.height,
                            Bitmap.Config.ARGB_8888
                    )
            bitmap.copyPixelsFromBuffer(buffer)

            // Crop to actual screen size if needed
            val croppedBitmap =
                    if (rowPadding != 0) {
                        Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
                    } else {
                        bitmap
                    }

            // Convert to Base64
            val base64 = bitmapToBase64(croppedBitmap, 80) // 80% JPEG quality

            // Save to cache (optional)
            val cacheFile = saveBitmapToCache(croppedBitmap)

            // Send to React Native
            val params =
                    Arguments.createMap().apply {
                        putString("base64", base64)
                        putString("filePath", cacheFile?.absolutePath)
                        putInt("width", image.width)
                        putInt("height", image.height)
                        putDouble("timestamp", System.currentTimeMillis().toDouble())
                    }

            sendEvent("onScreenCaptured", params)

            // Cleanup
            if (croppedBitmap != bitmap) {
                bitmap.recycle()
            }
            croppedBitmap.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing captured image: ${e.message}", e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap, quality: Int): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): File? {
        return try {
            val cacheDir = File(reactApplicationContext.cacheDir, "screenshots")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val file = File(cacheDir, "screenshot_${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()

            // Clean old files (keep only last 10)
            cleanOldScreenshots(cacheDir)

            file
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to cache: ${e.message}")
            null
        }
    }

    private fun cleanOldScreenshots(cacheDir: File) {
        try {
            val files = cacheDir.listFiles()?.sortedByDescending { it.lastModified() }
            files?.drop(10)?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning old screenshots: ${e.message}")
        }
    }

    private fun startCaptureLoop() {
        // No automatic loop - capture is now completely on-demand
        Log.d(TAG, "🔄 On-demand capture system ready - no automatic intervals")
    }

    private fun stopCaptureLoop() {
        captureRunnable?.let { captureHandler?.removeCallbacks(it) }
        captureHandler = null
        captureRunnable = null
    }

    private fun cleanupCapture() {
        virtualDisplay?.release()
        virtualDisplay = null

        imageReader?.close()
        imageReader = null

        mediaProjection?.stop()
        mediaProjection = null
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
                    }

            val notificationManager: NotificationManager =
                    reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                            NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendEvent(eventName: String, params: WritableMap?) {
        reactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
    }
}
