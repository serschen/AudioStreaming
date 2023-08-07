package com.appdev.audiostreaming

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class ArtistCollectionAdapter(private val supportFragmentManager: FragmentManager, private val viewModel: MyViewModel, private val data:ArrayList<HashMap<String, Any>>): RecyclerView.Adapter<ArtistCollectionAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById(R.id.imgArtistCollection)
        val txtName: TextView = itemView.findViewById(R.id.txtCollectionName)
        val rvSongs: RecyclerView = itemView.findViewById(R.id.rvArtistSongs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val map = data[position]["collection"] as HashMap<String, Any>
        holder.txtName.text = map["name"].toString()

        val itemList = data[position]["songs"] as ArrayList<HashMap<String, Any>>

        val songAdapter = SongAdapter(supportFragmentManager, viewModel, itemList, false)

        holder.rvSongs.adapter = songAdapter

        val storageReference = FirebaseStorage.getInstance().reference
        val photoReference = storageReference.child(map["imagePath"].toString())

        val ONE_MEGABYTE = (1024 * 1024).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.img.setImageBitmap(bmp)
        }.addOnFailureListener {
            Log.wtf("song adapter", "No Such file or Path found!!")
        }
    }
}