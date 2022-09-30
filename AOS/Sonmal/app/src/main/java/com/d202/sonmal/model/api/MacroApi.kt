package com.d202.sonmal.model.api

import com.d202.sonmal.model.dto.MacroDto
import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.model.paging.PagingResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MacroApi {


    @GET("sign/macro/category/{categorySeq}")
    suspend fun getMacroList(@Path("categorySeq") categorySeq: Int): Response<MutableList<MacroDto>>

    @GET("sign/macro/category/{categorySeq}")
    suspend fun getPageMacroList(@Path("categorySeq") categorySeq: Int, @Query("page")page: Int, @Query("size")size: Int): Response<PagingResult<MacroDto>>

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

    @POST("sign/macros")
    suspend fun addMacroNull(@Body newMacro: MacroDto): Response<Void>

    @GET("sign/macro/video/{videoFileId}")
    suspend fun getVideo(@Path("videoFileId") videoFileId: Int): Response<String>

    @POST("jwt/refresh")
    suspend fun refreshToken(@Body tokens: TokenDto) :Response<TokenDto>
}