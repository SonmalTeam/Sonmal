package com.d202.sonmal.model.api

import com.d202.sonmal.model.dto.TokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST("jwt/refresh")
    suspend fun refreshToken(@Body tokens: TokenDto): Response<TokenDto>
}