package com.appdev.audiostreaming

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage

class AudioplayerFragment : Fragment() {
    var txtName: TextView? = null
    var txtArtist: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audioplayer, container, false)
        txtName = v.findViewById(R.id.textView6)
        txtArtist = v.findViewById(R.id.textView11)

        val extras = requireActivity().intent.extras

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

        return v
    }
}