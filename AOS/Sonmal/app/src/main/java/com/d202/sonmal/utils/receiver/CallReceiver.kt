package com.d202.sonmal.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.ui.MainActivity
import com.gun0912.tedpermission.provider.TedPermissionProvider
import kotlinx.coroutines.Runnable

private const val TAG = "CallReceiver"

class CallReceiver : BroadcastReceiver() {
    private var phoneState: String? = ""
    private var phoneNum: String? = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("android.intent.action.PHONE_STATE")) {
            val crun = Runnable {
                val intentPhoneCall = Intent(context, MainActivity::class.java)
                intentPhoneCall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intentPhoneCall.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context!!.startActivity(intentPhoneCall)
            }
            resultData
            val handler = Handler(context!!.mainLooper)
            handler.postDelayed(crun, 1000)

            Log.d(TAG, "onReceive: Coroutine-----------")
            val i = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context.applicationContext.startActivity(i)
            }
            val telephonyManager =
                TedPermissionProvider.context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val extras = intent!!.extras
            if (extras != null) {
                val state = extras.getString(TelephonyManager.EXTRA_STATE) // 현재 폰 상태 가져옴
                if (state == phoneState) {
                    return
                } else {
                    phoneState = state
                }
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    val phoneNo = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    phoneNum = phoneNo
                    Log.d(TAG, phoneNo + "currentNumber")
                    Log.d(TAG, "통화벨 울리는중")


                    // telephonyManager.acceptRingingCall(); 전화 받기 함수이다.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    telephonyManager.endCall(); 전화 끊기, 거절 함수이다.
//                }
                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    telephonyManager.endCall()
//                    val packageManager = context!!.packageManager
//                    val intent =
//                        packageManager.getLaunchIntentForPackage("com.d202.sonmal")!!.apply {
//                            putExtra("PHONE", phoneNum)
//                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
//                                    Intent.FLAG_ACTIVITY_NEW_TASK or
//                                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                        }
//
//
//                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//                    context!!.startActivity(intent)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(1000)
//                        context!!.startActivity(intent)
//
//                    }

                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.d(TAG, "통화종료 혹은 통화벨 종료")
                    val packageManager = context!!.packageManager
//                    val intent = packageManager.getLaunchIntentForPackage("com.d202.sonmal", "com.d202.sonmal.ui.MainActivity")!!.apply {
//                        putExtra("PHONE", phoneNum)
//                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
//                                Intent.FLAG_ACTIVITY_NEW_TASK or
//                                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                    }
//                    context!!.startActivity(intent)
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(2000)
//                        context!!.startActivity(intent)
//
//                    }



                }
                Log.d(TAG, "phone state : $state")
                Log.d(TAG, "phone currentPhonestate : $phoneState")
            }
        }
    }
}