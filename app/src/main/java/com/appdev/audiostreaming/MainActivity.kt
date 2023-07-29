package com.appdev.audiostreaming

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.`as`.AudioPlayerService
import com.example.`as`.NotificationReceiver
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    lateinit var bottomNav : BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createChannel()

        if(auth.currentUser == null){
            redirectStartActivity()
        }else{
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

            Toast.makeText(this, "" + auth.currentUser?.uid, Toast.LENGTH_SHORT).show()
        }
    }

    fun logout() {
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

    /////////////////////////////////////

    fun onPreviousCLicked(view: View) {
        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "prev"
            startService(intent)
        }
    }
    fun onBackClicked(view: View) {
        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "back"
            startService(intent)
        }
    }
    fun onPlayClicked(view: View) {
        if (AudioPlayerService.uri != null) {
            val playButton = findViewById<ImageView>(R.id.play_button)
            if (AudioPlayerService.isPlaying) {
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            } else {
                playButton.setImageResource(R.drawable.pause)
            }
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "play"
            var cut = findViewById<TextView>(R.id.song_info).text.toString().splitToSequence(" - ")
            updateNotification(cut.first(), cut.last(), AudioPlayerService.isPlaying)
            startService(intent)
        }
    }
    fun onForwardClicked(view: View) {
        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "forw"
            startService(intent)
        }
    }
    fun onNextClicked(view: View) {
        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "next"
            startService(intent)
        }
    }

    /////////////////////////////////////

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val id = "channel_id"
        val name = "Audio Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val visibility = NotificationCompat.VISIBILITY_PUBLIC

        val channel = NotificationChannel(id, name, importance)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun updateNotifivation(info: String, isPlaying: Boolean){
        var cut = info.split(' ')
        updateNotification(cut[0], cut[1], isPlaying)
    }
    fun updateNotification(title: String, artist: String, isPlaying: Boolean) {
        val prevIntent = createPendingIntent("prev")
        val backIntent = createPendingIntent("back")
        val playIntent = createPendingIntent("play")
        val forwIntent = createPendingIntent("forw")
        val nextIntent = createPendingIntent("next")

        val builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", prevIntent)
            .addAction(R.drawable.baseline_arrow_back_ios_24, "Back", backIntent)
            .addAction(if (isPlaying){
                R.drawable.pause
            } else{
                R.drawable.baseline_play_arrow_24
            }, "Play/Pause", playIntent)
            .addAction(R.drawable.baseline_arrow_forward_ios_24, "Forward", forwIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextIntent)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1, builder.build())
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = action

        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun onMusicbarClicked(view: View) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,SongInfoFragment())
        transaction.commit()
    }
}