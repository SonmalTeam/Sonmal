package com.d202.sonmal.utils.sharedpref

import com.d202.sonmal.common.ApplicationClass

class SettingsPreference {
    companion object

    fun getFirstRunCheck(): Boolean{
        return ApplicationClass.firstRunCheck.getBoolean("State", true)
    }

    fun setFirstRunCheck(accept: Boolean){
        ApplicationClass.firstRunCheck.edit().putBoolean("State", accept).apply()
    }
}