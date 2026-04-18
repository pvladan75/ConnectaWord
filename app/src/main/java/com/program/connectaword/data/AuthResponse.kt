package com.program.connectaword.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val korisnik: User
)

@Serializable
data class User(
    val id: String,
    val korisnickoIme: String,
    val email: String,
    val rating: Int
)