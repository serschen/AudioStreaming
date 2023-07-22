package com.appdev.audiostreaming

import android.os.Parcel
import android.os.Parcelable

class Song(
    val collection: String?, val artist: String?, val artistName: String?, val path: String?,
    val name: String?, val length:Int) {

}