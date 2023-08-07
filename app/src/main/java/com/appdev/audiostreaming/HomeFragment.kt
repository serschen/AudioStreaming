package com.appdev.audiostreaming

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var viewModel: MyViewModel

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var settingsImage: ImageView
    private lateinit var playbtnImage: ImageView
    private lateinit var clockImg: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentThemes: Themes

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
        recyclerView = v?.findViewById(R.id.homerecyclerview)!!

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        settingsImage = v?.findViewById(R.id.settings)!!
        settingsImage.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, SettingFragment())
            transaction.commit()
        }

        viewModel.getAllSongs()

        viewModel.currentPlaylist.observe(viewLifecycleOwner, Observer {
            val itemList: ArrayList<HashMap<String, Any>> = it as ArrayList<HashMap<String, Any>>

            val rwChat: RecyclerView = v.findViewById(R.id.homerecyclerview)
            rwChat.layoutManager = LinearLayoutManager(context)

            val songAdapter = SongAdapter(requireActivity().supportFragmentManager, viewModel, itemList, true)

            rwChat.adapter = songAdapter
        })
        return v
    }

    private fun showMsg(message: String) {
        Toast.makeText(requireContext(), "Last played Songs..", Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            ViewModelProvider(requireActivity()).get(AudioServiceViewModel::class.java)

/*   sharedViewModel.imageButtonResId.observe(viewLifecycleOwner, Observer { resId ->
       // Update the ImageButton in FragmentA with the new resource ID
       imageButton.setImageResource(resId)
   })

   // Set the initial image for the ImageButton
   settingsImage.setImageResource(R.drawable.baseline_more_time_24)

   // Set a click listener to change the ImageButton from FragmentA
   settingsImage.setOnClickListener {
       sharedViewModel.setImageButtonResId(R.drawable.retro_library__2_)
   }
}
}*/
    }


}