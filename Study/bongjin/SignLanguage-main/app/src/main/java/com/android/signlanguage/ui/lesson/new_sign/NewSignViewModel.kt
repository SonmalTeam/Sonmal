package com.android.signlanguage.ui.lesson.new_sign

import androidx.lifecycle.*
import com.android.signlanguage.FinishedListener

class NewSignViewModel(signInput: Char) : ViewModel(), FinishedListener {

    private val _finished = MutableLiveData<Boolean?>()
    override val finished: LiveData<Boolean?> = _finished

    private val _sign = MutableLiveData<Char>()
    val sign: LiveData<Char> = _sign
    val signString = Transformations.map(sign) {
        it.toString()
    }

    init {
        _sign.value = signInput
    }

    fun finish() {
        _finished.value = true
    }
}

class NewSignViewModelFactory(private val _sign: Char) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewSignViewModel(_sign) as T
    }
}