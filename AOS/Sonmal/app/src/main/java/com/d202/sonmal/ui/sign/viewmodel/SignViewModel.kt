package com.d202.sonmal.ui.sign.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.model.Retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private const val TAG = "SignViewModel"
class SignViewModel: ViewModel() {

    private val _accessToken = MutableLiveData<String>() // 소셜로그인 성공 시 콜백
    val accessToken: LiveData<String>
        get() = _accessToken

    private val _jwtToken = MutableLiveData<Int>() //jwt 저장
    val jwtToken: LiveData<Int>
        get() = _jwtToken

    fun setAccessToken(token: String) {
        _accessToken.postValue(token)
    }

    fun setJwtToken(token: Int) {
        _jwtToken.postValue(token)
    }


    private val _isJoinSucced = MutableLiveData<Boolean>() // 회원 가입 처리 콜백
    val isJoinSucced: LiveData<Boolean>
        get() = _isJoinSucced

    fun refresh() {
        _isJoinSucced.value = false
    }

    fun joinWithKaKao(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "joinWithKaKao api 시작 token : $token")
                Retrofit.signApi.joinWithKakao(token).let {
                    if(it.isSuccessful && it.body() != null){
                        Log.d(TAG, "joinwithkakao 통신 성공 ${it.body()}")
                        _isJoinSucced.postValue(true)
                        Log.d(TAG, "_jwtToken1 ${it.body()!!.seq}")
                        _jwtToken.postValue(it.body()!!.seq)
                        Log.d(TAG, "_jwtToken2 ${jwtToken.value}")
                    }
                    else {
                        Log.d(TAG, "joinWithKaKao api 통신 실패 ${it.body()}")
                        _isJoinSucced.postValue(false)
                    }
                }
            } catch (e: Exception){
                Log.d(TAG, "kakao login: error ${e.message}")
            }
        }
    }

    fun joinWithNaver(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "joinWithNaver api 시작 token : $token")
                Retrofit.signApi.joinWithNaver(token).let {
                    if(it.isSuccessful && it.body() != null){
                        Log.d(TAG, "joinWithNaver 통신 성공 ${it.body()}")
                        _isJoinSucced.postValue(true)
                        _jwtToken.postValue(it.body()!!.seq)
                    }
                    else if (it.code() == 500) {
                        Log.d(TAG, "joinWithNaver api 통신 실패")
                        _isJoinSucced.postValue(false)
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "naver login: error ${e.message}")
            }
        }
    }

    fun unregister(token: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "unregister api 시작 token : $token")
                Retrofit.signApi.unregister(token).let {
                    if(it.isSuccessful){
                        Log.d(TAG, "unregister 통신 성공 ${it.body()}")
                    }
                    else  {
                        Log.d(TAG, "unregister 실패")
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "unregister : error ${e.message}")
            }
        }

    }

}