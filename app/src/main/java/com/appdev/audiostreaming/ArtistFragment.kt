package com.appdev.audiostreaming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "artistId"

class ArtistFragment : Fragment() {
    private var artistId: String? = null

    lateinit var txtArtistName:TextView
    lateinit var txtBiography:TextView
    lateinit var ivArtist:ImageView
    lateinit var rvCollections:RecyclerView

    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            artistId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_artist, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        v.findViewById<ImageView>(R.id.btnArtistBack).setOnClickListener{close()}

        txtArtistName = v.findViewById(R.id.textView12)
        txtBiography = v.findViewById(R.id.bio)
        rvCollections = v.findViewById(R.id.a_page_recycler)
        ivArtist = v.findViewById(R.id.imageView9)

        setArtistSongs(v)

        return v
    }

    private fun setArtistSongs(v: View) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("getArtistById?userId=" + Firebase.auth.currentUser?.uid +
                    "&id=" + artistId)
            .call()
            .addOnFailureListener {
                Log.wtf("tag", it)
            }
            .addOnSuccessListener {
                val data:HashMap<String, Any> = it.data as HashMap<String, Any>
                val artist: HashMap<String, Any> = data["artist"] as HashMap<String, Any>
                val collections:ArrayList<HashMap<String, Any>> = data["collections"] as ArrayList<HashMap<String, Any>>

                txtArtistName.text = artist["name"].toString()
                txtBiography.text = artist["description"].toString()

                rvCollections.layoutManager = LinearLayoutManager(context)

                val songAdapter = ArtistCollectionAdapter(viewModel, collections)

                rvCollections.adapter = songAdapter
            }
    }

    private fun close() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(artistId: String) =
            ArtistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, artistId)
                }
            }
    }
}