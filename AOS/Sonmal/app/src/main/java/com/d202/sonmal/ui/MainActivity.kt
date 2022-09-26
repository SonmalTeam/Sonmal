package com.d202.sonmal.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.d202.sonmal.R
import com.d202.sonmal.databinding.ActivityMainBinding

import com.d202.sonmal.ui.sign.LoginFragment
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.frame_main) as NavHostFragment
        navController = navHostFragment.navController

        //keyHash 구하기
        var keyHash = Utility.getKeyHash(this)
        Log.d("key", "해쉬 키 : ${keyHash}")

    }
}