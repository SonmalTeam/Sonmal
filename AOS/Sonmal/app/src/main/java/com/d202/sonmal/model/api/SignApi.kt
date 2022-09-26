package com.d202.sonmal.model.api

import com.d202.sonmal.model.dto.TokenDto
import com.d202.sonmal.model.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface SignApi {

    @POST("user/kakao/login")
    suspend fun joinWithKakao(@Body token: String): Response<TokenDto>

    @POST("user/naver/login")
    suspend fun joinWithNaver(@Body token: String): Response<TokenDto>

    @DELETE("user")
    suspend fun unregister(): Response<Void>
}