package com.appdev.audiostreaming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AudioServiceViewModel: ViewModel() {

    private val _currentTheme = MutableLiveData<Themes>()
    val liveData:LiveData<Themes> = _currentTheme

    fun change(t: Themes){
        _currentTheme.value=t
    }
}