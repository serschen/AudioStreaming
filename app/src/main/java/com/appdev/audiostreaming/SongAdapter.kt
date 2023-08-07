package com.appdev.audiostreaming

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView


class SongAdapter(private val viewModel: MyViewModel, private val songs: ArrayList<HashMap<String, Any>>, private var showPicture: Boolean) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    private lateinit var listText: TextView
    private lateinit var lottieAnimationView: LottieAnimationView

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView? = itemView.findViewById(R.id.txtResultName)
        val txtArtist: TextView? = itemView.findViewById(R.id.txtResultArtist)
        val searchResultLayout: LinearLayout = itemView.findViewById(R.id.searchResultLayout)
        val img: ImageView = itemView.findViewById(R.id.imgArtistResult)

        //animation in Grid
//        val animationView: LottieAnimationView = itemView.findViewById(R.id.animationView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val song:Song = songs[position]

        //holder.txtName?.text = song.name
        //holder.txtArtist?.text = song.artistName
        holder.txtName?.text = songs[position]["name"].toString()
        holder.txtArtist?.text = songs[position]["artistName"].toString()

        holder.searchResultLayout.setOnClickListener { v ->
            if (!showPicture) {
                holder.img.visibility = View.GONE
            }

            holder.searchResultLayout.setOnClickListener { v ->
                viewModel.position.value = position
                val intent = Intent(v.context, AudioPlayerService::class.java)
                intent.action = "play"
                val temp = viewModel.position.value?.let { it1 ->
                    viewModel.currentPlaylist.value?.get(
                        it1
                    )?.get("path")
                }
                intent.putExtra("path", temp.toString())

                v.context.startService(intent)
            }

            //https://medium.com/@manuchekhrdev/lottie-animation-in-android-using-kotlin-8ff5d07f5f23#:~:text=Lottie%20is%20a%20library%20that,back%20natively%20on%20Android%20devices.
            //  val item = songs[position]
            // Set the Lottie animation for the view holder
            // holder.animationView.playAnimation()
        }
    }
}