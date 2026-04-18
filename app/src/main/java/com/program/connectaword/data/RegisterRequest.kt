package com.program.connectaword.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val korisnickoIme: String,
    val email: String,
    val lozinka: String
)