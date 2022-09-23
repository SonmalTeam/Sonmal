package com.d202.sonmal.model

import com.d202.sonmal.common.ApplicationClass
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", ApplicationClass.mainPref.token ?: "")
            .build()

        return chain.proceed(request)
    }

}