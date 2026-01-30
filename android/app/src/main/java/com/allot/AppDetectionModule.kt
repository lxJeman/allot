package com.allot

import android.content.Intent
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
                Log.w(TAG, "🚫 getCurrentApp: Accessibility service is not running")
                
                // Return default "unknown" app info instead of rejecting
                val result = Arguments.createMap().apply {
                    putString("packageName", "unknown")
                    putString("appName", "Unknown")
                    putBoolean("isMonitored", false)
                    putLong("timestamp", System.currentTimeMillis())
                    putBoolean("serviceAvailable", false)
                }
                
                promise.resolve(result)
                return
            }
            
            val currentApp = service.getCurrentApp()
            val isMonitored = service.isCurrentAppMonitored()
            val appName = service.getAppDisplayName(currentApp)
            
            Log.d(TAG, "📱 getCurrentApp: $appName ($currentApp) - Monitored: $isMonitored")
            
            val result = Arguments.createMap().apply {
                putString("packageName", currentApp ?: "unknown")
                putString("appName", appName)
                putBoolean("isMonitored", isMonitored)
                putLong("timestamp", System.currentTimeMillis())
                putBoolean("serviceAvailable", true)
            }
            
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting current app", e)
            
            // Return default instead of rejecting
            val result = Arguments.createMap().apply {
                putString("packageName", "error")
                putString("appName", "Error")
                putBoolean("isMonitored", false)
                putLong("timestamp", System.currentTimeMillis())
                putBoolean("serviceAvailable", false)
            }
            
            promise.resolve(result)
        }
    }
    
    @ReactMethod
    fun isMonitoredApp(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                Log.w(TAG, "🚫 isMonitoredApp: Service not available - defaulting to false")
                promise.resolve(false) // CRITICAL: Default to false when service unavailable
                return
            }
            
            val isMonitored = service.isCurrentAppMonitored()
            val currentApp = service.getCurrentApp()
            val appName = service.getAppDisplayName(currentApp)
            
            Log.d(TAG, "🔍 isMonitoredApp: $appName - $isMonitored")
            promise.resolve(isMonitored)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking monitored app", e)
            promise.resolve(false) // CRITICAL: Default to false on error
        }
    }
    
    @ReactMethod
    fun isAccessibilityServiceEnabled(promise: Promise) {
        try {
            val isEnabled = AllotAccessibilityService.isServiceRunning()
            Log.d(TAG, "🔍 Accessibility service enabled: $isEnabled")
            promise.resolve(isEnabled)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking accessibility service status", e)
            promise.resolve(false)
        }
    }
    
    @ReactMethod
    fun openAccessibilitySettings(promise: Promise) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            reactApplicationContext.startActivity(intent)
            Log.d(TAG, "📱 Opened accessibility settings")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error opening accessibility settings", e)
            promise.resolve(false)
        }
    }
    
    @ReactMethod
    fun getServiceStatus(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            val isRunning = service != null
            
            val result = Arguments.createMap().apply {
                putBoolean("isRunning", isRunning)
                putString("currentApp", service?.getCurrentApp() ?: "unknown")
                putString("currentAppName", service?.getAppDisplayName(service?.getCurrentApp()) ?: "Unknown")
                putBoolean("isMonitored", service?.isCurrentAppMonitored() ?: false)
                putLong("timestamp", System.currentTimeMillis())
            }
            
            Log.d(TAG, "📊 Service status: running=$isRunning")
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting service status", e)
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
    
    @ReactMethod
    fun getMonitoredApps(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            val monitoredApps = service.getMonitoredAppsList()
            Log.d(TAG, "📋 getMonitoredApps: ${monitoredApps.size} apps configured")
            
            val result = Arguments.createArray()
            monitoredApps.forEach { app ->
                val appMap = Arguments.createMap().apply {
                    putString("packageName", app.packageName)
                    putString("displayName", app.displayName)
                }
                result.pushMap(appMap)
            }
            
            promise.resolve(result)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting monitored apps", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    @ReactMethod
    fun startAppDetectionLogging(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            service.enableDebugLogging(true)
            Log.i(TAG, "🔍 APP DETECTION DEBUG LOGGING ENABLED")
            Log.i(TAG, "═══════════════════════════════════════")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error starting app detection logging", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    @ReactMethod
    fun stopAppDetectionLogging(promise: Promise) {
        try {
            val service = AllotAccessibilityService.getInstance()
            if (service == null) {
                promise.reject("SERVICE_NOT_RUNNING", "Accessibility service is not running")
                return
            }
            
            service.enableDebugLogging(false)
            Log.i(TAG, "🔇 APP DETECTION DEBUG LOGGING DISABLED")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error stopping app detection logging", e)
            promise.reject("ERROR", e.message)
        }
    }
    
    private fun sendAppChangeEvent(packageName: String, isMonitored: Boolean) {
        try {
            if (eventEmitter == null) {
                eventEmitter = reactApplicationContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            }
            
            val service = AllotAccessibilityService.getInstance()
            val appName = service?.getAppDisplayName(packageName) ?: packageName
            
            val params = Arguments.createMap().apply {
                putString("packageName", packageName)
                putString("appName", appName)
                putBoolean("isMonitored", isMonitored)
                putLong("timestamp", System.currentTimeMillis())
            }
            
            eventEmitter?.emit("onAppChanged", params)
            
            if (isMonitored) {
                Log.i(TAG, "🎯 APP CHANGE → MONITORED: $appName ($packageName)")
            } else {
                Log.d(TAG, "📱 APP CHANGE → $appName ($packageName)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error sending app change event", e)
        }
    }
}
