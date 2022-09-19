package com.android.signlanguage

import androidx.lifecycle.ViewModel

interface ViewModelInitListener {
    var viewModelInitialized: ((viewModel: ViewModel)->Unit)?
}
