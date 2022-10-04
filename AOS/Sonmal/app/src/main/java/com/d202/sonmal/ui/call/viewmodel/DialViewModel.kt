package com.d202.sonmal.ui.call.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialViewModel: ViewModel() {
    private val _showDial = MutableLiveData<Boolean>(true)
    val showDial : LiveData<Boolean>
        get() = _showDial
    fun dialVisibility(flag: Boolean){
        _showDial.postValue(flag)
    }
}