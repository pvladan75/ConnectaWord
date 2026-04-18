package com.program.connectaword.repository

import com.program.connectaword.api.ApiService
import com.program.connectaword.data.CreateRoomRequest
import com.program.connectaword.data.RoomResponse
import retrofit2.Response

import javax.inject.Inject

import com.program.connectaword.data.db.dao.RoomDao
import com.program.connectaword.data.db.entities.LocalRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LobbyRepository {
    fun getAllRooms(): Flow<List<RoomResponse>>
    suspend fun refreshRooms(): Result<Unit>
    suspend fun createRoom(createRoomRequest: CreateRoomRequest): Response<RoomResponse>
}

class LobbyRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val roomDao: RoomDao
) : LobbyRepository {

    override fun getAllRooms(): Flow<List<RoomResponse>> {
        return roomDao.getAllRooms().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshRooms(): Result<Unit> {
        return try {
            val response = apiService.getRooms()
            if (response.isSuccessful && response.body() != null) {
                val entities = response.body()!!.map { it.toEntity() }
                roomDao.updateRooms(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch rooms from server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRoom(createRoomRequest: CreateRoomRequest): Response<RoomResponse> {
        val response = apiService.createRoom(createRoomRequest)
        if (response.isSuccessful && response.body() != null) {
            roomDao.insertRooms(listOf(response.body()!!.toEntity()))
        }
        return response
    }
}

// Mapper extensions
fun RoomResponse.toEntity() = LocalRoomEntity(
    id = id,
    name = name,
    hostId = hostId,
    hostRating = hostRating,
    hostUsername = hostUsername,
    language = language,
    createdAt = createdAt
)

fun LocalRoomEntity.toDomain() = RoomResponse(
    id = id,
    name = name,
    hostId = hostId,
    hostRating = hostRating,
    hostUsername = hostUsername,
    language = language,
    createdAt = createdAt
)