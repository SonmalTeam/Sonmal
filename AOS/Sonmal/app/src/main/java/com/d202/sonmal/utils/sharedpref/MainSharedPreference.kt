package com.d202.sonmal.utils.sharedpref

import android.content.Context.MODE_PRIVATE
import android.content.Context

class MainSharedPreference(context: Context) {
    private val prefName = "mainPreference"
    private val prefs = context.getSharedPreferences(prefName, MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("token", null)
        set(value) {
            prefs.edit().putString("token", value).apply()
        }

    var refreshToken: String?
        get() = prefs.getString("refreshToken", null)
        set(value) {
            prefs.edit().putString("refreshToken", value).apply()
        }

    var loginPlatform: Int?
        get() = prefs.getInt("loginPlatform", 0)
        set(value) {
            prefs.edit().putInt("loginPlatform", value!!).apply()
        }

}