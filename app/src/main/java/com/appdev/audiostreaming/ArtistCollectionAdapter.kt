package com.appdev.audiostreaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArtistCollectionAdapter(private val data:ArrayList<HashMap<String, Any>>): RecyclerView.Adapter<ArtistCollectionAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById<ImageView>(R.id.imgArtistCollection)
        val txtName: TextView = itemView.findViewById<TextView>(R.id.txtCollectionName)
        val rvSongs: RecyclerView = itemView.findViewById<RecyclerView>(R.id.rvArtistSongs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_layout, parent, false)
        return ArtistCollectionAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = data[position]["collection"] as HashMap<String, Any>
        holder.txtName.text = map["name"].toString()

        val itemList = data[position]["songs"] as ArrayList<HashMap<String, Any>>

        val songAdapter = SongAdapter(itemList, false)

        holder.rvSongs.adapter = songAdapter
    }
}