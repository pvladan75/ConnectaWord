package com.program.connectaword.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val lozinka: String
)