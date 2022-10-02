package com.d202.sonmal.model.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.d202.sonmal.R
import com.d202.sonmal.ui.MainActivity
import com.d202.sonmal.utils.sharedpref.SettingsPreference
import com.gun0912.tedpermission.provider.TedPermissionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "CallReceiver"

class CallReceiver : BroadcastReceiver() {
    private var phoneState: String? = ""
    private var phoneNum: String? = ""
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("android.intent.action.PHONE_STATE")) {
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
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1500)
                        val phoneNo = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        val tManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        var myNumber = tManager.getLine1Number()
                        if(phoneNo != myNumber) {
                            Log.d(TAG, "통화벨 울리는중")

                            val notiManager =
                                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val notiChannel = NotificationChannel(
                                "sonmal",
                                "sonmal",
                                NotificationManager.IMPORTANCE_HIGH
                            )
                            notiManager.createNotificationChannel(notiChannel)

                            Log.d(TAG, "onReceive: ${phoneNo}")
                            val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
                                putExtra("PHONE", phoneNo)
                            }
                            val fullScreenPendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                fullScreenIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )

                            val notificationBuilder =
                                NotificationCompat.Builder(context, notiChannel.id)
                                    .setSmallIcon(R.mipmap.ic_launcher_sonmal_foreground)
                                    .setContentTitle("Sonmal 전화")
                                    .setContentText(phoneNo)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .addAction(
                                        R.mipmap.ic_launcher_sonmal_foreground,
                                        "Call",
                                        fullScreenPendingIntent
                                    )
                                    .setCategory(NotificationCompat.CATEGORY_CALL)
                                    .setFullScreenIntent(fullScreenPendingIntent, true)

                            val incomingCallNotification = notificationBuilder.build()
                            Log.d(TAG, "onReceive: phoneNo ${phoneNo}")
                            if(phoneNo != null) {
                                SettingsPreference().setCallNumber(phoneNo!!)
                            }

                            notiManager.notify(1, incomingCallNotification)
                        }
                    }

                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.d(TAG, "통화종료 혹은 통화벨 종료")
                }
                Log.d(TAG, "phone state : $state")
                Log.d(TAG, "phone currentPhonestate : $phoneState")
            }
        }
    }
}