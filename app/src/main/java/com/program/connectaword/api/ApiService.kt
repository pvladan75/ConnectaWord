package com.program.connectaword.api

import com.program.connectaword.data.AuthResponse
import com.program.connectaword.data.CreateRoomRequest
import com.program.connectaword.data.LoginRequest
import com.program.connectaword.data.RegisterRequest
import com.program.connectaword.data.RoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("rooms")
    suspend fun getRooms(): Response<List<RoomResponse>>

    @POST("create-room")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<RoomResponse>
}