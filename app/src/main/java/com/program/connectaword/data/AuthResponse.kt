package com.program.connectaword.data

data class AuthResponse(
    val token: String,
    val korisnik: User
)

data class User(
    val id: String,
    val korisnickoIme: String,
    val email: String
)