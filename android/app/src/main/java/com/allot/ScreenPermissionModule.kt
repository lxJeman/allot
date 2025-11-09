package com.allot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.ActivityEventListener

class ScreenPermissionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {

    companion object {
        const val SCREEN_CAPTURE_REQUEST_CODE = 1001
        const val ACCESSIBILITY_REQUEST_CODE = 1002
        const val OVERLAY_REQUEST_CODE = 1003
        const val TAG = "AllotPermissions"
    }

    private var screenCapturePromise: Promise? = null

    init {
        // Register to receive activity results
        reactContext.addActivityEventListener(this)
    }

    override fun getName(): String {
        return "ScreenPermissionModule"
    }

    @ReactMethod
    fun checkScreenCapturePermission(promise: Promise) {
        try {
            Log.d(TAG, "Checking screen capture permission...")
            
            val mediaProjectionManager = reactApplicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
            val isAvailable = mediaProjectionManager != null
            
            Log.d(TAG, "Screen capture available: $isAvailable")
            promise.resolve(isAvailable)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking screen capture permission: ${e.message}", e)
            promise.reject("PERMISSION_CHECK_ERROR", e.message)
        }
    }

    @ReactMethod
    fun requestScreenCapturePermission(promise: Promise) {
        try {
            Log.d(TAG, "Requesting screen capture permission...")
            
            // Guard against concurrent requests
            val activity = reactApplicationContext.currentActivity
            if (activity == null) {
                promise.reject("NO_ACTIVITY", "Activity not available")
                return
            }
            if (screenCapturePromise != null) {
                promise.reject("ALREADY_REQUESTING", "Permission request already in progress")
                return
            }
            screenCapturePromise = promise

            val mediaProjectionManager = reactApplicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
            if (mediaProjectionManager == null) {
                screenCapturePromise = null
                promise.reject("SERVICE_UNAVAILABLE", "MediaProjection service not available")
                return
            }
            
            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
            Log.d(TAG, "Starting screen capture permission activity...")
            activity.startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE)
        } catch (e: Exception) {
            screenCapturePromise = null
            Log.e(TAG, "Error requesting screen capture permission: ${e.message}", e)
            promise.reject("PERMISSION_REQUEST_ERROR", e.message)
        }
    }

    @ReactMethod
    fun checkAccessibilityPermission(promise: Promise) {
        try {
            Log.d(TAG, "Checking accessibility permission...")
            
            val enabledServices = Settings.Secure.getString(
                reactApplicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val packageName = reactApplicationContext.packageName
            val isEnabledInSettings = enabledServices?.contains(packageName) ?: false
            val isServiceRunning = AllotAccessibilityService.isServiceRunning()
            
            // Service is truly enabled only if both conditions are met
            val isEnabled = isEnabledInSettings && isServiceRunning
            
            Log.d(TAG, "Accessibility check for $packageName:")
            Log.d(TAG, "  - Enabled in Settings: $isEnabledInSettings")
            Log.d(TAG, "  - Service Running: $isServiceRunning")
            Log.d(TAG, "  - Final Status: $isEnabled")
            Log.d(TAG, "Enabled services: $enabledServices")
            
            promise.resolve(isEnabled)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking accessibility permission: ${e.message}", e)
            promise.reject("PERMISSION_CHECK_ERROR", e.message)
        }
    }

    @ReactMethod
    fun requestAccessibilityPermission(promise: Promise) {
        try {
            Log.d(TAG, "Requesting accessibility permission...")
            
            val activity = reactApplicationContext.currentActivity
            if (activity == null) {
                Log.e(TAG, "No current activity available")
                promise.reject("NO_ACTIVITY", "Activity not available")
                return
            }

            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            
            // Store promise for later resolution
            accessibilityPromise = promise
            Log.d(TAG, "Opening accessibility settings...")
            activity.startActivityForResult(intent, ACCESSIBILITY_REQUEST_CODE)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting accessibility permission: ${e.message}", e)
            promise.reject("PERMISSION_REQUEST_ERROR", e.message)
        }
    }

    @ReactMethod
    fun checkOverlayPermission(promise: Promise) {
        try {
            Log.d(TAG, "Checking overlay permission...")
            
            val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Settings.canDrawOverlays(reactApplicationContext)
                } catch (e: Exception) {
                    Log.w(TAG, "canDrawOverlays not available on this device: ${e.message}")
                    false
                }
            } else {
                true // Permission not needed on older versions
            }
            
            Log.d(TAG, "Overlay permission: $hasPermission (API level: ${Build.VERSION.SDK_INT})")
            promise.resolve(hasPermission)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking overlay permission: ${e.message}", e)
            promise.reject("PERMISSION_CHECK_ERROR", e.message)
        }
    }

    @ReactMethod
    fun requestOverlayPermission(promise: Promise) {
        try {
            Log.d(TAG, "Requesting overlay permission...")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activity = reactApplicationContext.currentActivity
                if (activity == null) {
                    Log.e(TAG, "No current activity available")
                    promise.reject("NO_ACTIVITY", "Activity not available")
                    return
                }

                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${reactApplicationContext.packageName}")
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    
                    overlayPromise = promise
                    Log.d(TAG, "Opening overlay permission settings...")
                    activity.startActivityForResult(intent, OVERLAY_REQUEST_CODE)
                } catch (e: Exception) {
                    Log.w(TAG, "Overlay permission not available on this device: ${e.message}")
                    promise.resolve(false)
                }
            } else {
                Log.d(TAG, "Overlay permission not needed on API < 23")
                promise.resolve(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting overlay permission: ${e.message}", e)
            promise.reject("PERMISSION_REQUEST_ERROR", e.message)
        }
    }

    @ReactMethod
    fun getAllPermissionsStatus(promise: Promise) {
        try {
            Log.d(TAG, "Getting all permissions status...")
            
            val permissions = WritableNativeMap()
            
            // Screen capture - check if service is available
            val mediaProjectionManager = reactApplicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
            permissions.putBoolean("screenCapture", mediaProjectionManager != null)
            
            // Accessibility - check both Settings AND actual service instance
            val enabledServices = Settings.Secure.getString(
                reactApplicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val isEnabledInSettings = enabledServices?.contains(reactApplicationContext.packageName) ?: false
            val isServiceRunning = AllotAccessibilityService.isServiceRunning()
            
            // Service is truly enabled only if both conditions are met
            val isAccessibilityEnabled = isEnabledInSettings && isServiceRunning
            
            Log.d(TAG, "Accessibility check - Settings: $isEnabledInSettings, Service Running: $isServiceRunning, Final: $isAccessibilityEnabled")
            permissions.putBoolean("accessibility", isAccessibilityEnabled)
            
            // Overlay
            val hasOverlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    Settings.canDrawOverlays(reactApplicationContext)
                } catch (e: Exception) {
                    Log.w(TAG, "canDrawOverlays not available: ${e.message}")
                    false
                }
            } else {
                true
            }
            permissions.putBoolean("overlay", hasOverlayPermission)
            
            Log.d(TAG, "Permissions status - Screen: ${mediaProjectionManager != null}, Accessibility: $isAccessibilityEnabled, Overlay: $hasOverlayPermission")
            promise.resolve(permissions)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting permissions status: ${e.message}", e)
            promise.reject("PERMISSION_CHECK_ERROR", e.message)
        }
    }

    // Store promises for activity result handling  
    private var accessibilityPromise: Promise? = null
    private var overlayPromise: Promise? = null

    // ActivityEventListener implementation
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode, data=${data != null}")
        
        when (requestCode) {
            SCREEN_CAPTURE_REQUEST_CODE -> {
                screenCapturePromise?.let { promise ->
                    val granted = resultCode == Activity.RESULT_OK && data != null
                    if (granted) {
                        // Store real intent & code in the shared holder (do not serialize)
                        ProjectionPermissionHolder.pendingResultCode = resultCode
                        ProjectionPermissionHolder.pendingDataIntent = Intent(data) // copy for safety

                        Log.d(TAG, "✅ Screen capture permission GRANTED - stored in holder")
                        promise.resolve(Arguments.createMap().apply {
                            putBoolean("granted", true)
                            putInt("resultCode", resultCode)
                        })
                    } else {
                        // Clear any existing
                        ProjectionPermissionHolder.clear()
                        Log.d(TAG, "❌ Screen capture permission DENIED")
                        promise.resolve(Arguments.createMap().apply {
                            putBoolean("granted", false)
                        })
                    }
                    screenCapturePromise = null
                } ?: run {
                    Log.w(TAG, "No stored promise for screen capture result")
                    // Still store intent for possible manual start
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ProjectionPermissionHolder.pendingResultCode = resultCode
                        ProjectionPermissionHolder.pendingDataIntent = Intent(data)
                        Log.d(TAG, "Stored screen capture permission without promise")
                    }
                }
            }
            ACCESSIBILITY_REQUEST_CODE -> {
                accessibilityPromise?.let { promise ->
                    // Check if accessibility is now enabled
                    val enabledServices = Settings.Secure.getString(
                        reactApplicationContext.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    )
                    val isEnabled = enabledServices?.contains(reactApplicationContext.packageName) ?: false
                    
                    Log.d(TAG, "Accessibility permission result: $isEnabled")
                    promise.resolve(isEnabled)
                    accessibilityPromise = null
                }
            }
            OVERLAY_REQUEST_CODE -> {
                overlayPromise?.let { promise ->
                    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            Settings.canDrawOverlays(reactApplicationContext)
                        } catch (e: Exception) {
                            Log.w(TAG, "canDrawOverlays not available: ${e.message}")
                            false
                        }
                    } else {
                        true
                    }
                    
                    Log.d(TAG, "Overlay permission result: $hasPermission")
                    promise.resolve(hasPermission)
                    overlayPromise = null
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        // Required by ActivityEventListener interface
    }
}