package com.appdev.audiostreaming

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.`as`.AudioPlayerService

class SearchAdapter(private val songs:ArrayList<HashMap<String, Any>>,
                    private val albums:ArrayList<HashMap<String, Any>>,
                    private val artists:ArrayList<HashMap<String, Any>>,
                    private val fragmentManager:FragmentManager
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView? = itemView.findViewById(R.id.txtResultName)
        val txtArtist: TextView? = itemView.findViewById(R.id.txtResultArtist)
        val searchResultLayout:LinearLayout = itemView.findViewById(R.id.searchResultLayout)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.songs.size + this.albums.size + this.artists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val song:Song = songs[position]

        //holder.txtName?.text = song.name
        //holder.txtArtist?.text = song.artistName

        if(position < songs.size){
            holder.txtName?.text = songs[position]["name"].toString()
            holder.txtArtist?.text = songs[position]["artistName"].toString()

            holder.searchResultLayout.setOnClickListener{v ->
                val intent = Intent(v.context, AudioPlayerService::class.java)
                intent.putExtra("map", songs[position])
                intent.putExtra("pos", position)
                intent.action = "chan"
                v.context.startService(intent)
            }
        }else if(position < (songs.size + albums.size)){
            val pos = position - songs.size
            val artistName = songs[pos]["artistName"].toString()
            val albumName = albums[pos]["name"].toString()
            val type = albums[pos]["type"].toString()
            val id = albums[pos]["id"].toString()

            holder.txtName?.text = albumName
            holder.txtArtist?.text = type
            //open AlbumFragment
            holder.searchResultLayout.setOnClickListener{v ->
                val transaction = fragmentManager.beginTransaction()
                transaction.add(android.R.id.content , CollectionFragment.newInstance(id, artistName, albumName))
                transaction.commit()
            }
        }else {
            val pos = position - (songs.size + albums.size)
            holder.txtName?.text = artists[pos]["name"].toString()
            holder.txtArtist?.text = "Artist"
            val id = artists[pos]["id"].toString()
            //open ArtistFragment
            holder.searchResultLayout.setOnClickListener{v ->
                val transaction = fragmentManager.beginTransaction()
                transaction.add(android.R.id.content , ArtistFragment.newInstance(id))
                transaction.commit()
            }
        }
    }
}