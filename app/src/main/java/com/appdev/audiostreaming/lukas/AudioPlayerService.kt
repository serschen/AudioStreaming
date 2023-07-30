package com.example.`as`

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdev.audiostreaming.MainActivity
import com.appdev.audiostreaming.R
import com.appdev.audiostreaming.SongAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AudioPlayerService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        var title: String = ""
        var artist: String = ""
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

    private fun play() {
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
        updateUI()
    }

    private fun updateUI() {
        val intent = Intent(MainActivity.ACTION_UPDATE_UI)
        intent.putExtra("isPlaying", isPlaying)
        intent.putExtra("title", title)
        intent.putExtra("artist", artist)
        sendBroadcast(intent)
    }

}
