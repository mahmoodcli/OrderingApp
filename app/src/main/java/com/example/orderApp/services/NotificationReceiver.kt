package com.example.orderApp.services
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.content.ContextCompat
import com.example.orderApp.R
import com.example.orderApp.activities.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Play the beep sound
        playBeepSound(context)
    }

    private fun playBeepSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.beep)
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
        mediaPlayer.start()
    }
}
