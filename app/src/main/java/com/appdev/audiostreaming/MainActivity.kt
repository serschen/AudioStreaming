package com.appdev.audiostreaming

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.com.kotlinapp.OnSwipeTouchListener
import com.appdev.audiostreaming.R.id.linearLayout
import com.appdev.audiostreaming.R.layout.activity_main
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private val auth = Firebase.auth
   private lateinit var bottomNav: BottomNavigationView
    private var musicplayer: MediaPlayer? = null
    private var currentSong: MutableList<Int> = mutableListOf()
    private lateinit var playBtn: Button

    //Gestures

    private lateinit var layout: LinearLayout

    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        layout = findViewById(linearLayout)

        layout.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Toast.makeText(this@MainActivity, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                Toast.makeText(this@MainActivity, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                Toast.makeText(this@MainActivity, "Swipe up gesture detected", Toast.LENGTH_SHORT).show()
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                Toast.makeText(this@MainActivity, "Swipe down gesture detected", Toast.LENGTH_SHORT).show()
            }
        })


      //  controlSound(currentSong[0])

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        viewModel.theme.observe(this, Observer{
            if(it == Themes.ALTERNATE){
                bottomNav.menu[0].icon = ContextCompat.getDrawable(this, R.drawable.retro_home__3_)
            }else if(it == Themes.MODERN){
                bottomNav.menu[0].icon = ContextCompat.getDrawable(this, R.drawable.baseline_home_24)
            }
        })

        val filter = IntentFilter(ACTION_UPDATE_UI)
        registerReceiver(updateUIReceiver, filter)

        createChannel()

        if (auth.currentUser == null) {
            redirectStartActivity()
        } else {
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
                    else -> {
                        false
                    }
                }
            }

            Toast.makeText(this, "" + auth.currentUser?.uid, Toast.LENGTH_SHORT).show()
        }

        viewModel.isPlaying.observe(this, Observer {
            if (AudioPlayerService.uri != null) {
                val intent = Intent(this, AudioPlayerService::class.java)
                if (it) {
                    val playButton = findViewById<ImageView>(R.id.play_button)
                    if (viewModel.isPlaying.value == true) {
                        playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                        intent.action = "play"
                        val temp = viewModel.position.value?.let { it1 ->
                            viewModel.currentPlaylist.value?.get(
                                it1
                            )?.get("path")
                        }
                        intent.putExtra("path", temp.toString())
                    } else {
                        playButton.setImageResource(R.drawable.pause)
                        intent.action = "pause"
                    }
                }
                var cut = findViewById<TextView>(R.id.song_info).text.toString().splitToSequence(" - ")
                viewModel.isPlaying.value?.let { it1 ->
                    updateNotification(cut.first(), cut.last(),
                        it1
                    )
                }
                startService(intent)
            }
        })

        viewModel.position.observe(this, Observer {
            var intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "play"
            val temp = viewModel.position.value?.let { it1 ->
                viewModel.currentPlaylist.value?.get(
                    it1
                )?.get("path")
            }
            intent.putExtra("path", temp.toString())
        })
    }

    fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                redirectStartActivity()
            }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private fun redirectStartActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun controlSound(id:Int){
        playBtn= findViewById(R.id.play_button)

        playBtn.setOnClickListener{
            if(musicplayer == null){
                musicplayer = MediaPlayer.create(this,id)
                Log.d("MainActivity", "ID:${musicplayer!!.audioSessionId}")

                initSeekBar()
            }
            musicplayer?.start()
            Log.d("MainActivity", "Duration: ${musicplayer!!.duration/1000} seconds")
        }
        val seekbar:SeekBar = findViewById(R.id.seekbar)

        seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicplayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

        })


    }
    private fun initSeekBar(){
        val seekbar:SeekBar = findViewById(R.id.seekbar)

        seekbar.max = musicplayer!!.duration
        Log.d("MainActivity", "SeekBar Maximum set at ${musicplayer!!.duration/1000} seconds")

        val handler = Handler()

        handler.postDelayed(object: Runnable{
            override fun run() {
                try{
                seekbar.progress = musicplayer!!.currentPosition
                handler.postDelayed(this,1000)
            }catch (e: Exception){
                    seekbar.progress = 0
                }
            }

        },0)
    }
    override fun onBackPressed() {
        //loadFragment(HomeFragment())
    }

    /////////////////////////////////////

    fun onPreviousCLicked(view: View) {
        var position = viewModel.position.value?.minus(1)
        if(position!! < 0){
            position = viewModel.currentPlaylist.value?.size?.minus(1)
        }
        viewModel.position.value = position

        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "play"
            val temp = viewModel.position.value?.let { it1 ->
                viewModel.currentPlaylist.value?.get(
                    it1
                )?.get("path")
            }
            intent.putExtra("path", temp.toString())
            startService(intent)
        }
    }
    fun onBackClicked(view: View) {
        val intent = Intent(this, AudioPlayerService::class.java)
        intent.action = "back"
    }
    fun onPlayClicked(view: View) {
        viewModel.isPlaying.value = !viewModel.isPlaying.value!!
    }
    fun onForwardClicked(view: View) {
        val intent = Intent(this, AudioPlayerService::class.java)
        intent.action = "forward"
    }
    fun onNextClicked(view: View) {
        var position = viewModel.position.value?.plus(1)
        if(position!! > viewModel.currentPlaylist.value!!.size - 1){
            position = 0
        }
        viewModel.position.value = position

        if (AudioPlayerService.uri != null) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "play"
            val temp = viewModel.position.value?.let { it1 ->
                viewModel.currentPlaylist.value?.get(
                    it1
                )?.get("path")
            }
            intent.putExtra("path", temp.toString())
            startService(intent)
        }
    }

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
            }, if(isPlaying) "Pause" else "Play", playIntent)
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

        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val ACTION_UPDATE_UI = "com.example.appdev.audiostreaming.UPDATE_UI"
    }
    private val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_UPDATE_UI) {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val title = intent.getStringExtra("title")
                val artist = intent.getStringExtra("artist")

                findViewById<TextView>(R.id.song_info).setText("$title - $artist")
                findViewById<ImageView>(R.id.play_button).setImageResource(if (isPlaying) R.drawable.pause else R.drawable.baseline_play_arrow_24)
            }
        }
    }
    //function for changeing Theme
    private fun changeTheme(theme: Int) {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("set_theme", theme)
        startActivity(intent)
    }
}