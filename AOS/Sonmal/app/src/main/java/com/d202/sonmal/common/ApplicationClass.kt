package com.d202.sonmal.common

import android.app.Application
import android.util.Log
import com.d202.sonmal.model.AuthInterceptor
import com.d202.sonmal.utils.MainSharedPreference
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationClass: Application() {

    companion object{
        lateinit var retrofit: Retrofit
        lateinit var mainPref: MainSharedPreference
        var jwtFlag : Boolean = true
    }

    override fun onCreate() {
        super.onCreate()

        // Preference 초기화
        mainPref = MainSharedPreference(applicationContext)

        // token 적용을 위한 intercepter 사용
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()).build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                if (original.url.encodedPath.equals("/api/user/kakao/login", false)
                    || original.url.encodedPath.equals("/api/user/naver/login", true)
                ) {
                    Log.d("jwt","jwt 없음")
                    chain.proceed(original)
                } else {
                    Log.d("jwt","jwt 있음 ${original.url.encodedPath}")
                    chain.proceed(original.newBuilder().apply {
                        addHeader("JWT-AUTHENTICATION", ApplicationClass.mainPref.token ?: "A")
                    }.build())

                }
            }.build()

        // retrofit 초기화
        retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //kakao 로그인 위한 SDK 초기화
        KakaoSdk.init(this, NATIVE_APP_KEY)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)
    }
}