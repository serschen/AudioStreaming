package com.appdev.audiostreaming

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class LaunchActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Send user to MainActivity as soon as this activity loads
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // remove this activity from the stack
        finish()
    }
}