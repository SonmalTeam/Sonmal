package com.android.signlanguage

import androidx.lifecycle.LiveData

interface FinishedListener {
    val finished: LiveData<Boolean?>
}
