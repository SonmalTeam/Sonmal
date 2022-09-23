package com.d202.sonmal.utils

import android.graphics.Bitmap

interface CustomFrameAvailableListener {
    fun onFrame(bitmap: Bitmap)
}