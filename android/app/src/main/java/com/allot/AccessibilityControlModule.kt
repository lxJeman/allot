package com.allot

import android.util.Log
import com.facebook.react.bridge.*

class AccessibilityControlModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "AllotAccessibilityControl"
    }

    override fun getName(): String {
        return "AccessibilityControlModule"
    }

    @ReactMethod
    fun isAccessibilityServiceRunning(promise: Promise) {
        try {
            val isRunning = AllotAccessibilityService.isServiceRunning()
            Log.d(TAG, "Accessibility service running: $isRunning")
            promise.resolve(isRunning)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking accessibility service: ${e.message}", e)
            promise.reject("SERVICE_CHECK_ERROR", e.message)
        }
    }

    @ReactMethod
    fun showOverlay(message: String, promise: Promise) {
        try {
            Log.d(TAG, "Requesting overlay show: $message")
            
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                Log.w(TAG, "Accessibility service not running")
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            service.showOverlay(message)
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing overlay: ${e.message}", e)
            promise.reject("OVERLAY_ERROR", e.message)
        }
    }

    @ReactMethod
    fun hideOverlay(promise: Promise) {
        try {
            Log.d(TAG, "Requesting overlay hide")
            
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                Log.w(TAG, "Accessibility service not running")
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            service.removeOverlay()
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding overlay: ${e.message}", e)
            promise.reject("OVERLAY_ERROR", e.message)
        }
    }

    @ReactMethod
    fun performAutoScroll(promise: Promise) {
        try {
            Log.d(TAG, "Requesting auto scroll")
            
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                Log.w(TAG, "Accessibility service not running")
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            service.performAutoScroll()
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error performing auto scroll: ${e.message}", e)
            promise.reject("SCROLL_ERROR", e.message)
        }
    }
}