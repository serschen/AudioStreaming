package com.example.`as`

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.net.toUri
import com.appdev.audiostreaming.MainActivity
import com.appdev.audiostreaming.R
import com.google.firebase.storage.FirebaseStorage

class AudioPlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        var time: Int = 0
        var isPlaying = false
        var uri: Uri? = null
    }

    private lateinit var player: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            "prev" -> {

            }
            "back" -> {
                player.stop()
                time -= 15000
                if (time < 0) {
                    time = 0
                }
                player = MediaPlayer.create(this, uri)
                player.seekTo(time)
                player.start()
            }
            "play" -> {
                play()
            }
            "forw" -> {
                player.stop()
                time += 15000
                if (time > player.duration) {
                    time = player.duration
                }
                player = MediaPlayer.create(this, uri)
                player.seekTo(time)
                player.start()
            }
            "next" -> {

            }
            "chan" -> {
                val extras = intent.extras

                if (extras != null) {
                    val song: Map<String, Any> = extras.getSerializable("map") as Map<String, Any>

                    val path = song["path"]?.toString() ?: ""

                    if (path != "") {
                        val storage = FirebaseStorage.getInstance()
                        storage.reference.child(path).downloadUrl.addOnSuccessListener {
                            uri = it
                            play()
                        }
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun play(){
        if (isPlaying) {
            time = player.currentPosition
            isPlaying = false
            player.stop()
        } else {
            player = MediaPlayer.create(this, uri)
            player.seekTo(time)
            isPlaying = true
            player.start()
        }
    }
}
