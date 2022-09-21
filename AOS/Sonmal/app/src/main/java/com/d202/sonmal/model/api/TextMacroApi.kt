package com.d202.sonmal.model.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TextMacroApi {

    @POST("/???") //todo 예시
    suspend fun addTextMacro(@Body string: String): Response<Void>

}