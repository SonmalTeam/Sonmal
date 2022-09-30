package com.d202.sonmal.model.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.api.MacroApi
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import kotlinx.coroutines.runBlocking
import retrofit2.Response

private const val TAG ="MacroDataSource"
private const val START_PAGE_INDEX = 0
class MacroDataSource(private val macroApi: MacroApi, private val categorySeq: Int): PagingSource<Int, MacroDto>() {
    init {
        Log.d(TAG, "MacroDataSource init")

    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MacroDto> {
        return try {
            Log.d(TAG, "MacroDataSource Load1 ${params.key}")
            val page = params.key?: START_PAGE_INDEX
            var response: Response<PagingResult<MacroDto>>?
            Log.d(TAG, "getPageMacroList start 값들 $categorySeq, $page, ${params.loadSize}")
            response = macroApi.getPageMacroList(categorySeq, page, 7)
            var body = response.body()
            Log.d(TAG, "getPageMacroList end $body")
            if(response.isSuccessful && body != null){
                Log.d(TAG, "getPageMacroList success $body")
                LoadResult.Page(
                    data = body.result,
                    prevKey = if(page == 0) null else page -1,
                    nextKey = if(page == body.totalPage) null else page +1
                )
            } else if (response.code() == 401) {
                runBlocking {
                    try {
                        Log.d(TAG, "refreshToken tokens ${ApplicationClass.mainPref.token} ${ApplicationClass.mainPref.refreshToken}")
                        var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                        val response = Retrofit.tokenApi.refreshToken(tokens)
                        if(response.isSuccessful && response.body() != null) {
                            Log.d(TAG, "refreshToken success ${response.body()}")
                            ApplicationClass.mainPref.token = response.body()!!.accessToken
                            ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                            body = macroApi.getPageMacroList(categorySeq, page, 7).body()
                        } else {
                            Log.d(TAG, "refreshToken err ${response.code()}")
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "e: ${e.message}")
                    }
                    LoadResult.Page(
                        data = body!!.result,
                        prevKey = if(page == 0) null else page -1,
                        nextKey = if(page == body!!.totalPage) null else page +1
                    )
                }
            } else {
                Log.d(TAG, "getPageMacroList fail ${response.code()}")
                LoadResult.Page(
                    data = body!!.result,
                    prevKey = null,
                    nextKey = null
                )
            }
        }catch (e: java.lang.Exception){
            Log.d(TAG, "load error: ${e.message}")
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, MacroDto>): Int? {
        TODO("Not yet implemented")
    }
}