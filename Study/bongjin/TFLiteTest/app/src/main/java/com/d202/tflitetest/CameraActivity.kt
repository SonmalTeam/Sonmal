package com.d202.tflitetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.container, Camera2BasicFragment()).commit()
    }
}