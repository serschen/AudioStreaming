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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    lateinit var settingsImage: ImageView
    lateinit var playbtnImage:ImageView

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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val v = inflater.inflate(R.layout.fragment_home, container,false)
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView= v?.findViewById(R.id.homerecyclerview)!!

        settingsImage = v?.findViewById(R.id.settings)!!
        clockImg = v?.findViewById(R.id.clockIcon)!!
        clockImg.setOnClickListener {
            showMsg("This is some Message for u! ;)")
        }
        settingsImage.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, SettingFragment())
            transaction.commit()
        }
        playbtnImage = v?.findViewById(R.id.imageViewPlayBtn)!!
        playbtnImage.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, AudioplayerFragment())
            transaction.commit()
        }


        FirebaseFunctions.getInstance()
            .getHttpsCallable("getAllSongs?userId=" + Firebase.auth.currentUser?.uid)
            .call()
            .addOnFailureListener {
                Log.wtf("tag", it)
            }
            .addOnSuccessListener {
                val itemList: ArrayList<HashMap<String, Any>> =
                    it.data as ArrayList<HashMap<String, Any>>

                val rwChat: RecyclerView = v.findViewById(R.id.homerecyclerview)
                rwChat.layoutManager = LinearLayoutManager(this.requireContext())

                val songAdapter: SongAdapter = SongAdapter(itemList)
                val songAdapter:SongAdapter = SongAdapter(itemList, true)

                rwChat.adapter = songAdapter
            }

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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