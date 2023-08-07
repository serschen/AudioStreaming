package com.appdev.audiostreaming

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class SearchAdapter(private val songs:ArrayList<HashMap<String, Any>>,
                    private val albums:ArrayList<HashMap<String, Any>>,
                    private val artists:ArrayList<HashMap<String, Any>>,
                    private val fragmentManager:FragmentManager,
                    private val viewModel: MyViewModel
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
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
        return this.songs.size + this.albums.size + this.artists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val song:Song = songs[position]

        //holder.txtName?.text = song.name
        //holder.txtArtist?.text = song.artistName

        if(position < songs.size && songs.size != 0){
            holder.txtName?.text = songs[position]["name"].toString()
            holder.txtArtist?.text = songs[position]["artistName"].toString()
            setImage(holder, songs[position]["imagePath"].toString())

            holder.searchResultLayout.setOnClickListener{v ->
                viewModel.currentPlaylist.value = songs
                viewModel.position.value = position
                viewModel.isPlaying.value = true
                val position = viewModel.position.value
                viewModel.title.value = position?.let { viewModel.currentPlaylist.value?.get(it)?.get("name").toString() }
                viewModel.artist.value = position?.let { viewModel.currentPlaylist.value?.get(it)?.get("artistName").toString() }
            }
        }else if(position < (songs.size + albums.size) && albums.size != 0){
            val pos = position - songs.size
            val artistName = albums[pos]["artistName"].toString()
            val albumName = albums[pos]["name"].toString()
            val type = albums[pos]["type"].toString()
            val id = albums[pos]["id"].toString()
            setImage(holder, albums[pos]["imagePath"].toString())

            holder.txtName?.text = albumName
            holder.txtArtist?.text = type
            //open AlbumFragment
            holder.searchResultLayout.setOnClickListener{
                val transaction = fragmentManager.beginTransaction()
                transaction.add(android.R.id.content , CollectionFragment.newInstance(id, artistName, albumName))
                transaction.commit()
            }
        }else if(artists.size != 0){
            val pos = position - (songs.size + albums.size)
            holder.txtName?.text = artists[pos]["name"].toString()
            holder.txtArtist?.text = "Artist"
            val id = artists[pos]["id"].toString()
            setImage(holder, artists[pos]["imagePath"].toString())
            //open ArtistFragment
            holder.searchResultLayout.setOnClickListener{
                val transaction = fragmentManager.beginTransaction()
                transaction.add(android.R.id.content , ArtistFragment.newInstance(id))
                transaction.commit()
            }
        }
    }

    fun setImage(holder:ViewHolder, path:String){
        val storageReference = FirebaseStorage.getInstance().reference
        val photoReference = storageReference.child(path)

        val ONE_MEGABYTE = (1024 * 1024).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.img.setImageBitmap(bmp)
        }.addOnFailureListener {
            Log.wtf("song adapter", "No Such file or Path found!!")
        }
    }
}