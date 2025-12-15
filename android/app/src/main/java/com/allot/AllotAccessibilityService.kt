package com.allot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView

class AllotAccessibilityService : AccessibilityService() {
    
    companion object {
        const val TAG = "AllotAccessibility"
        private var instance: AllotAccessibilityService? = null
        
        fun getInstance(): AllotAccessibilityService? = instance
        
        fun isServiceRunning(): Boolean = instance != null
        
        // Monitored apps (social media)
        private val MONITORED_APPS = setOf(
            "com.zhiliaoapp.musically",    // TikTok
            "com.zhiliaoapp.musically.go", // TikTok Lite
            "com.ss.android.ugc.trill",    // TikTok Lite (some regions)
            "com.instagram.android",       // Instagram
            "com.instagram.lite",          // Instagram Lite
            "com.facebook.katana",         // Facebook
            "com.facebook.lite",           // Facebook Lite
            "com.twitter.android",         // Twitter
            "com.reddit.frontpage",        // Reddit
            "com.snapchat.android",        // Snapchat
            "com.whatsapp",                // WhatsApp
            "com.discord",                 // Discord
            "com.pinterest",               // Pinterest
            "com.linkedin.android"         // LinkedIn
        )
    }
    
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var currentApp: String? = null
    private var isMonitoredApp: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    
    // Callback for app changes
    var onAppChanged: ((String, Boolean) -> Unit)? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        Log.d(TAG, "")
        Log.d(TAG, "✅ ═══════════════════════════════════════")
        Log.d(TAG, "✅ ACCESSIBILITY SERVICE CONNECTED")
        Log.d(TAG, "✅ ═══════════════════════════════════════")
        Log.d(TAG, "✅ Service instance set: ${instance != null}")
        Log.d(TAG, "✅ WindowManager ready: ${windowManager != null}")
        Log.d(TAG, "✅ Service is now available for content blocking")
        Log.d(TAG, "✅ ═══════════════════════════════════════")
        Log.d(TAG, "")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        removeOverlay()
        onAppChanged = null
        Log.d(TAG, "❌ Accessibility service destroyed")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            // Detect app changes
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val packageName = it.packageName?.toString()
                if (packageName != null && packageName != currentApp) {
                    currentApp = packageName
                    isMonitoredApp = MONITORED_APPS.contains(packageName)
                    
                    val appName = when (packageName) {
                        "com.zhiliaoapp.musically" -> "TikTok"
                        "com.zhiliaoapp.musically.go" -> "TikTok Lite"
                        "com.ss.android.ugc.trill" -> "TikTok Lite"
                        "com.instagram.android" -> "Instagram"
                        "com.instagram.lite" -> "Instagram Lite"
                        "com.facebook.katana" -> "Facebook"
                        "com.facebook.lite" -> "Facebook Lite"
                        "com.twitter.android" -> "Twitter"
                        "com.reddit.frontpage" -> "Reddit"
                        "com.snapchat.android" -> "Snapchat"
                        "com.whatsapp" -> "WhatsApp"
                        "com.discord" -> "Discord"
                        "com.pinterest" -> "Pinterest"
                        "com.linkedin.android" -> "LinkedIn"
                        else -> packageName
                    }
                    
                    if (isMonitoredApp) {
                        Log.d(TAG, "📱 Switched to monitored app: $appName")
                    } else {
                        Log.v(TAG, "📱 Switched to: $appName (not monitored)")
                    }
                    
                    // Notify callback
                    onAppChanged?.invoke(packageName, isMonitoredApp)
                }
            }
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "⚠️ Accessibility service interrupted")
    }
    
    fun getCurrentApp(): String? = currentApp
    
    fun isCurrentAppMonitored(): Boolean = isMonitoredApp
    
    fun showOverlay(message: String = "Allot Active") {
        try {
            if (overlayView != null) {
                Log.d(TAG, "Overlay already showing")
                return
            }
            
            Log.d(TAG, "Showing accessibility overlay: $message")
            
            // Create a simple overlay view
            overlayView = TextView(this).apply {
                text = message
                setBackgroundColor(0x80000000.toInt()) // Semi-transparent black
                setTextColor(0xFFFFFFFF.toInt()) // White text
                textSize = 16f
                setPadding(20, 10, 20, 10)
            }
            
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = 100 // Offset from top
            }
            
            windowManager?.addView(overlayView, params)
            Log.d(TAG, "Overlay added successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing overlay: ${e.message}", e)
        }
    }

    fun showContentWarningOverlay(message: String) {
        try {
            if (overlayView != null) {
                removeOverlay() // Remove existing overlay first
            }
            
            Log.d(TAG, "Showing content warning overlay: $message")
            
            // Create a more prominent warning overlay
            overlayView = LayoutInflater.from(this).inflate(
                android.R.layout.simple_list_item_2, null
            ).apply {
                findViewById<TextView>(android.R.id.text1)?.apply {
                    text = "⚠️ HARMFUL CONTENT DETECTED"
                    setTextColor(0xFFFF4444.toInt()) // Red text
                    textSize = 18f
                }
                findViewById<TextView>(android.R.id.text2)?.apply {
                    text = message
                    setTextColor(0xFFFFFFFF.toInt()) // White text
                    textSize = 14f
                }
                setBackgroundColor(0xCC000000.toInt()) // More opaque black
                setPadding(40, 30, 40, 30)
            }
            
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }
            
            windowManager?.addView(overlayView, params)
            Log.d(TAG, "Content warning overlay added successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing content warning overlay: ${e.message}", e)
        }
    }
    
    fun removeOverlay() {
        try {
            overlayView?.let { view ->
                Log.d(TAG, "Removing accessibility overlay")
                windowManager?.removeView(view)
                overlayView = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay: ${e.message}", e)
        }
    }
    
    fun performAutoScroll(): Boolean {
        return try {
            Log.d(TAG, "⏭️  Performing auto scroll...")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Get screen dimensions
                val displayMetrics = DisplayMetrics()
                windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                val screenHeight = displayMetrics.heightPixels
                val screenWidth = displayMetrics.widthPixels
                
                // Create swipe gesture (swipe up to scroll down)
                val swipePath = Path()
                val startX = screenWidth / 2f
                val startY = screenHeight * 0.7f  // Start at 70% down
                val endY = screenHeight * 0.3f    // End at 30% down
                
                swipePath.moveTo(startX, startY)
                swipePath.lineTo(startX, endY)
                
                val gestureBuilder = GestureDescription.Builder()
                val strokeDescription = GestureDescription.StrokeDescription(
                    swipePath,
                    0,      // Start time
                    300     // Duration (300ms for smooth scroll)
                )
                
                gestureBuilder.addStroke(strokeDescription)
                val gesture = gestureBuilder.build()
                
                // Dispatch gesture
                val result = dispatchGesture(gesture, object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        Log.d(TAG, "✅ Auto scroll completed")
                    }
                    
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        Log.w(TAG, "⚠️ Auto scroll cancelled")
                    }
                }, null)
                
                if (result) {
                    Log.d(TAG, "✅ Auto scroll gesture dispatched")
                } else {
                    Log.w(TAG, "⚠️ Failed to dispatch auto scroll gesture")
                }
                
                result
            } else {
                Log.w(TAG, "⚠️ Auto scroll requires Android N (API 24) or higher")
                // Fallback: Try to use back action as scroll is not available
                performGlobalAction(GLOBAL_ACTION_BACK)
                true
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error performing auto scroll: ${e.message}", e)
            false
        }
    }
    
    fun performScrollDown(): Boolean {
        return performAutoScroll()
    }
}