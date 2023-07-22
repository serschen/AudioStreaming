package com.appdev.audiostreaming

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable


class PlayerActivity : AppCompatActivity() {
    var txtName:TextView? = null
    var txtArtist:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        txtName = findViewById(R.id.txtSongNamePlayer)
        txtArtist = findViewById(R.id.txtArtistPlayer)

        val extras = intent.extras

        if(extras != null){
           val song:Map<String, Any> = extras.getSerializable("map") as Map<String, Any>

            txtName?.text = song["name"]?.toString() ?: "Name not found"
            txtArtist?.text = song["artistName"]?.toString() ?: "Artist not found"

            val path = song["path"]?.toString() ?: ""

            if(path != "") {
                val storage = FirebaseStorage.getInstance()
                storage.reference.child(path).downloadUrl.addOnSuccessListener {
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(it.toString())
                    mediaPlayer.setOnPreparedListener { player ->
                        player.start()
                    }
                    mediaPlayer.prepareAsync()
                }
            }
        }
    }
}