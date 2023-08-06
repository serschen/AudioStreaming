package com.appdev.audiostreaming.lukas

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.appdev.audiostreaming.MainActivity
import com.appdev.audiostreaming.SongInfoFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.appdev.audiostreaming.*
import com.google.firebase.storage.FirebaseStorage

class AudioPlayerService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
    }

    companion object {
        var title: String = ""
        var artist: String = ""
        var time: Int = 0
        var isPlaying = false
        var uri: Uri? = null
        var position = 0
    }

    private lateinit var player: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            "prev" -> {
                previous()
            }
            "back" -> {
                back()
            }
            "play" -> {
                play()
            }
            "forw" -> {
                forward()
            }
            "next" -> {
                next()
            }
            "chan" -> {
                change(intent)
            }
        }
        return START_NOT_STICKY
    }

    private fun previous() {
        position = (position - 1 + Songs.itemList.size) % Songs.itemList.size
        playSong(position)
    }

    private fun back(){
        time -= 15000
        if(time <= 0){
            time = 0
        }
        player.seekTo(time)
        player.start()
    }

    private fun play() {
        if (isPlaying) {
            time = player.currentPosition
            isPlaying = false
            player.pause()
        } else {
            player = MediaPlayer.create(this, uri)
            player.seekTo(time)
            isPlaying = true
            player.start()
        }
        updateUI()
    }

    private fun forward(){
        player.pause()
        time += 15000
        if (time > player.duration) {
            time = player.duration
        }
        player.seekTo(time)
        player.start()
    }

    private fun change(intent: Intent){
        val extras = intent.extras

        if (extras != null) {
            val song: Map<String, Any> = extras.getSerializable("map") as Map<String, Any>

            title = song["name"].toString()
            artist = song["artistName"].toString()

            val path = song["path"]?.toString() ?: ""

            if (path != "") {
                val storage = FirebaseStorage.getInstance()
                storage.reference.child(path).downloadUrl.addOnSuccessListener {
                    uri = it
                    position = extras.getInt("pos")
                    play()
                }
            }
        }
        updateUI()
    }

    private fun playSong(position: Int) {
        if (position >= 0 && position < Songs.itemList.size) {
            val song = Songs.itemList[position]
            val path = song["path"]?.toString() ?: ""
            if (path != "") {
                val storage = FirebaseStorage.getInstance()
                storage.reference.child(path).downloadUrl.addOnSuccessListener {
                    uri = it
                    Companion.position = position
                    player.reset()
                    player.setDataSource(this, uri!!)
                    player.prepare()
                    player.setOnCompletionListener {
                        next()
                    }
                    player.start()
                    updateUI()
                }
            }
        }
    }

    private fun next() {
        position = (position + 1) % Songs.itemList.size
        playSong(position)
    }

    private fun updateUI() {
        val intent = Intent(MainActivity.ACTION_UPDATE_UI)
        intent.putExtra("isPlaying", isPlaying)
        intent.putExtra("title", title)
        intent.putExtra("artist", artist)
        sendBroadcast(intent)

        val intent2 = Intent(SongInfoFragment.ACTION_UPDATE_UI) // Corrected to use intent2 instead of intent
        intent2.putExtra("isPlaying", isPlaying)
        intent2.putExtra("title", title)
        intent2.putExtra("artist", artist)
        sendBroadcast(intent2)
    }

}
