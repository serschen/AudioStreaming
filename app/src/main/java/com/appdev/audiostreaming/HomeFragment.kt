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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var settingsImage: ImageView
    lateinit var playbtnImage:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val v = inflater.inflate(R.layout.fragment_home, container,false)

        settingsImage = v.findViewById(R.id.settings)!!
        settingsImage.setOnClickListener{
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SettingFragment())
            transaction.commit()
        }
        playbtnImage = v.findViewById(R.id.imageViewPlayBtn)!!
        playbtnImage.setOnClickListener{
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
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
                rwChat.layoutManager = LinearLayoutManager(context)

                val songAdapter = SongAdapter(itemList)

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
}