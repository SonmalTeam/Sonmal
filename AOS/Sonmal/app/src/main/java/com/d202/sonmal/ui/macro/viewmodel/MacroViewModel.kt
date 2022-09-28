package com.d202.sonmal.ui.macro.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.model.paging.MacroDataSource
import com.d202.sonmal.utils.FormDataUtil
import com.kakao.sdk.common.KakaoSdk.type
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.refreshToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val TAG = "MacroViewModel"
class MacroViewModel: ViewModel() {

    private val macroListPage = MutableLiveData<Int>()
    val pagingMacroList = macroListPage.switchMap {
        getPagingMacroList(it).cachedIn(viewModelScope)
    }

    private fun getPagingMacroList(userSeq: Int) = Pager(
        config = PagingConfig(pageSize = 1, maxSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {MacroDataSource(Retrofit.macroApi, userSeq)}
    ).liveData

    fun getPagingMacroListValue(userSeq: Int){
        getPagingMacroList(userSeq)
    }

    private val _macroList = MutableLiveData<MutableList<MacroDto>>()
    val macroList: LiveData<MutableList<MacroDto>>
        get() = _macroList

    private val _macroAddCallback = MutableLiveData<Int>()
    val macroAddCallback: LiveData<Int>
        get() = _macroAddCallback

    private val _getVideoCallback = MutableLiveData<String>()
    val getVideoCallback: LiveData<String>
        get() = _getVideoCallback

    fun getMacroList(category: Int) { // 카테고리의 매크로 전체 리스트 불러오기
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //todo userSeq, category api로 보내기
                Log.d(TAG, "getMacroList token: ${ApplicationClass.mainPref.token}")
                val response = Retrofit.macroApi.getMacroList(category)
                Log.d(TAG, "getMacroList response: ${response.body()}")
                if(response.isSuccessful && response.body() != null){
                    _macroList.postValue(response.body() as MutableList<MacroDto>)
                } else if(response.code() == 500) {
                    runBlocking {
                        try {
                            Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                Log.d(TAG, "refreshToken sucess ${response.body()}")
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                getMacroList(category)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                        }
                    }
                } else {
                    //todo 지우기 test 용
                    val temp = mutableListOf<MacroDto>(
                        MacroDto(1,1, "테스트1", "테스트1내용","1","category", "icon"),
                        MacroDto(1,1, "테스트2", "테스트2내용","1","category", "icon2"),
                    )
                    _macroList.postValue(temp)
                }

            }catch (e: Exception){
                Log.d(TAG, "getList error: ${e.message}")
            }
        }
    }

    fun addMacro(title: String, content: String, category: String, emoji: String, video: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "macro add start $title $emoji")
            try {
                val response = Retrofit.macroApi.addMacro(
                    FormDataUtil.getBody("title", title),
                    FormDataUtil.getBody("content", content),
                    FormDataUtil.getBody("categorySeq", category),
                    FormDataUtil.getBody("icon", emoji),
                    if(video != null) {
                        FormDataUtil.getVideoBody("file", video)
                    } else {
                        MultipartBody.Part.createFormData(
                            name = "file",
                            filename = "빈 파일",
                            body = File("").asRequestBody()
                        )
                    }
                )

                if(response.isSuccessful && response.body() != null){
                    Log.d(TAG, "macro add success")
                    _macroAddCallback.postValue(200)

                } else if(response.code() == 500) {
                    Log.d(TAG, "refresh")

                    runBlocking {
                        try {
                            Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                Log.d(TAG, "refreshToken sucess ${response.body()}")
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                addMacro(title, content, category, emoji, video)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                        }
                    }
//                    refreshToken("getMacroList", category)
                } else {
                    Log.d(TAG, "addMacro fail : ${response.code()}")
                    _macroAddCallback.postValue(400)
                }
            }catch (e: Exception) {
                Log.d(TAG, "addMacro error: ${e.message}")
            }
        }

    }

    fun addMacroNull() {

    }

    fun getVideo(videoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Retrofit.macroApi.getVideo(videoId)
                if(response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "getVideo success: ${response.body()}")
                    _getVideoCallback.postValue(response.body())
                } else if(response.code() == 500) {
                    runBlocking {
                        try {
                            Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                Log.d(TAG, "refreshToken sucess ${response.body()}")
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                getVideo(videoId)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                        }
                    }
//                    refreshToken("getMacroList", category)
                } else {
                    Log.d(TAG, "getVideo fail: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.d(TAG, "getVideo err: ${e.message}")

            }
        }
    }

    fun refreshToken(type: String, value: Any){
        Log.d(TAG, "refreshToken")
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "refreshToken viewModelScope")
            try {
                Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                val response = Retrofit.tokenApi.refreshToken(tokens)
                if(response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "refreshToken sucess ${response.body()}")
                    ApplicationClass.mainPref.token = response.body()!!.accessToken
                    ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                    if(type == "getMacroList") {
                        getMacroList(value as Int)
                    }
                } else {
                    Log.d(TAG, "refreshToken err ${response.code()}")
                }

            } catch (e: Exception) {
                Log.d(TAG, "e: ${e.message}")
            }
        }
    }
}