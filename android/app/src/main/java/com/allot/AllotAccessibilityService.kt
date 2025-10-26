package com.allot

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.PixelFormat
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
    }
    
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        Log.d(TAG, "Accessibility service connected")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        removeOverlay()
        Log.d(TAG, "Accessibility service destroyed")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events here
        event?.let {
            Log.v(TAG, "Accessibility event: ${it.eventType} from ${it.packageName}")
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }
    
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
    
    fun performAutoScroll() {
        try {
            Log.d(TAG, "Performing auto scroll")
            
            // TODO: Implement gesture-based scrolling
            // This would require API 24+ for gesture support
            Log.d(TAG, "Auto scroll functionality - to be implemented")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error performing auto scroll: ${e.message}", e)
        }
    }
}