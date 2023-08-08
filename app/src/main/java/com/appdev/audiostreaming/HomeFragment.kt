package com.appdev.audiostreaming

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var viewModel: MyViewModel

    private lateinit var settingsImage: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var username: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        settingsImage = v?.findViewById(R.id.settings)!!
        recyclerView = v?.findViewById(R.id.homerecyclerview)!!
        username = v?.findViewById(R.id.u_name)!!

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        viewModel.theme.observe(viewLifecycleOwner , Observer{
            if(it == Themes.ALTERNATE){
             settingsImage.setImageResource(R.drawable.retro_settings)
             recyclerView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))

            }else if(it == Themes.MODERN){
                settingsImage.setImageResource(R.drawable.baseline_settings_24)
                recyclerView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        })
        val name = Firebase.auth.currentUser?.displayName
        if(name != null){
            username.text = name.toString()
        } else {
            username.text = "User XYZ"
            Log.d("HomeFragment", "Username could not be found")
        }

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)


        settingsImage.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, SettingFragment())
            transaction.commit()
        }

        viewModel.getAllSongs()

        viewModel.allSongs.observe(viewLifecycleOwner) {
            val itemList: ArrayList<HashMap<String, Any>> = it as ArrayList<HashMap<String, Any>>

            val rwChat: RecyclerView = v.findViewById(R.id.homerecyclerview)
            rwChat.layoutManager = LinearLayoutManager(context)

            val songAdapter =
                SongAdapter(requireActivity().supportFragmentManager, viewModel, itemList, true)

            rwChat.adapter = songAdapter
        }
        return v
    }
}