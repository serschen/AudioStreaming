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
import android.graphics.BitmapFactory
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
    private lateinit var playBtn: ImageView
    private lateinit var forward: ImageView
    private lateinit var back: ImageView
    private lateinit var next: ImageView
    private lateinit var prev: ImageView







    //Gestures

    private lateinit var layout: LinearLayout

    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        layout = findViewById(linearLayout)
        playBtn = findViewById(R.id.play_button)
        forward = findViewById(R.id.forward_button)
        back = findViewById(R.id.back_button)
        next = findViewById(R.id.next_button)
        prev = findViewById(R.id.previous_button)



        layout.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                previous()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                next()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                //Toast.makeText(this@MainActivity, "Swipe up gesture detected", Toast.LENGTH_SHORT).show()
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                //Toast.makeText(this@MainActivity, "Swipe down gesture detected", Toast.LENGTH_SHORT).show()
            }
        })


        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        viewModel.theme.observe(this, Observer{
            if(it == Themes.ALTERNATE){
                bottomNav.menu[0].icon = ContextCompat.getDrawable(this, R.drawable.retro_home)
                bottomNav.menu[1].icon = ContextCompat.getDrawable(this, R.drawable.retro_search_1)
                bottomNav.menu[2].icon = ContextCompat.getDrawable(this, R.drawable.retro_libicon)
                playBtn.setImageResource(R.drawable.retro_play)
                playBtn.setImageResource(R.drawable.retro_pause)
                back.setImageResource(R.drawable.back)
                forward.setImageResource(R.drawable.retro_forward)
                next.setImageResource(R.drawable.retro_next)
                prev.setImageResource(R.drawable.retro_prev)

            }else if(it == Themes.MODERN){
                bottomNav.menu[0].icon = ContextCompat.getDrawable(this, R.drawable.baseline_home_24)
                bottomNav.menu[1].icon = ContextCompat.getDrawable(this, R.drawable.baseline_search_24)
                bottomNav.menu[2].icon = ContextCompat.getDrawable(this, R.drawable.baseline_local_library_24)
                playBtn.setImageResource(R.drawable.baseline_play_arrow_24)
                back.setImageResource(R.drawable.baseline_skip_previous_24)
                forward.setImageResource(R.drawable.baseline_skip_next_24)
                next.setImageResource(R.drawable.baseline_arrow_forward_ios_24)
                prev.setImageResource(R.drawable.baseline_arrow_back_ios_24)
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
        }

        viewModel.isPlaying.observe(this) {
            val intent = Intent(this, AudioPlayerService::class.java)
            val playButton = findViewById<ImageView>(R.id.play_button)
            if (viewModel.isPlaying.value == true) {
                playButton.setImageResource(R.drawable.pause)
                intent.action = "play"
                val temp = viewModel.position.value?.let { it1 ->
                    viewModel.currentPlaylist.value?.get(
                        it1
                    )?.get("path")
                }
                intent.putExtra("path", temp.toString())
                setImage()
            } else {
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                intent.action = "pause"
            }
            val cut =
                findViewById<TextView>(R.id.song_info).text.toString().splitToSequence(" - ")
            viewModel.isPlaying.value?.let { it1 ->
                updateNotification(
                    cut.first(), cut.last(),
                    it1
                )
            }

            startService(intent)
        }

        viewModel.position.observe(this) {
            val intent = Intent(this, AudioPlayerService::class.java)
            intent.action = "play"
            val temp = viewModel.position.value?.let { it1 ->
                viewModel.currentPlaylist.value?.get(
                    it1
                )?.get("path")
            }
            intent.putExtra("path", temp.toString())
        }

        viewModel.title.observe(this) {
            findViewById<TextView>(R.id.song_info).text =
                viewModel.title.value + " - " + viewModel.artist.value
        }

        viewModel.artist.observe(this) {
            findViewById<TextView>(R.id.song_info).text =
                viewModel.title.value + " - " + viewModel.artist.value
        }
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
        previous()
    }
    private fun previous(){
        AudioPlayerService.time = 0

        var position = viewModel.position.value?.minus(1)
        if(position!! < 0){
            position = viewModel.currentPlaylist.value?.size?.minus(1)
        }
        viewModel.position.value = position
        viewModel.title.value =
            viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("name").toString() }
        viewModel.artist.value = viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("artistName").toString() }

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
        startService(intent)
    }
    fun onPlayClicked(view: View) {
        viewModel.isPlaying.value = viewModel.isPlaying.value != true
    }
    fun onForwardClicked(view: View) {
        val intent = Intent(this, AudioPlayerService::class.java)
        intent.action = "forward"
        startService(intent)
    }
    fun onNextClicked(view: View) {
        next()
    }
    private fun next(){
        AudioPlayerService.time = 0

        var position = viewModel.position.value?.plus(1)
        if(position!! > viewModel.currentPlaylist.value!!.size - 1){
            position = 0
        }
        viewModel.position.value = position
        viewModel.title.value =
            viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("name").toString() }
        viewModel.artist.value = viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("artistName").toString() }


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

    private fun updateNotification(title: String, artist: String, isPlaying: Boolean) {
        val backIntent = createPendingIntent("back")
        val playIntent = createPendingIntent("play")
        val pauseIntent = createPendingIntent("pause")
        val forwIntent = createPendingIntent("forward")

        val builder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .addAction(R.drawable.baseline_arrow_back_ios_24, "Back", backIntent)
            .addAction(if (isPlaying){
                R.drawable.pause
            } else{
                R.drawable.baseline_play_arrow_24
            }, if(isPlaying) "Pause" else "Play", if(isPlaying) pauseIntent else playIntent)
            .addAction(R.drawable.baseline_arrow_forward_ios_24, "Forward", forwIntent)

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

                findViewById<TextView>(R.id.song_info).text = "$title - $artist"
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

    fun onMusicbarClicked(view: View) {
        loadFragment(AudioplayerFragment())
    }

    fun setImage(){
        val storageReference = FirebaseStorage.getInstance().reference
        val path:String = viewModel.position.value?.let { viewModel.currentPlaylist.value?.get(it)?.get("imagePath") }.toString()
        val photoReference = storageReference.child(path)

        val ONE_MEGABYTE = (1024 * 1024).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            findViewById<ImageView>(R.id.song_pic).setImageBitmap(bmp)
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "No Such file or Path found!!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}