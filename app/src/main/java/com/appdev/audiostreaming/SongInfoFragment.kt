package com.appdev.audiostreaming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.appdev.audiostreaming.lukas.AudioPlayerService

class SongInfoFragment : Fragment() {

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_song_info, container, false)
        view = v
        view.findViewById<TextView>(R.id.song_title).setText(AudioPlayerService.title)
        view.findViewById<TextView>(R.id.song_artist).setText(AudioPlayerService.artist)

        val filter = IntentFilter(MainActivity.ACTION_UPDATE_UI)
        context?.registerReceiver(updateUIReceiver, filter)
        return v
    }

    companion object {
        const val ACTION_UPDATE_UI = "com.example.appdev.audiostreaming.UPDATE_UI"
    }
    private val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_UPDATE_UI) {
                val title = intent.getStringExtra("title")
                val artist = intent.getStringExtra("artist")

                view.findViewById<TextView>(R.id.song_title).setText(title)
                view.findViewById<TextView>(R.id.song_artist).setText(artist)
            }
        }
    }
}