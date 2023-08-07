package com.appdev.audiostreaming

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.appdev.audiostreaming.*
import com.google.firebase.storage.FirebaseStorage

class AudioPlayerService : Service() {

    private lateinit var player: MediaPlayer

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
    }

    companion object {
        var uri: Uri? = null
        var time = 0
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            "back" -> {
                back()
            }
            "play" -> {
                play(intent)
            }
            "pause" -> {
                pause()
            }
            "forward" -> {
                forward()
            }
        }
        return START_NOT_STICKY
    }

    private fun play(intent: Intent) {
        val path = intent.extras?.get("path")?.toString() ?: ""
        if (path != "") {
            val storage = FirebaseStorage.getInstance()
            storage.reference.child(path).downloadUrl.addOnSuccessListener {
                uri = it
                player.reset()
                player.setDataSource(this, it!!)
                player.prepare()
                player.setOnCompletionListener {
                }
                player.start()
            }
        }
    }

    private fun pause(){
        player.pause()
    }

    private fun back(){
        //TODO
    }

    private fun forward(){
        //TODO
    }
}
