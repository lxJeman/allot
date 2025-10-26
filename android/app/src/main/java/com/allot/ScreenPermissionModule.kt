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

class ScreenPermissionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val SCREEN_CAPTURE_REQUEST_CODE = 1001
        const val ACCESSIBILITY_REQUEST_CODE = 1002
        const val OVERLAY_REQUEST_CODE = 1003
        const val TAG = "AllotPermissions"
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
            
            val activity = reactApplicationContext.currentActivity
            if (activity == null) {
                Log.e(TAG, "No current activity available")
                promise.reject("NO_ACTIVITY", "Activity not available")
                return
            }

            val mediaProjectionManager = reactApplicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
            if (mediaProjectionManager == null) {
                Log.e(TAG, "MediaProjectionManager not available")
                promise.reject("SERVICE_UNAVAILABLE", "MediaProjection service not available")
                return
            }

            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
            
            // Store promise for later resolution
            screenCapturePromise = promise
            Log.d(TAG, "Starting screen capture permission activity...")
            activity.startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE)
        } catch (e: Exception) {
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
            val isEnabled = enabledServices?.contains(packageName) ?: false
            
            Log.d(TAG, "Accessibility enabled for $packageName: $isEnabled")
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
            
            // Accessibility
            val enabledServices = Settings.Secure.getString(
                reactApplicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val isAccessibilityEnabled = enabledServices?.contains(reactApplicationContext.packageName) ?: false
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
    private var screenCapturePromise: Promise? = null
    private var accessibilityPromise: Promise? = null
    private var overlayPromise: Promise? = null

    // Handle activity results (this would be called from MainActivity)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        
        when (requestCode) {
            SCREEN_CAPTURE_REQUEST_CODE -> {
                screenCapturePromise?.let { promise ->
                    val granted = resultCode == Activity.RESULT_OK
                    Log.d(TAG, "Screen capture permission result: $granted")
                    promise.resolve(granted)
                    screenCapturePromise = null
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
}