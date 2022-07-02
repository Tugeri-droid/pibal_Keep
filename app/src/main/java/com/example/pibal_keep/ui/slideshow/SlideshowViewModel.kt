package com.example.pibal_keep.ui.slideshow

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tutorial Penggunaan"
    }
    val text: LiveData<String> = _text
}