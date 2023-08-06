package com.appdev.audiostreaming

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {
    lateinit var settingsImage: ImageView
    lateinit var playbtnImage:ImageView

    private lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val v = inflater.inflate(R.layout.fragment_home, container,false)

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        settingsImage = v?.findViewById(R.id.settings)!!
        settingsImage.setOnClickListener{
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, SettingFragment())
            transaction.commit()
        }
        playbtnImage = v?.findViewById(R.id.imageViewPlayBtn)!!
        playbtnImage.setOnClickListener{
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, AudioplayerFragment())
            transaction.commit()
        }

        viewModel.getAllSongs()

        viewModel.currentPlaylist.observe(viewLifecycleOwner, Observer {
            val rwChat: RecyclerView = v.findViewById(R.id.homerecyclerview)
            rwChat.layoutManager = LinearLayoutManager(context)

            val songAdapter = SongAdapter(viewModel ,it, true)

            rwChat.adapter = songAdapter
        })

        return v
    }
}