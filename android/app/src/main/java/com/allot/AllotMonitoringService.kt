package com.allot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * AllotMonitoringService - Foreground service that keeps the app alive
 * 
 * This is the "dev mode simulator" for production:
 * - Runs as a foreground service (Android won't kill it)
 * - Shows persistent notification to user
 * - Keeps JS thread alive and responsive
 * - Makes monitoring feel smooth like dev mode
 * 
 * The notification is required by Android for foreground services.
 * It's actually a feature - users know the app is monitoring.
 */
class AllotMonitoringService : Service() {

    companion object {
        const val TAG = "AllotMonitoringService"
        const val CHANNEL_ID = "allot_monitoring_channel"
        const val NOTIFICATION_ID = 1001
        
        @Volatile
        var isRunning = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service starting...")
        
        val title = intent?.getStringExtra("title") ?: "Allot Monitoring Active"
        val message = intent?.getStringExtra("message") ?: "Scanning for harmful content"
        
        // Create and show the notification
        val notification = createNotification(title, message)
        startForeground(NOTIFICATION_ID, notification)
        
        isRunning = true
        Log.d(TAG, "Service started in foreground with notification")
        
        // START_STICKY means Android will restart the service if it's killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    private fun createNotification(title: String, message: String): Notification {
        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Allot Monitoring",
                NotificationManager.IMPORTANCE_LOW // Low = no sound/vibration
            ).apply {
                description = "Shows when Allot is actively monitoring for harmful content"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open the app when notification is tapped
        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_view) // Use system icon for now
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Can't be dismissed by swiping
            .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority = less intrusive
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
