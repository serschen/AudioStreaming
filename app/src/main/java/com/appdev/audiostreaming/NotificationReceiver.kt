package com.appdev.audiostreaming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.appdev.audiostreaming.lukas.AudioPlayerService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        if (action != null) {
            val serviceIntent = Intent(p0, AudioPlayerService::class.java)
            serviceIntent.action = action
            p0?.startService(serviceIntent)
        }
    }
}
