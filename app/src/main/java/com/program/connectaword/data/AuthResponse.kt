package com.program.connectaword.data

import kotlinx.serialization.Serializable // <-- ДОДАЈ ОВАЈ IMPORT

@Serializable // <-- ДОДАЈ ОВУ АНОТАЦИЈУ
data class AuthResponse(
    val token: String,
    val korisnik: User
)

@Serializable // <-- И ОВДЕ ДОДАЈ АНОТАЦИЈУ
data class User(
    val id: String,
    val korisnickoIme: String,
    val email: String
)