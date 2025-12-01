package com.example.pinenenas.ui.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnnouncementViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is announcement Fragment"
    }
    val text: LiveData<String> = _text
}