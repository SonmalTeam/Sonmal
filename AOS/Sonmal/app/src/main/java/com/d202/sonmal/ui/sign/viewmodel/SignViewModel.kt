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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "SignViewModel"
class SignViewModel: ViewModel() {


    private val _refreshExpire = MutableLiveData<Boolean>()
    val refreshExpire: LiveData<Boolean>
        get() = _refreshExpire

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
                ApplicationClass.jwtFlag = false
                Retrofit.signApi.joinWithKakao(token).let {
                    if(it.isSuccessful && it.body() != null){
                        _isJoinSucced.postValue(true)
                        _jwtToken.postValue(it.body()!!.accessToken)
                        _refreshtoken.postValue(it.body()!!.refreshToken)
                        ApplicationClass.jwtFlag = true
                    }
                    else {
                        _isJoinSucced.postValue(false)
                        ApplicationClass.jwtFlag = true
                    }
                }
            } catch (e: Exception){
                Log.d(TAG, "joinWithKaKao : error ${e.message}")
                ApplicationClass.jwtFlag = true
            }
        }

    }

    fun joinWithNaver(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ApplicationClass.jwtFlag = false
                Retrofit.signApi.joinWithNaver(token).let {
                    if(it.isSuccessful && it.body() != null){
                        _isJoinSucced.postValue(true)
                        _jwtToken.postValue(it.body()!!.accessToken)
                        _refreshtoken.postValue(it.body()!!.refreshToken)
                        ApplicationClass.jwtFlag = true
                    }
                    else if (it.code() == 500) {
                        _isJoinSucced.postValue(false)
                        ApplicationClass.jwtFlag = true
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "joinWithNaver : error ${e.message}")
                ApplicationClass.jwtFlag = true
            }
        }
    }

    fun unregister() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Retrofit.signApi.unregister().let {
                    if(it.isSuccessful){
                        _unregisterCallBack.postValue(true)
                    }
                    else if(it.code() == 401) {
                        runBlocking {
                            try {
                                var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                                val response = Retrofit.tokenApi.refreshToken(tokens)
                                if(response.isSuccessful && response.body() != null) {
                                    ApplicationClass.mainPref.token = response.body()!!.accessToken
                                    ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                    unregister()
                                } else {
                                    _refreshExpire.postValue(true)
                                }

                            } catch (e: Exception) {
                                _unregisterCallBack.postValue(false)
                                _refreshExpire.postValue(true)
                            }
                        }
                    } else {
                        _unregisterCallBack.postValue(false)
                    }
                }

            } catch (e: Exception) {
                Log.d(TAG, "unregister : error ${e.message}")
            }
        }

    }

}