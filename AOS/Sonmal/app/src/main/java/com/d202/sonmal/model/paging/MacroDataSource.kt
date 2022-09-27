package com.d202.sonmal.model.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.d202.sonmal.model.api.MacroApi
import com.d202.sonmal.model.dto.MacroDto
import retrofit2.Response

private const val TAG ="MacroDataSource"
class MacroDataSource(private val macroApi: MacroApi, private val userSeq: Int): PagingSource<Int, MacroDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MacroDto> {
        return try {

            val page = params.key?: 0
            var response: Response<PagingResult<MacroDto>>?
            response = macroApi.getPageMacroList(userSeq, page)
            val body = response.body()
            if(response.isSuccessful && body != null){
                LoadResult.Page(
                    data = body.result,
                    prevKey = if(page == 0) null else page -1,
                    nextKey = if(page == body.totalPage) null else page +1
                )
            }else {
                LoadResult.Page(
                    data = body!!.result,
                    prevKey = null,
                    nextKey = null
                )
            }
        }catch (e: java.lang.Exception){
            Log.d(TAG, "load: ${e.message}")
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, MacroDto>): Int? {
        TODO("Not yet implemented")
    }
}