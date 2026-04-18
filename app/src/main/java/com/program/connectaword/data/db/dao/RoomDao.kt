package com.program.connectaword.data.db.dao

import androidx.room.*
import com.program.connectaword.data.db.entities.LocalRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT * FROM rooms ORDER BY createdAt DESC")
    fun getAllRooms(): Flow<List<LocalRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<LocalRoomEntity>)

    @Query("DELETE FROM rooms")
    suspend fun clearRooms()

    @Transaction
    suspend fun updateRooms(rooms: List<LocalRoomEntity>) {
        clearRooms()
        insertRooms(rooms)
    }
}