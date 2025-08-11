package com.program.connectaword.data

import kotlinx.serialization.Serializable

// This class is for sending a request to create a new room
@Serializable
data class CreateRoomRequest(
    val name: String,
    val hostId: String,
    val language: String,
    val wordSource: String
)

// This class represents a single game room received from the server
@Serializable
data class RoomResponse(
    val id: String,
    val name: String,
    val hostId: String,
    val createdAt: String
)