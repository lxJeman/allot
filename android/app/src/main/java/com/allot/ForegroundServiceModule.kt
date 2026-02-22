package com.allot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.*

/**
 * ForegroundServiceModule - Keeps the app process alive during monitoring
 * 
 * This is the MVP fix to simulate dev mode behavior in production:
 * - Starts a foreground service when monitoring begins
 * - Shows persistent notification: "Allot is monitoring for harmful content"
 * - Prevents Android from killing the JS process
 * - Makes the app feel responsive like dev mode
 * 
 * Why this matters:
 * - Without it: Android may pause/kill JS when app is backgrounded
 * - With it: Android treats app as active, JS stays alive
 * - Result: Smooth monitoring without rewriting detection logic
 */
class ForegroundServiceModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val TAG = "AllotForegroundService"
        const val CHANNEL_ID = "allot_monitoring_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun getName(): String {
        return "ForegroundServiceModule"
    }

    @ReactMethod
    fun startForegroundService(title: String, message: String, promise: Promise) {
        try {
            Log.d(TAG, "Starting foreground service: $title")
            
            val context = reactApplicationContext
            
            // Create notification channel (required for Android 8+)
            createNotificationChannel(context)
            
            // Start the foreground service
            val serviceIntent = Intent(context, AllotMonitoringService::class.java).apply {
                putExtra("title", title)
                putExtra("message", message)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            
            Log.d(TAG, "Foreground service started successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service: ${e.message}", e)
            promise.reject("FOREGROUND_SERVICE_ERROR", e.message)
        }
    }

    @ReactMethod
    fun stopForegroundService(promise: Promise) {
        try {
            Log.d(TAG, "Stopping foreground service")
            
            val context = reactApplicationContext
            val serviceIntent = Intent(context, AllotMonitoringService::class.java)
            context.stopService(serviceIntent)
            
            Log.d(TAG, "Foreground service stopped successfully")
            promise.resolve(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping foreground service: ${e.message}", e)
            promise.reject("FOREGROUND_SERVICE_ERROR", e.message)
        }
    }

    @ReactMethod
    fun isServiceRunning(promise: Promise) {
        try {
            val isRunning = AllotMonitoringService.isRunning
            Log.d(TAG, "Service running: $isRunning")
            promise.resolve(isRunning)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking service status: ${e.message}", e)
            promise.reject("FOREGROUND_SERVICE_ERROR", e.message)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Allot Monitoring",
                NotificationManager.IMPORTANCE_LOW // Low importance = no sound/vibration
            ).apply {
                description = "Shows when Allot is actively monitoring for harmful content"
                setShowBadge(false)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "Notification channel created")
        }
    }
}
