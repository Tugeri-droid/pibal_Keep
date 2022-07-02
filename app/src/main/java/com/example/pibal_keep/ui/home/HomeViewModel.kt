package com.example.pibal_keep.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "PENCATAT PIBAL"
    }
    val text: LiveData<String> = _text
}