package com.d202.sonmal.model

import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit.retrofit
import com.d202.sonmal.model.api.MacroApi
import com.d202.sonmal.model.api.SignApi
import com.d202.sonmal.model.api.TokenApi

object Retrofit {
    private val retrofit = ApplicationClass.retrofit

    val macroApi: MacroApi by lazy {
        retrofit.create(MacroApi::class.java)
    }

    val signApi: SignApi by lazy {
        retrofit.create(SignApi::class.java)
    }

    val tokenApi: TokenApi by lazy {
        retrofit.create(TokenApi::class.java)
    }
}