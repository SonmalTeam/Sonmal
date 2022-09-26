package com.d202.sonmal.model

import com.d202.sonmal.common.ApplicationClass
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request().newBuilder()
                .addHeader("JWT-AUTHENTICATION", ApplicationClass.mainPref.token ?: "A")
                .build()


        return chain.proceed(request)
    }

}