package com.appdev.audiostreaming

import android.os.Bundle
import android.transition.TransitionInflater
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var searchBar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_search, container, false)

        searchBar = v.findViewById(R.id.txtSongSuche)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s, v)
            }
        })

        return v
    }

    private fun search(s: CharSequence?, v:View) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("search?userId=" + Firebase.auth.currentUser?.uid +
            "&subject=" + s.toString())
            .call()
            .addOnFailureListener {
                Log.wtf("tag", it)
            }
            .addOnSuccessListener {
                val data:HashMap<String, Any> = it.data as HashMap<String, Any>
                val songs:ArrayList<HashMap<String, Any>> = data["songs"] as ArrayList<HashMap<String, Any>>
                val albums:ArrayList<HashMap<String, Any>> = data["collections"] as ArrayList<HashMap<String, Any>>
                val artists:ArrayList<HashMap<String, Any>> = data["artists"] as ArrayList<HashMap<String, Any>>

                val rwChat: RecyclerView = v.findViewById(R.id.searchrecycler)
                rwChat.layoutManager = LinearLayoutManager(context)

                val songAdapter = SearchAdapter(songs, albums, artists, requireActivity().supportFragmentManager)

                rwChat.adapter = songAdapter
            }
    }
}