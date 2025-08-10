package com.program.connectaword.repository

import com.program.connectaword.api.ApiService
import com.program.connectaword.data.CreateRoomRequest
import com.program.connectaword.data.RoomResponse
import retrofit2.Response

interface LobbyRepository {
    suspend fun getRooms(): Response<List<RoomResponse>>
    suspend fun createRoom(createRoomRequest: CreateRoomRequest): Response<Unit>
}

class LobbyRepositoryImpl(
    private val apiService: ApiService
) : LobbyRepository {
    override suspend fun getRooms(): Response<List<RoomResponse>> {
        return apiService.getRooms()
    }

    override suspend fun createRoom(createRoomRequest: CreateRoomRequest): Response<Unit> {
        return apiService.createRoom(createRoomRequest)
    }
}