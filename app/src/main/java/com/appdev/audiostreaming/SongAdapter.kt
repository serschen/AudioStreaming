package com.appdev.audiostreaming

import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.`as`.AudioPlayerService

class SongAdapter(private val songs:ArrayList<HashMap<String, Any>>,
                  private val showPicture:Boolean) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView? = itemView.findViewById(R.id.txtResultName)
        val txtArtist: TextView? = itemView.findViewById(R.id.txtResultArtist)
        val searchResultLayout:LinearLayout = itemView.findViewById(R.id.searchResultLayout)
        val img:ImageView = itemView.findViewById(R.id.imgArtistResult)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)


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

        if(!showPicture){
            holder.img.visibility = View.GONE
        }

        holder.searchResultLayout.setOnClickListener{v ->
            val intent = Intent(v.context, AudioPlayerService::class.java)
            intent.putExtra("map", songs[position])
            intent.putExtra("pos", position)
            intent.action = "chan"
            v.context.startService(intent)
        }
    }
}