package com.allot

import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class AppDetectionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    
    companion object {
        const val TAG = "AppDetectionModule"
    }
    
    private var eventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter? = null
    
    init {
        // Set up app change listener
        AllotAccessibilityService.getInstance()?.onAppChanged = { packageName, isMonitored ->
            sendAppChangeEvent(packageName, isMonitored)
        }
    }
    
    override fun getName(): String = "AppDetectionModule"
    
    @ReactMethod
    fun getCurrentApp(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            val currentApp = service.getCurrentApp()
            val isMonitored = service.isCurrentAppMonitored()
            
            val result = Arguments.createMap().apply {
                putString("packageName", currentApp)
                putBoolean("isMonitored", isMonitored)
            }
            
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current app", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    @ReactMethod
    fun isMonitoredApp(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.resolve(false)
                return
            }
            
            promise.resolve(service.isCurrentAppMonitored())
        } catch (e: Exception) {
            Log.e(TAG, "Error checking monitored app", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    @ReactMethod
    fun performAutoScroll(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            val result = service.performAutoScroll()
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "Error performing auto scroll", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    private fun sendAppChangeEvent(packageName: String, isMonitored: Boolean) {
        try {
            if (eventEmitter == null) {
                eventEmitter = reactApplicationContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            }
            
            val params = Arguments.createMap().apply {
                putString("packageName", packageName)
                putBoolean("isMonitored", isMonitored)
            }
            
            eventEmitter?.emit("onAppChanged", params)
            Log.d(TAG, "📱 App change event sent: $packageName (monitored: $isMonitored)")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending app change event", e)
        }
    }
}
