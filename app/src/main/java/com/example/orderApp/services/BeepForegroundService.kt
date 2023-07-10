package com.example.orderApp.services


import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.orderApp.R

class BeepForegroundService : Service() {

    companion object {
        private const val BEEP_FOREGROUND_SERVICE_ID = 2
        private const val BEEP_NOTIFICATION_CHANNEL_ID = "beep_notification_channel"
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playBeep()
        createNotificationChannel(BEEP_NOTIFICATION_CHANNEL_ID, "Beep Foreground Service")

        val notification = NotificationCompat.Builder(this, BEEP_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("New Order")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .build()

        startForeground(BEEP_FOREGROUND_SERVICE_ID, notification)
        return START_STICKY
    }

    private fun playBeep() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
