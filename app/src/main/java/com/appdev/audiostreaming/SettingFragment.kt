package com.appdev.audiostreaming

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {
    private lateinit var changeThemeBtn: Button
    private lateinit var viewModel:MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide)

        val s = Firebase.auth.currentUser?.displayName

        Log.d("SettingsFragment", "$s")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_setting, container, false)
        changeThemeBtn = v?.findViewById(R.id.btn_change_theme)!!

        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)

        changeThemeBtn.setOnClickListener{
            if(viewModel.theme.value == Themes.ALTERNATE){
                viewModel.theme.value = Themes.MODERN
            }else{
                viewModel.theme.value = Themes.ALTERNATE
            }
        }
        v.findViewById<Button>(R.id.btnLogoutSettings).setOnClickListener {
            logout()
        }

        return v
    }

    private fun logout() {
        (activity as MainActivity).logout()
    }
}