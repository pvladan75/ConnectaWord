package com.program.connectaword.data

data class CreateRoomRequest(
    val name: String,
    val hostId: String,
    val language: String,
    val wordSource: String
)

data class RoomResponse(
    val id: String,
    val name: String,
    val hostId: String,
    val hostRating: Int,
    val hostUsername: String, // НОВО
    val language: String,     // НОВО
    val createdAt: String
)