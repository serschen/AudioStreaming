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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {
    private lateinit var searchBar: TextView

    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

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
                if(activity != null) {
                    val data: HashMap<String, Any> = it.data as HashMap<String, Any>
                    val songs: ArrayList<HashMap<String, Any>> =
                        data["songs"] as ArrayList<HashMap<String, Any>>
                    val albums: ArrayList<HashMap<String, Any>> =
                        data["collections"] as ArrayList<HashMap<String, Any>>
                    val artists: ArrayList<HashMap<String, Any>> =
                        data["artists"] as ArrayList<HashMap<String, Any>>

                    val rwChat: RecyclerView = v.findViewById(R.id.searchrecycler)
                    rwChat.layoutManager = LinearLayoutManager(context)

                    val songAdapter = SearchAdapter(
                        songs,
                        albums,
                        artists,
                        requireActivity().supportFragmentManager
                    )

                rwChat.adapter = songAdapter

                viewModel.currentPlaylist.value = songs
                }
            }
    }
}