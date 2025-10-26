package com.allot

import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.*

class ContentBlockingModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "AllotContentBlocking"
    }

    private var blurView: View? = null
    private var autoHideHandler: Handler? = null

    override fun getName(): String {
        return "ContentBlockingModule"
    }

    @ReactMethod
    fun showContentWarning(message: String, autoHideAfterSeconds: Int, promise: Promise) {
        try {
            Log.d(TAG, "Showing content warning: $message")
            
            val activity = reactApplicationContext.currentActivity
            if (activity == null) {
                Log.e(TAG, "No current activity available")
                promise.reject("NO_ACTIVITY", "Activity not available")
                return
            }

            // Step 1: Blur the current activity background
            blurActivityBackground(activity)
            
            // Step 2: Show accessibility overlay with warning
            val accessibilityService = AllotAccessibilityService.getInstance()
            if (accessibilityService != null) {
                accessibilityService.showContentWarningOverlay(message)
                Log.d(TAG, "Accessibility overlay shown")
            } else {
                Log.w(TAG, "Accessibility service not available, showing in-app warning only")
            }
            
            // Step 3: Auto-scroll (simulate scrolling to next content)
            accessibilityService?.performAutoScroll()
            
            // Step 4: Auto-hide after specified seconds
            if (autoHideAfterSeconds > 0) {
                autoHideHandler = Handler(Looper.getMainLooper())
                autoHideHandler?.postDelayed({
                    Log.d(TAG, "Auto-hiding content warning after ${autoHideAfterSeconds}s")
                    try {
                        val activity = reactApplicationContext.currentActivity
                        if (activity != null) {
                            removeBlurEffect(activity)
                        }
                        val accessibilityService = AllotAccessibilityService.getInstance()
                        accessibilityService?.removeOverlay()
                        Log.d(TAG, "Auto-hide completed successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during auto-hide: ${e.message}", e)
                    }
                }, (autoHideAfterSeconds * 1000).toLong())
            }
            
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing content warning: ${e.message}", e)
            promise.reject("CONTENT_WARNING_ERROR", e.message)
        }
    }

    @ReactMethod
    fun hideContentWarning(promise: Promise) {
        try {
            Log.d(TAG, "Hiding content warning")
            
            // Cancel auto-hide timer
            autoHideHandler?.removeCallbacksAndMessages(null)
            autoHideHandler = null
            
            val activity = reactApplicationContext.currentActivity
            if (activity != null) {
                // Remove blur effect
                removeBlurEffect(activity)
            }
            
            // Hide accessibility overlay
            val accessibilityService = AllotAccessibilityService.getInstance()
            accessibilityService?.removeOverlay()
            
            Log.d(TAG, "Content warning hidden successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding content warning: ${e.message}", e)
            promise.reject("CONTENT_WARNING_ERROR", e.message)
        }
    }

    @ReactMethod
    fun isContentWarningActive(promise: Promise) {
        try {
            val isActive = blurView != null
            Log.d(TAG, "Content warning active: $isActive")
            promise.resolve(isActive)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking content warning status: ${e.message}", e)
            promise.reject("CONTENT_WARNING_ERROR", e.message)
        }
    }

    private fun blurActivityBackground(activity: Activity) {
        try {
            val rootView = activity.window.decorView
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ - Use RenderEffect for real blur
                Log.d(TAG, "Using RenderEffect blur (Android 12+)")
                val blurEffect = RenderEffect.createBlurEffect(
                    25f, 25f, // blur radius
                    Shader.TileMode.CLAMP
                )
                rootView.setRenderEffect(blurEffect)
            } else {
                // Fallback for older Android - Semi-transparent overlay
                Log.d(TAG, "Using fallback dim overlay (Android < 12)")
                if (blurView == null) {
                    blurView = View(activity).apply {
                        setBackgroundColor(0x80000000.toInt()) // Semi-transparent black
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    (rootView as ViewGroup).addView(blurView)
                }
            }
            
            Log.d(TAG, "Background blur/dim applied successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying background blur: ${e.message}", e)
        }
    }

    private fun removeBlurEffect(activity: Activity) {
        try {
            val rootView = activity.window.decorView
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Remove RenderEffect
                rootView.setRenderEffect(null)
                Log.d(TAG, "RenderEffect blur removed")
            } else {
                // Remove overlay view
                blurView?.let { view ->
                    (rootView as ViewGroup).removeView(view)
                    blurView = null
                    Log.d(TAG, "Fallback dim overlay removed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing blur effect: ${e.message}", e)
        }
    }
}