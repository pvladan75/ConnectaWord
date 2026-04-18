package com.program.connectaword.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class LocalRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val hostId: String,
    val hostRating: Int,
    val hostUsername: String,
    val language: String,
    val createdAt: String
)