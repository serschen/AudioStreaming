package com.appdev.audiostreaming

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    lateinit var bottomNav : BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.library -> {
                    loadFragment(LibraryFragment())
                    true
                }
                else -> {false}
            }
        }

        if(auth.currentUser == null){
            redirectStartActivity()
        }else{
            //fetch all songs with cloud services
            FirebaseFunctions.getInstance()
                .getHttpsCallable("getAllSongs")
                .call()
                .addOnFailureListener {
                    Log.wtf("tag", it)
                }
                .addOnSuccessListener {
                    val itemList:ArrayList<HashMap<String, Any>> = it.data as ArrayList<HashMap<String, Any>>

                    val rwChat: RecyclerView = findViewById(R.id.recyclerView)
                    rwChat.layoutManager = LinearLayoutManager(this)

                    val songAdapter:SongAdapter = SongAdapter(itemList)

                    rwChat.adapter = songAdapter
                }
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener{
            logout()
        }
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                redirectStartActivity()
            }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
    private fun redirectStartActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        //loadFragment(HomeFragment())
    }
}