package com.appdev.audiostreaming

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.String


class MainActivity : AppCompatActivity() {
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    /*val j = JSONObject(it.data.toString())

                    val gson = Gson()
                    val itemType = object : TypeToken<ArrayList<Song>>() {}.type
                    val itemList = gson.fromJson<ArrayList<Song>>(j.get("songs").toString(), itemType)*/

                    val itemList:ArrayList<Song> = ArrayList()

                    itemList.add(Song("Collection1","Artist1","Rick Astley","/Music/nevergonnagiveyouup.mp3","Never gonna give you up",212))


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

    private fun redirectStartActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}