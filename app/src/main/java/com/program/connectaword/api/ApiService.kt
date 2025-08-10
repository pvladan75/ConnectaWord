package com.program.connectaword.api

import com.program.connectaword.data.AuthResponse
import com.program.connectaword.data.LoginRequest
import com.program.connectaword.data.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

}