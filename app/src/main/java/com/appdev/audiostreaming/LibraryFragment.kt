package com.appdev.audiostreaming

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import android.widget.EditText


class LibraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var txt: EditText

    lateinit var rwFav:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)

    }

    private lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_library, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        FirebaseFunctions.getInstance()
            .getHttpsCallable("getHeartedSongs?userId=" + Firebase.auth.currentUser?.uid)
            .call()
            .addOnFailureListener {
                Log.wtf("tag", it)
            }
            .addOnSuccessListener {
                val itemList: ArrayList<HashMap<String, Any>> =
                    it.data as ArrayList<HashMap<String, Any>>

                val rwChat: RecyclerView = v.findViewById(R.id.rvFav)
                rwChat.layoutManager = LinearLayoutManager(context)

                val songAdapter = SongAdapter(requireActivity().supportFragmentManager, viewModel, itemList, true)

                rwChat.adapter = songAdapter
            }

        return v
    }
}