package com.appdev.audiostreaming

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AudioplayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioplayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var txtName: TextView? = null
    var txtArtist: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Audioplayer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AudioplayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}