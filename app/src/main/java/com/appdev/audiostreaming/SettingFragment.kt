package com.appdev.audiostreaming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_setting, container, false)

        v.findViewById<Button>(R.id.btnLogoutSettings).setOnClickListener{
            logout()
        }

        return v
    }

    private fun logout() {
        (activity as MainActivity).logout()
    }
}