package com.appdev.audiostreaming

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AudioplayerFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var img:ImageView
    private lateinit var _artist_name: TextView
    private lateinit var playBtn: ImageView
    private lateinit var forward: ImageView
    private lateinit var back: ImageView
    private lateinit var next: ImageView
    private lateinit var prev: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audioplayer, container, false)
        _artist_name = v?.findViewById(R.id.artist_name)!!


        /*_artist_name.setOnClickListener {
            val artist = viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it) }?.get("artistId").toString()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.add(android.R.id.content , ArtistFragment.newInstance(artist))
            transaction?.commit()

        }

        img = v.findViewById(R.id.imageView2)

         */

        v.findViewById<TextView>(R.id.artist_name).setOnClickListener{
            val artistId =
                viewModel.position.value?.let { it1 -> viewModel.currentPlaylist.value?.get(it1)?.get("artist").toString() }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(android.R.id.content , ArtistFragment.newInstance(artistId ?: ""))
            transaction.commit()
        }
        activity?.findViewById<ConstraintLayout>(R.id.musicbar_container)?.isVisible = false
        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        playBtn = v?.findViewById(R.id.play_button)!!
        forward = v?.findViewById(R.id.forward)!!
        back = v?.findViewById(R.id.back)!!
        next = v?.findViewById(R.id.next)!!
        prev = v?.findViewById(R.id.prev)!!
        img = v.findViewById(R.id.imageView2)

        viewModel.theme.observe(viewLifecycleOwner , Observer{
            if(it == Themes.ALTERNATE){
                playBtn.setImageResource(R.drawable.retro_play)
                playBtn.setImageResource(R.drawable.retro_pause)
                back.setImageResource(R.drawable.retro_prev)
                next.setImageResource(R.drawable.retro_forward)
                forward.setImageResource(R.drawable.retro_next)
                prev.setImageResource(R.drawable.back)

            }else if(it == Themes.MODERN){
                playBtn.setImageResource(R.drawable.baseline_play_arrow_24)
                back.setImageResource(R.drawable.baseline_skip_previous_24)
                forward.setImageResource(R.drawable.baseline_skip_next_24)
                next.setImageResource(R.drawable.baseline_arrow_forward_ios_24)
                prev.setImageResource(R.drawable.baseline_arrow_back_ios_24)
            }
        })

        viewModel.isPlaying.observe(requireActivity()) {
            val playButton = v.findViewById<ImageView>(R.id.play_button)
            if (viewModel.isPlaying.value == true) {
                if(viewModel.theme.value == Themes.ALTERNATE) {
                    playBtn.setImageResource(R.drawable.retro_pause)
                }else {
                    playButton.setImageResource(R.drawable.pause)
                }
            } else {
                if(viewModel.theme.value == Themes.ALTERNATE) {
                    playButton.setImageResource(R.drawable.retro_play)
                }else {
                    playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                }
            }
        }

        v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
        v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value

        viewModel.title.observe(requireActivity()) {
            v.findViewById<TextView>(R.id.song_title).text = viewModel.title.value
            setImage()
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
                    .addOnSuccessListener {
                        val id:String = it.data as String

                        val list = viewModel.currentPlaylist.value
                        val pos = viewModel.position.value ?: -1
                        if(pos != -1) {
                            list?.get(pos)?.set("fav", id)
                            viewModel.currentPlaylist.value = list
                        }
                    }
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
                    .addOnSuccessListener {
                        val list = viewModel.currentPlaylist.value
                        val pos = viewModel.position.value ?: -1
                        if(pos != -1) {
                            list?.get(pos)?.set("fav", "")
                            viewModel.currentPlaylist.value = list
                        }
                    }
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

    fun setImage(){
        if(viewModel.currentPlaylist.value != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val path: String = viewModel.position.value?.let {
                viewModel.currentPlaylist.value?.get(it)?.get("imagePath")
            }.toString()
            val photoReference = storageReference.child(path)

            val ONE_MEGABYTE = (1024 * 1024).toLong()
            photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                img.setImageBitmap(bmp)
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "No Such file or Path found!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}