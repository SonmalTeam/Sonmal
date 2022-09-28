package com.d202.sonmal.ui.sign.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.TokenDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "SignViewModel"
class SignViewModel: ViewModel() {

    private val _refreshtoken = MutableLiveData<String>() // 소셜로그인 성공 시 콜백
    val refreshtoken: LiveData<String>
        get() = _refreshtoken

    private val _jwtToken = MutableLiveData<String>() //jwt 저장
    val jwtToken: LiveData<String>
        get() = _jwtToken


    private val _isJoinSucced = MutableLiveData<Boolean>() // 회원 가입 처리 콜백
    val isJoinSucced: LiveData<Boolean>
        get() = _isJoinSucced

    private val _unregisterCallBack = MutableLiveData<Boolean>() // 회원 탈퇴 콜백
    val unregisterCallBack: LiveData<Boolean>
        get() = _unregisterCallBack

    fun refresh() {
        _isJoinSucced.value = false
        _unregisterCallBack.value = false
    }

    fun joinWithKaKao(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "joinWithKaKao api 시작 token : $token")
                ApplicationClass.jwtFlag = false
                Retrofit.signApi.joinWithKakao(token).let {
                    if(it.isSuccessful && it.body() != null){
                        Log.d(TAG, "joinwithkakao 통신 성공 ${it.body()}")
                        _isJoinSucced.postValue(true)
                        Log.d(TAG, "_jwtToken1 ${it.body()}")
                        _jwtToken.postValue(it.body()!!.accessToken)
                        _refreshtoken.postValue(it.body()!!.refreshToken)
                        ApplicationClass.jwtFlag = true

                    }
                    else {
                        Log.d(TAG, "joinWithKaKao api 통신 실패 ${it.body()}")
                        _isJoinSucced.postValue(false)
                        ApplicationClass.jwtFlag = true
                    }
                }
            } catch (e: Exception){
                Log.d(TAG, "kakao login: error ${e.message}")
                ApplicationClass.jwtFlag = true
            }
        }

    }

    fun joinWithNaver(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ApplicationClass.jwtFlag = false
                Log.d(TAG, "joinWithNaver api 시작 token : $token")
                Retrofit.signApi.joinWithNaver(token).let {
                    if(it.isSuccessful && it.body() != null){
                        Log.d(TAG, "joinwithNaver 통신 성공 ${it.body()}")
                        _isJoinSucced.postValue(true)
                        Log.d(TAG, "_jwtToken1 ${it.body()}")
                        _jwtToken.postValue(it.body()!!.accessToken)
                        _refreshtoken.postValue(it.body()!!.refreshToken)
                        ApplicationClass.jwtFlag = true
                    }
                    else if (it.code() == 500) {
                        Log.d(TAG, "joinWithNaver api 통신 실패")
                        _isJoinSucced.postValue(false)
                        ApplicationClass.jwtFlag = true
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "naver login: error ${e.message}")
                ApplicationClass.jwtFlag = true
            }
        }
    }

    fun unregister() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "unregister api 시작 token ${ApplicationClass.mainPref.token} flag ${ApplicationClass.jwtFlag}")
                Retrofit.signApi.unregister().let {
                    if(it.isSuccessful){
                        Log.d(TAG, "unregister 통신 성공 ${it.body()}")
                        _unregisterCallBack.postValue(true)
                    }
                    else if(it.code() == 500) {
                        runBlocking {
                            try {
                                Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                                var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                                val response = Retrofit.tokenApi.refreshToken(tokens)
                                if(response.isSuccessful && response.body() != null) {
                                    Log.d(TAG, "refreshToken sucess ${response.body()}")
                                    ApplicationClass.mainPref.token = response.body()!!.accessToken
                                    ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                    unregister()
                                } else {
                                    Log.d(TAG, "refreshToken err ${response.code()}")
                                }

                            } catch (e: Exception) {
                                Log.d(TAG, "e: ${e.message}")
                            }
                        }
                    } else {
                        Log.d(TAG, "unregister 실패 error ${it.code()}")

                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "unregister : error ${e.message}")
            }
        }

    }

}