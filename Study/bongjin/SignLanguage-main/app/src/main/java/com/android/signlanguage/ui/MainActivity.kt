package com.android.signlanguage.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.signlanguage.R

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("mediapipe_jni")
            System.loadLibrary("opencv_java3")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}