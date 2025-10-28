package com.allot

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.*

class NotificationModule(reactContext: ReactApplicationContext) :
        ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val CHANNEL_ID = "allot_foreground_service"
        const val PERSISTENT_CHANNEL_ID = "allot_persistent_service"
        const val NOTIFICATION_ID = 1001
        const val PERSISTENT_NOTIFICATION_ID = 1002
        const val TAG = "AllotNotification"
    }

    override fun getName(): String {
        return "NotificationModule"
    }

    @ReactMethod
    fun createNotificationChannel(promise: Promise) {
        try {
            Log.d(TAG, "Creating notification channel...")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Allot Service"
                val descriptionText = "Allot background monitoring service - shows when app is actively monitoring content"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    setShowBadge(true)
                    enableLights(true)
                    enableVibration(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

                val notificationManager: NotificationManager =
                    reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                
                Log.d(TAG, "Notification channel created successfully with IMPORTANCE_DEFAULT")
                promise.resolve(true)
            } else {
                Log.d(TAG, "Notification channel not needed for API < 26")
                promise.resolve(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification channel: ${e.message}", e)
            promise.reject("NOTIFICATION_CHANNEL_ERROR", e.message)
        }
    }

    @ReactMethod
    fun showForegroundNotification(title: String, message: String, promise: Promise) {
        try {
            Log.d(TAG, "Showing REAL system notification: $title - $message")
            
            // Create channel first, then show notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Allot Service"
                val descriptionText = "Allot background monitoring service - shows when app is actively monitoring content"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    setShowBadge(true)
                    enableLights(true)
                    enableVibration(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }

                val notificationManager: NotificationManager =
                    reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            
            val notificationIntent = Intent(reactApplicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                reactApplicationContext,
                0,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            val notification = NotificationCompat.Builder(reactApplicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_sync) // System notification icon
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Makes it persistent
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Ensures visibility
                .setAutoCancel(false)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Check if notifications are enabled
            val areNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationManager.areNotificationsEnabled()
            } else {
                true
            }
            
            if (!areNotificationsEnabled) {
                Log.w(TAG, "Notifications are disabled by user")
                promise.reject("NOTIFICATIONS_DISABLED", "Notifications are disabled in device settings")
                return
            }
            
            notificationManager.notify(NOTIFICATION_ID, notification)
            
            Log.d(TAG, "REAL system notification posted successfully to status bar")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error in showForegroundNotification: ${e.message}", e)
            promise.reject("NOTIFICATION_ERROR", e.message)
        }
    }

    @ReactMethod
    fun hideForegroundNotification(promise: Promise) {
        try {
            Log.d(TAG, "Hiding system notification from status bar...")
            
            val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)
            
            Log.d(TAG, "System notification removed from status bar successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding notification: ${e.message}", e)
            promise.reject("NOTIFICATION_ERROR", e.message)
        }
    }

    @ReactMethod
    fun checkNotificationPermission(promise: Promise) {
        try {
            val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val areNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationManager.areNotificationsEnabled()
            } else {
                true // Assume enabled on older versions
            }
            
            Log.d(TAG, "System notifications enabled: $areNotificationsEnabled")
            promise.resolve(areNotificationsEnabled)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notification permission: ${e.message}", e)
            promise.reject("NOTIFICATION_CHECK_ERROR", e.message)
        }
    }

    @ReactMethod
    fun showPersistentNotification(title: String, message: String, promise: Promise) {
        try {
            Log.d(TAG, "Creating PERSISTENT notification: $title - $message")
            
            // Create persistent notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Allot Persistent Service"
                val descriptionText = "Always-on notification showing Allot monitoring status"
                val importance = NotificationManager.IMPORTANCE_LOW // Low importance so it's not intrusive
                val channel = NotificationChannel(PERSISTENT_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    setShowBadge(false)
                    enableLights(false)
                    enableVibration(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(null, null) // No sound for persistent notifications
                }

                val notificationManager: NotificationManager =
                    reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            
            val notificationIntent = Intent(reactApplicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntent = PendingIntent.getActivity(
                reactApplicationContext,
                0,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            val notification = NotificationCompat.Builder(reactApplicationContext, PERSISTENT_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Makes it persistent - cannot be dismissed by user
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority so it's not intrusive
                .setAutoCancel(false) // Cannot be auto-cancelled
                .setShowWhen(false) // Don't show timestamp
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(null) // No sound
                .setVibrate(null) // No vibration
                .build()

            val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Check if notifications are enabled
            val areNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notificationManager.areNotificationsEnabled()
            } else {
                true
            }
            
            if (!areNotificationsEnabled) {
                Log.w(TAG, "Notifications are disabled by user")
                promise.reject("NOTIFICATIONS_DISABLED", "Notifications are disabled in device settings")
                return
            }
            
            notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification)
            
            Log.d(TAG, "PERSISTENT notification posted successfully - cannot be dismissed by user")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing persistent notification: ${e.message}", e)
            promise.reject("PERSISTENT_NOTIFICATION_ERROR", e.message)
        }
    }

    @ReactMethod
    fun hidePersistentNotification(promise: Promise) {
        try {
            Log.d(TAG, "Hiding persistent notification...")
            
            val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(PERSISTENT_NOTIFICATION_ID)
            
            Log.d(TAG, "Persistent notification removed successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding persistent notification: ${e.message}", e)
            promise.reject("PERSISTENT_NOTIFICATION_ERROR", e.message)
        }
    }

    @ReactMethod
    fun openNotificationSettings(promise: Promise) {
        try {
            Log.d(TAG, "Opening notification settings...")
            
            val intent = Intent().apply {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        // Android 8.0+ - Open app-specific notification settings
                        action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, reactApplicationContext.packageName)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        // Android 5.0+ - Open app info page where user can enable notifications
                        action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = android.net.Uri.parse("package:${reactApplicationContext.packageName}")
                    }
                    else -> {
                        // Older versions - Open general notification settings
                        action = android.provider.Settings.ACTION_SETTINGS
                    }
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            reactApplicationContext.startActivity(intent)
            Log.d(TAG, "Notification settings opened successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening notification settings: ${e.message}", e)
            promise.reject("SETTINGS_ERROR", e.message)
        }
    }

    @ReactMethod
    fun requestNotificationPermission(promise: Promise) {
        try {
            Log.d(TAG, "Requesting notification permission...")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ - Request POST_NOTIFICATIONS permission
                val activity = reactApplicationContext.currentActivity
                if (activity != null) {
                    Log.d(TAG, "Requesting POST_NOTIFICATIONS permission for Android 13+")
                    
                    // Check if permission is already granted
                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        reactApplicationContext,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    
                    if (hasPermission) {
                        Log.d(TAG, "POST_NOTIFICATIONS permission already granted")
                        promise.resolve(true)
                        return
                    }
                    
                    // Request the permission using ActivityCompat
                    if (activity is androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback) {
                        Log.d(TAG, "Requesting POST_NOTIFICATIONS permission via system dialog")
                        androidx.core.app.ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1001 // This matches NOTIFICATION_PERMISSION_REQUEST_CODE in MainActivity
                        )
                        // Note: The result will be handled by the activity's onRequestPermissionsResult
                        // For now, we'll resolve immediately and let the UI check status later
                        promise.resolve(true)
                    } else {
                        Log.w(TAG, "Activity doesn't implement OnRequestPermissionsResultCallback, opening settings")
                        openNotificationSettings(promise)
                    }
                } else {
                    Log.w(TAG, "No current activity available for permission request, opening settings")
                    openNotificationSettings(promise)
                }
            } else {
                // Pre-Android 13 - Notifications are enabled by default, just check status
                val notificationManager = reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val areNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    notificationManager.areNotificationsEnabled()
                } else {
                    true
                }
                
                if (areNotificationsEnabled) {
                    Log.d(TAG, "Notifications already enabled for pre-Android 13")
                    promise.resolve(true)
                } else {
                    Log.d(TAG, "Notifications disabled, opening settings")
                    openNotificationSettings(promise)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting notification permission: ${e.message}", e)
            promise.reject("NOTIFICATION_PERMISSION_ERROR", e.message)
        }
    }
}