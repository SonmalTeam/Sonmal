package com.d202.sonmal.model.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.d202.sonmal.R
import com.d202.sonmal.ui.MainActivity
import com.d202.sonmal.utils.sharedpref.SettingsPreference
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

                            val notiManager =
                                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val notiChannel = NotificationChannel(
                                "sonmal_channel",
                                "sonmal_channel",
                                NotificationManager.IMPORTANCE_HIGH
                            )
                            notiManager.createNotificationChannel(notiChannel)

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
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Sonmal 전화")
                                    .setContentText(phoneNo)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .addAction(
                                        R.mipmap.ic_launcher,
                                        "전화 받기",
                                        fullScreenPendingIntent
                                    )
                                    .setCategory(NotificationCompat.CATEGORY_CALL)
                                    .setFullScreenIntent(fullScreenPendingIntent, true)

                            val incomingCallNotification = notificationBuilder.build()
                            if(phoneNo != null) {
                                SettingsPreference().setCallNumber(phoneNo!!)
                            }

                            notiManager.notify(100, incomingCallNotification)
                        }
                    }

                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                }
            }
        }
    }
}