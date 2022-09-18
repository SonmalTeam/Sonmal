package com.android.signlanguage.ui.detailed_signs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.signlanguage.model.Language

class DetailedSignsViewModel : ViewModel() {
    private val _signsDrawables = MutableLiveData(Language.drawables.zip(Language.detailedDrawables))
    val signsDrawables: LiveData<List<Pair<Int, Int>>> = _signsDrawables
}