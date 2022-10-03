package com.d202.sonmal.ui.macro.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.model.paging.MacroDataSource
import com.d202.sonmal.utils.FormDataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val TAG = "MacroViewModel"
class MacroViewModel: ViewModel() {

    private val _refreshExpire = MutableLiveData<Boolean>()
    val refreshExpire: LiveData<Boolean>
        get() = _refreshExpire

    private val macroListPage = MutableLiveData<Int>()
    val pagingMacroList = macroListPage.switchMap {
        getPagingMacroList(it).cachedIn(viewModelScope)
    }

    fun pushRefreshExpire() {
        _refreshExpire.postValue(true)
    }

    fun getPagingMacroListValue(categorySeq: Int){ // seq를 입력하면 Pager 데이터로 변환
        macroListPage.postValue(categorySeq)
    }

    private fun getPagingMacroList(categorySeq: Int) =
         Pager( // Pager로 데이터 변환
            config = PagingConfig(pageSize = 1, maxSize = 9, enablePlaceholders = false),
            pagingSourceFactory = {
                MacroDataSource(Retrofit.macroApi, categorySeq, this@MacroViewModel)
            }
        ).liveData



    private val _macroList = MutableLiveData<MutableList<MacroDto>>()
    val macroList: LiveData<MutableList<MacroDto>>
        get() = _macroList

    private val _macroAddCallback = MutableLiveData<Int>()
    val macroAddCallback: LiveData<Int>
        get() = _macroAddCallback

    private val _getVideoCallback = MutableLiveData<String>()
    val getVideoCallback: LiveData<String>
        get() = _getVideoCallback

    private val _macroDeleteCallback = MutableLiveData<Int>()
    val macroDeleteCallback: LiveData<Int>
        get() = _macroDeleteCallback

    private val _flag = MutableLiveData<Boolean>()
    val flag : LiveData<Boolean>
        get() = _flag



    fun getMacroList(category: Int) { // 카테고리의 매크로 전체 리스트 불러오기
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //todo userSeq, category api로 보내기
                Log.d(TAG, "getMacroList")
                val response = Retrofit.macroApi.getMacroList(category)
                if(response.isSuccessful && response.body() != null){
                    Log.d(TAG, "getMacroList success")
                    _macroList.postValue(response.body() as MutableList<MacroDto>)
                } else if(response.code() == 401) {
                    runBlocking {
                        try {
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                getMacroList(category)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                                _refreshExpire.postValue(true)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
                        }
                        Log.d(TAG, "getMacroList fail ${response.code()}")
                    }
                } else {

                }

            }catch (e: Exception){
                Log.d(TAG, "getList error: ${e.message}")
            }
        }
    }

    fun addMacro(title: String, content: String, category: String, emoji: String, video: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG,"add macro 호출 $video")
            _flag.postValue(true)
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

                if(response.isSuccessful){
                    _macroAddCallback.postValue(200)
                    Log.d(TAG,"add macro 성공 ${response.code()}")
                }
                else if(response.code() == 401) {
                    Log.d(TAG,"add macro 권한 실패 ${response.code()}")
                    runBlocking {
                        try {
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                addMacro(title, content, category, emoji, video)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                                _refreshExpire.postValue(true)
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
                        }
                    }
//                    refreshToken("getMacroList", category)
                }
                else {
                    Log.d(TAG, "addMacro fail : ${response.code()}")
                    _macroAddCallback.postValue(response.code())
                }
                _flag.postValue(false)
            }catch (e: Exception) {
                Log.d(TAG, "addMacro error: ${e.message}")
                _macroAddCallback.postValue(700)
            }
        }

    }

    fun addMacroNull(title: String, content: String, category: Int, emoji: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "addMacroNull")
                val response = Retrofit.macroApi.addMacroNull(
                    category, title, content, emoji
                )

                if(response.isSuccessful){
                    _macroAddCallback.postValue(200)
                    Log.d(TAG, "addMacroNull 성공")
                } else if(response.code() == 401) {

                    runBlocking {
                        try {
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                addMacroNull(title, content, category, emoji)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                                _refreshExpire.postValue(true)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
                        }
                    }
                } else {
                    Log.d(TAG, "addMacroNull fail : ${response.code()}")
                    _macroAddCallback.postValue(400)
                }

            } catch (e : Exception) {

            }
        }
    }

    fun modifyCategryMacro(macroSeq: Int, categorySeq: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Retrofit.macroApi.modifyCategoryMAcro(
                    macroSeq, macroSeq, categorySeq
                )

                if(response.isSuccessful){
                    _macroAddCallback.postValue(200)
                } else if(response.code() == 401) {

                    runBlocking {
                        try {
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                modifyCategryMacro(macroSeq, categorySeq)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                                _refreshExpire.postValue(true)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
                        }
                    }
                } else {
                    Log.d(TAG, "modifyCategryMacro fail : ${response.code()}")
                    _macroAddCallback.postValue(400)
                }

            } catch (e : Exception) {

            }
        }
    }

    fun deleteMacro(macroSeq: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "deleteMacro seq : ${macroSeq}")
                val response = Retrofit.macroApi.deleteMacro(
                    macroSeq
                )

                if(response.isSuccessful){
                    Log.d(TAG, "deleteMacro fail : ${response.code()}")
                    _macroDeleteCallback.postValue(200)

                } else if(response.code() == 401) {

                    runBlocking {
                        try {
                            var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                            val response = Retrofit.tokenApi.refreshToken(tokens)
                            if(response.isSuccessful && response.body() != null) {
                                ApplicationClass.mainPref.token = response.body()!!.accessToken
                                ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                                deleteMacro(macroSeq)
                            } else {
                                Log.d(TAG, "refreshToken err ${response.code()}")
                                _refreshExpire.postValue(true)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
                        }
                    }
                } else {
                    Log.d(TAG, "deleteMacro fail : ${response.code()}")

                }

            } catch (e : Exception) {
                Log.d(TAG, "deleteMacro error : ${e.message}")
            }
        }
    }

    fun getVideo(videoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Retrofit.macroApi.getVideo(videoId)
                if(response.isSuccessful && response.body() != null) {
                    _getVideoCallback.postValue(response.body())
                } else if(response.code() == 401) {
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
                                _refreshExpire.postValue(true)
                            }

                        } catch (e: Exception) {
                            Log.d(TAG, "e: ${e.message}")
                            _refreshExpire.postValue(true)
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
                    _refreshExpire.postValue(true)
                }

            } catch (e: Exception) {
                Log.d(TAG, "e: ${e.message}")
                _refreshExpire.postValue(true)
            }
        }
    }
}