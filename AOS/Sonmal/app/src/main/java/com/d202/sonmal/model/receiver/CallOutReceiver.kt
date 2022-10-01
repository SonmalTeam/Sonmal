package com.d202.sonmal.model.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.d202.sonmal.ui.MainActivity
import com.gun0912.tedpermission.provider.TedPermissionProvider

private const val TAG = "CallOutReceiver"

class CallOutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val telephonyManager =
            TedPermissionProvider.context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        telephonyManager.endCall()

        val tManager = context!!.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        var number = tManager.getLine1Number()
        if (number.startsWith("+82")) {
            number = number.replace("+82", "0");
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java).apply {
                putExtra("PHONE", number)
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        pendingIntent.send()
        Log.d(TAG, "onReceive: 전화 걸기 $number")
    }
}