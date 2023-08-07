package com.appdev.audiostreaming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "collectionId"
private const val ARG_PARAM2 = "artistName"
private const val ARG_PARAM3 = "collectionName"


class CollectionFragment : Fragment() {
    private var collectionId: String? = null
    private var artistName: String? = null
    private var collectionName: String? = null

    lateinit var txtName:TextView
    lateinit var txtArtist:TextView
    lateinit var btnClose: Button

    private lateinit var viewModel: MyViewModel

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
        val v = inflater.inflate(R.layout.fragment_collection, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

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

                    val songAdapter = SongAdapter(requireActivity().supportFragmentManager, viewModel, itemList, false)

                    rwChat.adapter = songAdapter

                    viewModel.currentPlaylist.value = itemList
                }
        }
    }

    companion object {
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