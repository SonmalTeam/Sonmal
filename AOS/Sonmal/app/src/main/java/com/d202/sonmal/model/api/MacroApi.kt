package com.d202.sonmal.model.api

import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.model.paging.PagingResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MacroApi {


    @GET("sign/macro/category/{categorySeq}")
    suspend fun getMacroList(@Path("categorySeq") categorySeq: Int): Response<MutableList<MacroDto>>

    @GET("sign/macro/category/{categorySeq}")
    suspend fun getPageMacroList(@Path("categorySeq") categorySeq: Int, @Query("page")page: Int, @Query("size")size: Int): Response<PagingResult<MacroDto>>

    @GET("sign/macro/search")
    suspend fun getPageMacroSearchList(@Query("keyword") keyword: String, @Query("page")page: Int, @Query("size")size: Int): Response<PagingResult<MacroDto>>

    @Multipart
    @POST("sign/macro/video")
    suspend fun addMacro(
        @Part title: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part categorySeq: MultipartBody.Part,
        @Part icon: MultipartBody.Part,
        @Part videoFileId: MultipartBody.Part?
//        @PartMap data: HashMap<String, RequestBody>
    ): Response<Void>

    @POST("sign/macro")
    suspend fun addMacroNull(@Query("categorySeq")categorySeq: Int, @Query("title")title: String, @Query("content")content: String, @Query("icon")icon: String): Response<Void>

    @GET("sign/macro/video/{videoFileId}")
    suspend fun getVideo(@Path("videoFileId") videoFileId: Int): Response<String>

    @POST("jwt/refresh")
    suspend fun refreshToken(@Body tokens: TokenDto) :Response<TokenDto>

    @DELETE("sign/macro/{signMacroSeq}")
    suspend fun deleteMacro(@Path("signMacroSeq") signMacroSeq: Int): Response<Void>

    @PUT("sign/macro/{signMacroSeq}")
    suspend fun modifyCategoryMAcro(@Path("signMacroSeq") macroSeq1: Int,
                                    @Query("signMacroSeq") macroSeq2: Int, @Query("categorySeq") categorySeq: Int
    ): Response<Void>

}