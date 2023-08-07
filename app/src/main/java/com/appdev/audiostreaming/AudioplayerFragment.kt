package com.appdev.audiostreaming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class AudioplayerFragment : Fragment() {

    private lateinit var viewModel: MyViewModel

    private lateinit var _artist_name: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audioplayer, container, false)
        _artist_name = v?.findViewById(R.id.artist_name)!!


        _artist_name.setOnClickListener {
          /*  val artistId = viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it) }?.get("artistId").toString()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.add(android.R.id.content , ArtistFragment.newInstance(artistId))
            transaction?.commit()


           */

        }

        activity?.findViewById<ConstraintLayout>(R.id.musicbar_container)?.isVisible = false


        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        viewModel.isPlaying.observe(requireActivity()) {
            val playButton = v.findViewById<ImageView>(R.id.play_button)
            if (viewModel.isPlaying.value == true) {
                playButton.setImageResource(R.drawable.pause)
            } else {
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }

        v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
        v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value

        viewModel.title.observe(requireActivity()) {
            v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
        }

        viewModel.artist.observe(requireActivity()) {
            v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value
        }

        v.findViewById<ImageView>(R.id.animationViewHeart).setOnClickListener {
            if (viewModel.position.value?.let {
                    viewModel.currentPlaylist.value?.get(it)?.get("fav").toString()
                } == "") {
                FirebaseFunctions.getInstance()
                    .getHttpsCallable(
                        "heartSong?userId=" + Firebase.auth.currentUser?.uid +
                                "&songId=" + viewModel.currentPlaylist.value?.get(viewModel.position.value!!)
                            ?.get("id").toString()
                    )
                    .call()
                    .addOnFailureListener {
                        Log.wtf("tag", it)
                    }
            } else {
                FirebaseFunctions.getInstance()
                    .getHttpsCallable(
                        "unheartSong?userId=" + Firebase.auth.currentUser?.uid +
                                "&id=" + viewModel.currentPlaylist.value?.get(viewModel.position.value!!)
                            ?.get("fav").toString()
                    )
                    .call()
                    .addOnFailureListener {
                        Log.wtf("tag", it)
                    }
            }
        }

        return v
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<ConstraintLayout>(R.id.musicbar_container)?.isVisible = true
    }

}