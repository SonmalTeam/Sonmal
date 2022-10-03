package com.d202.sonmal.common

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.d202.sonmal.model.AuthInterceptor
import com.d202.sonmal.utils.MainSharedPreference
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.tensorflow.lite.Interpreter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationClass: Application() {

    companion object{
        lateinit var retrofit: Retrofit
        lateinit var mainPref: MainSharedPreference
        var jwtFlag : Boolean = true
        lateinit var interpreter: Interpreter
        val classes = arrayListOf("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ",
            "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "ㅏ", "ㅐ",
            "ㅑ", "ㅓ", "ㅔ", "ㅕ", "ㅗ", "ㅛ", "ㅜ", "ㅠ", "ㅡ", "ㅣ")

        lateinit var firstRunCheck: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        // 첫 실행 체크
        firstRunCheck = getSharedPreferences("FirstRun", MODE_PRIVATE)

        // Preference 초기화
        mainPref = MainSharedPreference(applicationContext)

        // token 적용을 위한 intercepter 사용
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()).build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                if (original.url.encodedPath.equals("/api/user/kakao/login", true)
                    || original.url.encodedPath.equals("/api/user/naver/login", true)
                    || original.url.encodedPath.equals("/api/jwt/refresh", true)
                ) {
                    chain.proceed(original)
                } else {
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