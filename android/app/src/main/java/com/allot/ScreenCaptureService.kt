package com.allot

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ScreenCaptureService : Service() {
    
    companion object {
        const val TAG = "ScreenCaptureService"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "screen_capture_service"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ScreenCaptureService created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "ScreenCaptureService started")
        
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        return START_STICKY // Restart if killed by system
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ScreenCaptureService destroyed")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Screen Capture Service"
            val descriptionText = "Allot is capturing screen content for analysis"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Allot Screen Analysis")
            .setContentText("Analyzing screen content for harmful material")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setShowWhen(false)
            .build()
    }
}