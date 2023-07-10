package com.example.orderApp.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.orderApp.R
import com.example.orderApp.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageReceiver : FirebaseMessagingService() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel"
        private const val FOREGROUND_SERVICE_ID = 1
        private const val BEEP_FOREGROUND_SERVICE_ID = 2
    }

    private var mediaPlayer: MediaPlayer? = null


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification!!.title.toString(), remoteMessage.notification!!.body.toString())
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(
        title: String,
        message: String
    ): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification
        )
        remoteViews.setTextViewText(R.id.notification_title, title)
        remoteViews.setTextViewText(R.id.notification_message, message)
        return remoteViews
    }

    private fun showNotification(
        title: String,
        message: String
    ) {
        // Start the beep foreground service to play the sound
        startBeepForegroundService()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("notification","this is notification")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setContent(getCustomDesign(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "web_app",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        notificationManager.notify(0, builder.build())
    }

    private fun startBeepForegroundService() {
        val beepIntent = Intent(this, BeepForegroundService::class.java)
        ContextCompat.startForegroundService(this, beepIntent)
    }
}
