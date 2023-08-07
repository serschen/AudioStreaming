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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AudioplayerFragment : Fragment() {

    private lateinit var viewModel: MyViewModel
    private lateinit var img:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_audioplayer, container, false)

        img = v.findViewById(R.id.imageView2)

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
            setImage()
        }

        viewModel.artist.observe(requireActivity()) {
            v.findViewById<TextView>(R.id.artist_name).text = viewModel.artist.value
        }

        v.findViewById<ImageView>(R.id.animationViewHeart).setOnClickListener {
            if(viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("fav").toString() } == ""){
                FirebaseFunctions.getInstance()
                    .getHttpsCallable("heartSong?userId=" + Firebase.auth.currentUser?.uid +
                            "&songId=" + viewModel.currentPlaylist.value?.get(viewModel.position.value!!)
                                ?.get("id").toString())
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
            }else{
                FirebaseFunctions.getInstance()
                    .getHttpsCallable("unheartSong?userId=" + Firebase.auth.currentUser?.uid +
                            "&id=" + viewModel.currentPlaylist.value?.get(viewModel.position.value!!)
                        ?.get("fav").toString())
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
        if(viewModel.currentPlaylist.value?.size!! > 0) {
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