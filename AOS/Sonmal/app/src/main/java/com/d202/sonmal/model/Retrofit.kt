package com.d202.sonmal.model

import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit.retrofit
import com.d202.sonmal.model.api.TextMacroApi

object Retrofit {
    private val retrofit = ApplicationClass.retrofit

    val textMacroApi: TextMacroApi by lazy {
        retrofit.create(TextMacroApi::class.java)
    }
}