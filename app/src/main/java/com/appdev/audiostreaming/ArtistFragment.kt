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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


private const val ARG_PARAM1 = "artistId"

class ArtistFragment : Fragment() {
    private var artistId: String? = null

    private lateinit var txtArtistName:TextView
    private lateinit var txtBiography:TextView
    private lateinit var ivArtist:ImageView
    private lateinit var rvCollections:RecyclerView

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
                if(activity != null) {
                    val data: HashMap<String, Any> = it.data as HashMap<String, Any>
                    val artist: HashMap<String, Any> = data["artist"] as HashMap<String, Any>
                    val collections: ArrayList<HashMap<String, Any>> =
                        data["collections"] as ArrayList<HashMap<String, Any>>
                    val imagePath: String = artist["imagePath"].toString()

                    txtArtistName.text = artist["name"].toString()
                    txtBiography.text = artist["description"].toString()

                    rvCollections.layoutManager = LinearLayoutManager(context)

                    val songAdapter = ArtistCollectionAdapter(
                        requireActivity().supportFragmentManager,
                        viewModel,
                        collections
                    )

                    rvCollections.adapter = songAdapter

                    setArtistImage(imagePath)
                }
            }
    }

    fun setArtistImage(path:String){
        val storageReference = FirebaseStorage.getInstance().reference
        val photoReference = storageReference.child(path)

        val ONE_MEGABYTE = (1024 * 1024).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ivArtist.setImageBitmap(bmp)
        }.addOnFailureListener {
            Toast.makeText(
                context,
                "No Such file or Path found!!",
                Toast.LENGTH_LONG
            ).show()
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