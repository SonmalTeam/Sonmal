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


    fun setCallNumber(number: String){
        ApplicationClass.callPref.edit().putString("PHONE", number).apply()
    }

    fun getCallNumber(): String{
        return ApplicationClass.callPref.getString("PHONE", "").toString()
    }

    fun setUseCall(use: Boolean){
        ApplicationClass.useCallPref.edit().putBoolean("UseCall", use).apply()
    }
    fun getUseCall(): Boolean{
        return ApplicationClass.useCallPref.getBoolean("UseCall", false)
    }
}