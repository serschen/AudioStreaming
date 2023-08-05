package com.appdev.audiostreaming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "collectionId"
private const val ARG_PARAM2 = "artistName"
private const val ARG_PARAM3 = "collectionName"

/**
 * A simple [Fragment] subclass.
 * Use the [CollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CollectionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var collectionId: String? = null
    private var artistName: String? = null
    private var collectionName: String? = null

    lateinit var txtName:TextView
    lateinit var txtArtist:TextView
    lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            collectionId = it.getString(ARG_PARAM1)
            artistName = it.getString(ARG_PARAM2)
            collectionName = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_collection, container, false)

        txtName = v.findViewById<TextView>(R.id.collectionName)
        txtArtist = v.findViewById<TextView>(R.id.collectionArtist)

        txtName.text = artistName ?: "Name undefined"
        txtArtist.text = collectionName ?: "Artist undefined"

        btnClose = v.findViewById(R.id.btnCloseCollection)
        btnClose.setOnClickListener{close()}

        getSongs(v)

        return v
    }

    private fun getSongs(v: View) {
        if(collectionId != "") {
            FirebaseFunctions.getInstance()
                .getHttpsCallable(
                    "getSongsByCollectionId?userId=" + Firebase.auth.currentUser?.uid +
                            "&collection=" + collectionId
                )
                .call()
                .addOnFailureListener {
                    Log.wtf("tag", it)
                }
                .addOnSuccessListener {
                    val itemList: ArrayList<HashMap<String, Any>> =
                        it.data as ArrayList<HashMap<String, Any>>

                    val rwChat: RecyclerView = v.findViewById(R.id.rvSongs)
                    rwChat.layoutManager = LinearLayoutManager(context)

                    val songAdapter = SongAdapter(itemList, false)

                    rwChat.adapter = songAdapter
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param collectionId Parameter 1.
         * @param artistName Parameter 2.
         * @return A new instance of fragment CollectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(collectionId: String, artistName: String, collectionName: String) =
            CollectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, collectionId)
                    putString(ARG_PARAM2, artistName)
                    putString(ARG_PARAM3, collectionName)
                }
            }
    }

    fun close(){
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }
}