package com.d202.sonmal.model.paging

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.d202.sonmal.common.ApplicationClass
import com.d202.sonmal.model.Retrofit
import com.d202.sonmal.model.api.MacroApi
import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.ui.macro.MacroCafeFragment
import com.d202.sonmal.ui.macro.MacroCafeFragmentDirections
import com.d202.sonmal.ui.macro.viewmodel.MacroViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.Response

private const val TAG ="MacroDataSource"
private const val START_PAGE_INDEX = 0
class MacroDataSource(private val macroApi: MacroApi, private val categorySeq: Int, private val viewModel: MacroViewModel): PagingSource<Int, MacroDto>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MacroDto> {
        return try {
            val page = params.key?: START_PAGE_INDEX
            var response: Response<PagingResult<MacroDto>>?
            Log.d(TAG, "MacroDataSource 요청 $categorySeq $page")
            response = macroApi.getPageMacroList(categorySeq, page, 7)
            var body = response.body()
            if(response.isSuccessful && body != null && body.totalPage > 0){
                Log.d(TAG, "MacroDataSource ${response.body()}")
                LoadResult.Page(
                    data = body.result,
                    prevKey = if(page == 0) null else page -1,
                    nextKey = if(page == body.totalPage) null else page +1
                )
            } else if (response.code() == 401) {
                runBlocking {
                    try {
                        Log.d(TAG, "MacroDataSource  401")
                        var tokens = TokenDto(ApplicationClass.mainPref.token!!, ApplicationClass.mainPref.refreshToken!!)
                        val response = Retrofit.tokenApi.refreshToken(tokens)
                        if(response.isSuccessful && response.body() != null) {
                            ApplicationClass.mainPref.token = response.body()!!.accessToken
                            ApplicationClass.mainPref.refreshToken = response.body()!!.refreshToken
                            body = macroApi.getPageMacroList(categorySeq, page, 7).body()
                        } else {
                            Log.d(TAG, "refreshToken err ${response.code()}")
                            viewModel.pushRefreshExpire()
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "e: ${e.message}")
                        viewModel.pushRefreshExpire()
                    }
                }
                LoadResult.Page(
                    data = body!!.result,
                    prevKey = if(page == 0) null else page -1,
                    nextKey = if(page == body!!.totalPage-1) null else page +1
                )
            } else {
                Log.d(TAG, "getPageMacroList fail ${response.code()}")
                LoadResult.Page(
                    data = body!!.result,
                    prevKey = if(page == 0) null else page -1,
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