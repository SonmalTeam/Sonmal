package com.d202.sonmal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.d202.sonmal.R
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.databinding.ActivityMainBinding
import com.d202.sonmal.ui.call.CallFragment
import com.d202.sonmal.utils.sharedpref.SettingsPreference
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.kakao.sdk.common.util.Utility

private const val TAG ="MainActivity"
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

        if(ApplicationClass.mainPref.refreshToken != null) {
            Log.d("refresh", "refresh ${ApplicationClass.mainPref.refreshToken}")
        }

//        SettingsPreference().setCallNumber("01012341234")
        val phone = SettingsPreference().getCallNumber()
        if(phone != ""){            
            SettingsPreference().setCallNumber("")
            startCall(phone)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val telephonyManager =
            TedPermissionProvider.context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        telephonyManager.endCall()
        val phone = intent?.getStringExtra("PHONE").toString()
        Log.d(TAG, "onNewIntent: $phone")

        startCall(phone)
    }

    private fun startCall(phone: String){
        supportFragmentManager.beginTransaction().replace(R.id.frame_main, CallFragment().apply {
            arguments = bundleOf("PHONE" to phone)
        }).commit()
        Log.d(TAG, "startCall: $phone")

    }
}