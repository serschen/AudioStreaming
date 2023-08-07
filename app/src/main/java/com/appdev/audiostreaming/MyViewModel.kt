package com.appdev.audiostreaming

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class MyViewModel: ViewModel() {
    var currentPlaylist: MutableLiveData<ArrayList<HashMap<String, Any>>> = MutableLiveData()
    var position: MutableLiveData<Int> = MutableLiveData()
    var isPlaying: MutableLiveData<Boolean> = MutableLiveData()
    var theme: MutableLiveData<Themes> = MutableLiveData()

    init {
        getAllSongs()
        position.value = 0
        isPlaying.value = false
    }

    fun getAllSongs(){
        FirebaseFunctions.getInstance()
            .getHttpsCallable("getAllSongs?userId=" + Firebase.auth.currentUser?.uid)
            .call()
            .addOnFailureListener {
                Log.wtf("tag", it)
            }
            .addOnSuccessListener {
                val itemList:ArrayList<HashMap<String, Any>> = it.data as ArrayList<HashMap<String, Any>>
                currentPlaylist.value = itemList
            }
    }

    fun changeTheme(theme:Themes){
        this.theme.value = theme
    }
}