package com.appdev.audiostreaming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class AudioplayerFragment : Fragment() {

    private lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audioplayer, container, false)

        activity?.findViewById<ConstraintLayout>(R.id.musicbar_container)?.isVisible = false

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        viewModel.isPlaying.observe(requireActivity(), {
            val playButton = v.findViewById<ImageView>(R.id.play_button)
            if (viewModel.isPlaying.value == true) {
                playButton.setImageResource(R.drawable.pause)
            } else {
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        })

        v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
        v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value

        viewModel.title.observe(requireActivity(), {
            v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
        })

        viewModel.artist.observe(requireActivity(), {
            v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value
        })

        return v
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<ConstraintLayout>(R.id.musicbar_container)?.isVisible = true
    }

}