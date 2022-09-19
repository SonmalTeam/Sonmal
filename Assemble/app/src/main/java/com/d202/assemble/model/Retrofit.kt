package com.d202.assemble.model

import com.d202.assemble.common.ApplicationClass
import com.d202.assemble.model.Retrofit.retrofit
import com.d202.assemble.model.api.TextMacroApi

object Retrofit {
    private val retrofit = ApplicationClass.retrofit

    val textMacroApi: TextMacroApi by lazy {
        retrofit.create(TextMacroApi::class.java)
    }
}