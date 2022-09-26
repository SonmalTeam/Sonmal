package com.d202.sonmal.ui.macro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.utils.FormDataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val TAG = "MacroViewModel"
class MacroViewModel: ViewModel() {

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
                val response = Retrofit.macroApi.getMacroList(category)
                Log.d(TAG, "response: ${response.body()}")
                if(response.isSuccessful && response.body() != null){
                    _macroList.postValue(response.body() as MutableList<MacroDto>)
                } else if(response.code() == 500) {

                } else {
                    //todo 지우기 test 용
                    val temp = mutableListOf<MacroDto>(
                        MacroDto(1,1, "테스트1", "테스트1내용","1","category", "icon"),
                        MacroDto(1,1, "테스트2", "테스트2내용","1","category", "icon2"),
                    )
                    _macroList.postValue(temp)
                }

            }catch (e: Exception){
                Log.d(TAG, "getList: ${e.message}")
            }
        }
    }

    fun addMacro(title: String, content: String, category: String, emoji: String, video: File?) {
        viewModelScope.launch(Dispatchers.IO) {
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
                    _macroAddCallback.postValue(200)
                } else if(response.code() == 500) {
                    Log.d(TAG, "addMacro 500 : ${response.code()}")
                } else {
                    _macroAddCallback.postValue(400)
                    Log.d(TAG, "addMacro error : ${response.code()}")
                }
            }catch (e: Exception) {
                Log.d(TAG, "addMacro: ${e.message}")
            }
        }

    }

    fun getVideo(videoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Retrofit.macroApi.getVideo(videoId)
                if(response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "getVideo success: ${response.body()}")
                    _getVideoCallback.postValue(response.body())
                } else {
                    Log.d(TAG, "getVideo fail: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.d(TAG, "getVideo: ${e.message}")

            }
        }
    }
}