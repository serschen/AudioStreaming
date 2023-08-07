package com.appdev.audiostreaming

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {
    private lateinit var changeThemeBtn: Button
    private lateinit var viewModel:MyViewModel
    private lateinit var linearLayout:LinearLayout
    private lateinit var logoutBtn:Button

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

        val v = inflater.inflate(R.layout.fragment_setting, container, false)
        changeThemeBtn = v?.findViewById(R.id.btn_change_theme)!!
        linearLayout = v?.findViewById(R.id.l)!!
        logoutBtn = v?.findViewById(R.id.btnLogoutSettings)!!


        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        viewModel.theme.observe(viewLifecycleOwner , Observer{
            if(it == Themes.ALTERNATE){
                linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
                changeThemeBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.alternate_bg))
                logoutBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
            }else if(it == Themes.MODERN){
                changeThemeBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                linearLayout.setBackgroundResource(R.drawable.background_settings)
                ContextCompat.getColor(requireContext(), R.color.red)
            }
        })

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