package com.d202.sonmal.model.api

import com.d202.sonmal.model.dto.MacroDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MacroApi {

    @POST("/???") //예시
    suspend fun addMacro(@Body string: String): Response<Void>

    @GET("sign/macro/category/{categorySeq}")
    suspend fun getMacroList(@Path("categorySeq") categorySeq: Int): Response<MutableList<MacroDto>>

}