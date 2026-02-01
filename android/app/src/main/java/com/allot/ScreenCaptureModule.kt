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
import android.os.HandlerThread
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
        private var captureInterval = 5000L // 5 seconds = 0.2 FPS (much slower for sequential processing)
        private var shouldProcessNextFrame = false // Control frame processing
        private var lastCapturedFrame: ScreenCaptureService.CapturedFrame? = null
        private var lastCapturedBitmap: android.graphics.Bitmap? = null // HOT PATH: Direct bitmap storage
        private var pendingCapturePromise: Promise? = null // Store pending promise
        private var backgroundThread: HandlerThread? = null
        
        // Bitmap memory pool for reuse
        private val bitmapPool = mutableListOf<Bitmap>()
        
        // Track if we're actively monitoring (to disable aggressive caching)
        private var lastProcessingStartTime = 0L
        private var isCurrentlyProcessing = false
        private const val MAX_POOL_SIZE = 3
        private var totalBitmapMemory = 0L
        private const val MAX_BITMAP_MEMORY = 50 * 1024 * 1024L // 50MB limit
    }

    override fun getName(): String {
        return "ScreenCaptureModule"
    }

    /**
     * Safely recycle the last captured bitmap to prevent memory leaks
     */
    private fun recycleLastBitmap() {
        lastCapturedBitmap?.let { bitmap ->
            if (!bitmap.isRecycled) {
                Log.d(TAG, "♻️ Recycling last bitmap: ${bitmap.width}x${bitmap.height}")
                
                // Calculate memory being freed
                val memoryFreed = bitmap.allocationByteCount.toLong()
                totalBitmapMemory -= memoryFreed
                
                // Try to return to pool if it's reusable
                if (bitmapPool.size < MAX_POOL_SIZE && bitmap.isMutable) {
                    Log.d(TAG, "🔄 Returning bitmap to pool (${bitmapPool.size + 1}/$MAX_POOL_SIZE)")
                    bitmapPool.add(bitmap)
                    totalBitmapMemory += memoryFreed // Add back to pool memory
                } else {
                    // Pool is full or bitmap not reusable, recycle it
                    bitmap.recycle()
                    Log.d(TAG, "♻️ Bitmap recycled, freed ${memoryFreed / 1024}KB")
                }
            } else {
                Log.w(TAG, "⚠️ Attempted to recycle already recycled bitmap")
            }
            lastCapturedBitmap = null
        }
    }

    /**
     * Check if we're actively monitoring and should avoid aggressive frame caching
     */
    private fun isActivelyMonitoring(): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // If we're currently processing, we're actively monitoring
        if (isCurrentlyProcessing) {
            return true
        }
        
        // If we recently started processing (within last 30 seconds), we're likely monitoring
        if (lastProcessingStartTime > 0 && (currentTime - lastProcessingStartTime) < 30000) {
            return true
        }
        
        // If capture interval is fast (< 2 seconds), we're likely monitoring
        if (captureInterval < 2000) {
            return true
        }
        
        return false
    }

    /**
     * Notify native side that processing has started
     */
    @ReactMethod
    fun notifyProcessingStarted(promise: Promise) {
        try {
            isCurrentlyProcessing = true
            lastProcessingStartTime = System.currentTimeMillis()
            Log.d(TAG, "🔄 Processing started - disabling aggressive frame caching")
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("PROCESSING_NOTIFY_ERROR", e.message)
        }
    }

    /**
     * Notify native side that processing has ended
     */
    @ReactMethod
    fun notifyProcessingEnded(promise: Promise) {
        try {
            isCurrentlyProcessing = false
            Log.d(TAG, "✅ Processing ended - re-enabling frame caching")
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("PROCESSING_NOTIFY_ERROR", e.message)
        }
    }

    /**
     * Get a bitmap from the pool or create a new one
     */
    private fun getBitmapFromPool(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        // Check if we have a suitable bitmap in the pool
        val iterator = bitmapPool.iterator()
        while (iterator.hasNext()) {
            val pooledBitmap = iterator.next()
            if (!pooledBitmap.isRecycled && 
                pooledBitmap.width == width && 
                pooledBitmap.height == height && 
                pooledBitmap.config == config) {
                
                iterator.remove()
                Log.d(TAG, "🔄 Reusing bitmap from pool (${bitmapPool.size}/$MAX_POOL_SIZE remaining)")
                return pooledBitmap
            }
        }
        
        // No suitable bitmap in pool, create new one
        val newBitmap = Bitmap.createBitmap(width, height, config)
        val memoryUsed = newBitmap.allocationByteCount.toLong()
        totalBitmapMemory += memoryUsed
        
        Log.d(TAG, "🆕 Created new bitmap: ${width}x${height}, memory: ${memoryUsed / 1024}KB, total: ${totalBitmapMemory / 1024 / 1024}MB")
        
        // Check if we're approaching memory limit
        if (totalBitmapMemory > MAX_BITMAP_MEMORY) {
            Log.w(TAG, "⚠️ Bitmap memory usage high: ${totalBitmapMemory / 1024 / 1024}MB, cleaning pool...")
            cleanBitmapPool()
        }
        
        return newBitmap
    }

    /**
     * Clean the bitmap pool to free memory
     */
    private fun cleanBitmapPool() {
        val iterator = bitmapPool.iterator()
        var freedMemory = 0L
        
        while (iterator.hasNext()) {
            val bitmap = iterator.next()
            if (!bitmap.isRecycled) {
                freedMemory += bitmap.allocationByteCount.toLong()
                bitmap.recycle()
            }
            iterator.remove()
        }
        
        totalBitmapMemory -= freedMemory
        Log.d(TAG, "🧹 Cleaned bitmap pool, freed ${freedMemory / 1024}KB, total memory: ${totalBitmapMemory / 1024 / 1024}MB")
    }

    /**
     * Monitor bitmap memory usage and log warnings
     */
    private fun logBitmapMemoryUsage() {
        val memoryMB = totalBitmapMemory / 1024 / 1024
        val poolSize = bitmapPool.size
        
        if (memoryMB > 30) { // Warn at 30MB
            Log.w(TAG, "⚠️ High bitmap memory usage: ${memoryMB}MB, pool size: $poolSize")
        } else {
            Log.d(TAG, "📊 Bitmap memory: ${memoryMB}MB, pool size: $poolSize")
        }
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

                        // MediaProjection created successfully - service will use React Native
                        // callbacks

                        // 4. Set up image capture on main thread
                        Handler(Looper.getMainLooper()).post {
                            try {
                                Log.d(TAG, "🖼️ Setting up image capture...")
                                setupImageCapture()

                                // 5. Connect service to trigger captures
                                connectServiceCallbacks()

                                // 6. Start native capture loop in service
                                com.allot.ScreenCaptureService.getInstance()?.startCaptureLoop()

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

            // Check if we have a recent frame (within 200ms for faster response)
            // BUT only if we're not in a monitoring scenario where content changes rapidly
            val frame = lastCapturedFrame
            val currentTime = System.currentTimeMillis()

            // Disable frame caching during active monitoring to ensure fresh captures
            // This prevents returning stale frames when content is changing (scrolling, etc.)
            val shouldUseCache = frame != null && 
                                (currentTime - frame.timestamp) < 200 &&
                                !isActivelyMonitoring()

            if (shouldUseCache) {
                // We have a very recent frame, return it immediately
                Log.d(TAG, "⚡ Returning very recent frame: ${frame.width}x${frame.height}")

                val result =
                        Arguments.createMap().apply {
                            putString("base64", frame.base64)
                            putInt("width", frame.width)
                            putInt("height", frame.height)
                            putDouble("timestamp", frame.timestamp.toDouble())
                        }

                promise.resolve(result)
                return
            } else if (frame != null && (currentTime - frame.timestamp) < 200) {
                Log.d(TAG, "🔄 Fresh frame needed - actively monitoring, forcing new capture")
            }

            // No recent frame, wait for a new one (should be very fast now)
            Log.d(TAG, "⏳ Waiting for fresh frame...")

            // Check if we already have a pending promise
            if (pendingCapturePromise != null) {
                Log.w(TAG, "⚠️ Frame capture already in progress, rejecting previous request")
                pendingCapturePromise?.reject(
                        "CAPTURE_SUPERSEDED",
                        "New capture request superseded this one"
                )
            }

            // Store the promise to resolve when frame is captured
            pendingCapturePromise = promise

            // Trigger frame generation by forcing a display refresh
            virtualDisplay?.let { display ->
                try {
                    val windowManager = reactApplicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val displayMetrics = DisplayMetrics()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
                    } else {
                        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
                    }
                    display.resize(displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi)
                    Log.d(TAG, "🔄 Triggered frame generation")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to trigger frame generation: ${e.message}")
                }
            }

            // Set a timeout to prevent hanging forever
            Handler(Looper.getMainLooper())
                    .postDelayed(
                            {
                                if (pendingCapturePromise == promise) {
                                    Log.w(
                                            TAG,
                                            "⚠️ Frame capture timeout, using last available frame"
                                    )
                                    pendingCapturePromise = null

                                    // Return last frame even if it's old, better than nothing
                                    val lastFrame = lastCapturedFrame
                                    if (lastFrame != null) {
                                        val result =
                                                Arguments.createMap().apply {
                                                    putString("base64", lastFrame.base64)
                                                    putInt("width", lastFrame.width)
                                                    putInt("height", lastFrame.height)
                                                    putDouble(
                                                            "timestamp",
                                                            lastFrame.timestamp.toDouble()
                                                    )
                                                }
                                        promise.resolve(result)
                                    } else {
                                        promise.reject("CAPTURE_TIMEOUT", "No frames available")
                                    }
                                }
                            },
                            1000
                    ) // 1 second timeout (should be much faster now)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting next frame: ${e.message}", e)
            promise.reject("CAPTURE_ERROR", e.message)
        }
    }

    @ReactMethod
    fun getLastCapturedFrame(promise: Promise) {
        try {
            val frame = lastCapturedFrame
            if (frame != null) {
                Log.d(TAG, "📸 Returning last captured frame: ${frame.width}x${frame.height}")

                val result = Arguments.createMap().apply {
                    putString("base64", frame.base64)
                    putInt("width", frame.width)
                    putInt("height", frame.height)
                    putDouble("timestamp", frame.timestamp.toDouble())
                }

                promise.resolve(result)
            } else {
                promise.reject("NO_FRAME", "No frame has been captured yet")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last frame: ${e.message}", e)
            promise.reject("GET_FRAME_ERROR", e.message)
        }
    }

    @ReactMethod
    fun getBitmapMemoryStats(promise: Promise) {
        try {
            val stats = Arguments.createMap().apply {
                putDouble("totalMemoryMB", totalBitmapMemory / 1024.0 / 1024.0)
                putInt("poolSize", bitmapPool.size)
                putInt("maxPoolSize", MAX_POOL_SIZE)
                putDouble("maxMemoryMB", MAX_BITMAP_MEMORY / 1024.0 / 1024.0)
                putBoolean("hasLastBitmap", lastCapturedBitmap != null)
                
                lastCapturedBitmap?.let { bitmap ->
                    if (!bitmap.isRecycled) {
                        putInt("lastBitmapWidth", bitmap.width)
                        putInt("lastBitmapHeight", bitmap.height)
                        putDouble("lastBitmapSizeMB", bitmap.allocationByteCount / 1024.0 / 1024.0)
                    }
                }
            }
            
            Log.d(TAG, "📊 Bitmap memory stats requested - Total: ${totalBitmapMemory / 1024 / 1024}MB, Pool: ${bitmapPool.size}")
            promise.resolve(stats)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bitmap memory stats: ${e.message}", e)
            promise.reject("STATS_ERROR", e.message)
        }
    }

    @ReactMethod
    fun forceBitmapCleanup(promise: Promise) {
        try {
            Log.d(TAG, "🧹 Forcing bitmap cleanup...")
            val beforeMemory = totalBitmapMemory
            
            recycleLastBitmap()
            cleanBitmapPool()
            
            val afterMemory = totalBitmapMemory
            val freedMemory = beforeMemory - afterMemory
            
            Log.d(TAG, "✅ Forced cleanup completed, freed ${freedMemory / 1024}KB")
            
            val result = Arguments.createMap().apply {
                putDouble("freedMemoryKB", freedMemory / 1024.0)
                putDouble("remainingMemoryMB", afterMemory / 1024.0 / 1024.0)
            }
            
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error during forced cleanup: ${e.message}", e)
            promise.reject("CLEANUP_ERROR", e.message)
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

            // Create ImageReader for capturing screenshots with more buffer capacity
            Log.d(TAG, "📷 Creating ImageReader...")
            imageReader =
                    ImageReader.newInstance(
                            width,
                            height,
                            PixelFormat.RGBA_8888,
                            5
                    ) // Increased from 2 to 5

            if (imageReader?.surface == null) {
                throw IllegalStateException("Failed to create ImageReader surface")
            }

            // Set up image available listener with proper background handler
            backgroundThread = HandlerThread("ScreenCapture")
            backgroundThread?.start()
            val backgroundHandler = Handler(backgroundThread!!.looper)

            // Create virtual display with different flags to force frame generation
            Log.d(TAG, "🖥️ Creating VirtualDisplay with enhanced settings...")
            virtualDisplay =
                    mediaProjection?.createVirtualDisplay(
                            "AllotScreenCapture",
                            width,
                            height,
                            density,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR or
                                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                            imageReader?.surface,
                            null,
                            backgroundHandler
                    )

            if (virtualDisplay == null) {
                throw IllegalStateException("Failed to create VirtualDisplay")
            }

            Log.d(TAG, "✅ VirtualDisplay created successfully")

            // No automatic refresh - only capture on demand

            imageReader?.setOnImageAvailableListener(
                    { reader ->
                        var image: Image? = null
                        try {
                            image = reader.acquireLatestImage()
                            if (image != null) {
                                // Only process frames when explicitly requested or when capturing is active
                                if (isCapturing && (shouldProcessNextFrame || pendingCapturePromise != null)) {
                                    Log.d(TAG, "🎯 Processing requested frame")
                                    shouldProcessNextFrame = false // Reset flag
                                    processImage(image)
                                } else {
                                    Log.v(TAG, "🚫 Discarding frame - not requested")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error in image listener: ${e.message}", e)
                        } finally {
                            // CRITICAL: Always close the image to free up ImageReader slots
                            image?.close()
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

            // CRITICAL: Recycle previous bitmap before creating new one
            recycleLastBitmap()

            // Create bitmap from image using memory pool
            val bitmap = getBitmapFromPool(
                image.width + rowPadding / pixelStride,
                image.height,
                Bitmap.Config.ARGB_8888
            )
            
            bitmap.copyPixelsFromBuffer(buffer)

            // Crop to actual screen size if needed
            val croppedBitmap = if (rowPadding != 0) {
                val cropped = getBitmapFromPool(image.width, image.height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(cropped)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                
                // Return original bitmap to pool since we created a cropped version
                if (bitmapPool.size < MAX_POOL_SIZE && bitmap.isMutable && !bitmap.isRecycled) {
                    bitmapPool.add(bitmap)
                } else {
                    bitmap.recycle()
                    totalBitmapMemory -= bitmap.allocationByteCount.toLong()
                }
                
                cropped
            } else {
                bitmap
            }

            // Store bitmap for HOT PATH (direct bitmap access)
            // CRITICAL: This is now managed by recycleLastBitmap()
            lastCapturedBitmap = croppedBitmap

            // Convert to Base64 for COLD PATH (React Native bridge)
            val base64 = bitmapToBase64(croppedBitmap, 80) // 80% JPEG quality

            // Save to cache (optional) - use a copy to avoid holding reference
            val cacheFile = saveBitmapToCache(croppedBitmap)

            val timestamp = System.currentTimeMillis()

            // Store last captured frame for native backend
            lastCapturedFrame = ScreenCaptureService.CapturedFrame(
                base64 = base64,
                width = image.width,
                height = image.height,
                timestamp = timestamp
            )

            Log.v(TAG, "📸 Frame captured: ${image.width}x${image.height} at $timestamp")

            // Log memory usage periodically (every 10th capture)
            if (timestamp % 10000 < 1000) { // Roughly every 10 seconds
                logBitmapMemoryUsage()
            }

            // Send to React Native (if available)
            val params = Arguments.createMap().apply {
                putString("base64", base64)
                putString("filePath", cacheFile?.absolutePath)
                putInt("width", image.width)
                putInt("height", image.height)
                putDouble("timestamp", timestamp.toDouble())
            }

            sendEvent("onScreenCaptured", params)

            // Resolve pending promise if there is one
            val promise = pendingCapturePromise
            if (promise != null) {
                pendingCapturePromise = null

                val result = Arguments.createMap().apply {
                    putString("base64", base64)
                    putInt("width", image.width)
                    putInt("height", image.height)
                    putDouble("timestamp", timestamp.toDouble())
                }

                Log.d(TAG, "✅ Frame captured and promise resolved: ${image.width}x${image.height}")
                promise.resolve(result)
            }

            // NOTE: We don't recycle croppedBitmap here because it's stored in lastCapturedBitmap
            // It will be recycled when the next frame is processed or when cleanup is called

        } catch (e: Exception) {
            Log.e(TAG, "Error processing captured image: ${e.message}", e)

            // Reject pending promise if there's an error
            val promise = pendingCapturePromise
            if (promise != null) {
                pendingCapturePromise = null
                promise.reject("PROCESSING_ERROR", "Error processing captured image: ${e.message}")
            }
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

    private fun connectServiceCallbacks() {
        Log.d(TAG, "🔗 Connecting service callbacks...")

        val service = com.allot.ScreenCaptureService.getInstance()
        if (service == null) {
            Log.w(TAG, "⚠️ Service not available for callbacks")
            return
        }

        // Set callback to trigger captures from service
        service.onTriggerCapture = {
            try {
                // Request next frame and trigger a display refresh to generate new frame
                shouldProcessNextFrame = true
                Log.v(TAG, "🎯 Frame requested by service")
                
                // Force a display refresh to generate a new frame
                virtualDisplay?.let { display ->
                    try {
                        // Trigger frame generation by forcing a display update
                        val windowManager = reactApplicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        val displayMetrics = DisplayMetrics()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
                        } else {
                            @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
                        }
                        display.resize(displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to trigger frame generation: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error triggering capture: ${e.message}")
            }
        }

        // COLD PATH: Base64 callback for React Native bridge
        service.onGetCapturedFrame = { lastCapturedFrame }

        // HOT PATH: Direct bitmap callback for service-to-service communication
        service.onGetCapturedBitmap = { lastCapturedBitmap }

        Log.d(TAG, "✅ Service callbacks connected (hot + cold paths)")

        // DON'T automatically enable native backend - respect existing setting
        // This allows LocalTextExtractionModule to control the backend mode
        Log.d(TAG, "🔧 Native backend setting preserved (not overridden)")

        Log.d(TAG, "✅ Service callbacks connected")
    }

    private fun disconnectServiceCallbacks() {
        ScreenCaptureService.getInstance()?.let { service ->
            service.onTriggerCapture = null
            service.onCaptureCallback = null
            Log.d(TAG, "🔌 Service callbacks disconnected")
        }
    }

    private fun stopCaptureLoop() {
        ScreenCaptureService.getInstance()?.stopCaptureLoop()
        disconnectServiceCallbacks()
        Log.d(TAG, "🛑 Capture loop stopped")
    }

    private fun cleanupCapture() {
        Log.d(TAG, "🧹 Starting capture cleanup...")
        
        // CRITICAL: Recycle bitmap before cleanup
        recycleLastBitmap()
        
        // Clean the entire bitmap pool
        cleanBitmapPool()
        
        virtualDisplay?.release()
        virtualDisplay = null

        imageReader?.close()
        imageReader = null

        mediaProjection?.stop()
        mediaProjection = null

        // Ensure background thread is properly terminated
        backgroundThread?.let { thread ->
            try {
                thread.quitSafely()
                // Wait for thread to finish (with timeout)
                thread.join(2000) // Wait max 2 seconds
                if (thread.isAlive) {
                    Log.w(TAG, "⚠️ Background thread didn't terminate gracefully, forcing interrupt")
                    thread.interrupt()
                }
            } catch (e: InterruptedException) {
                Log.w(TAG, "Thread cleanup interrupted: ${e.message}")
                Thread.currentThread().interrupt() // Restore interrupt status
            }
        }
        backgroundThread = null
        
        // Reset memory tracking
        totalBitmapMemory = 0L
        
        Log.d(TAG, "✅ Capture cleanup completed")
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
