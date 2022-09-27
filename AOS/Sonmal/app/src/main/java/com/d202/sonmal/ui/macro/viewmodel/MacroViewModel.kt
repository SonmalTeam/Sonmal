package com.d202.sonmal.ui.macro.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.paging.MacroDataSource
import com.d202.sonmal.utils.FormDataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val _macroAddCallback = MutableLiveData<String>()
    val macroAddCallback: LiveData<String>
        get() = _macroAddCallback

    fun getMacroList(userSeq: Int, category: Int) { // 카테고리의 매크로 전체 리스트 불러오기
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //todo userSeq, category api로 보내기
                val response = Retrofit.macroApi.getMacroList(category)
                Log.d(TAG, "response: ${response.body()}")
                if(response.isSuccessful && response.body() != null){
                    _macroList.postValue(response.body() as MutableList<MacroDto>)
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
                        FormDataUtil.getVideoBody("videoFileId", video)
                    } else {
                        null
                    }
                )

                if(response.isSuccessful && response.body() != null){
                } else {
                    _macroAddCallback.postValue(emoji)
                }
            }catch (e: Exception) {
                Log.d(TAG, "addMacro: ${e.message}")
            }
        }

    }
}