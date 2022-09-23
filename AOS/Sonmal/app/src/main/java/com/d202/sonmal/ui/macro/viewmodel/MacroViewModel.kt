package com.d202.sonmal.ui.macro.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.dto.MacroDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val TAG = "MacroViewModel"
class MacroViewModel: ViewModel() {

    private val _macroList = MutableLiveData<MutableList<MacroDto>>()
    val macroList: LiveData<MutableList<MacroDto>>
        get() = _macroList

    fun getMacroList(userSeq: Int, category: Int){ // 카테고리의 매크로 전체 리스트 불러오기
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
                        MacroDto(1,1, "테스트2", "테스트1내용","1","category", "icon2"),
                    )
                    _macroList.postValue(temp)
                }

            }catch (e: Exception){
                Log.d(TAG, "getList: ${e.message}")
            }
        }
    }
}