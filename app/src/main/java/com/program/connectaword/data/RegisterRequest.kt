package com.program.connectaword.data

data class RegisterRequest(
    val korisnickoIme: String,
    val email: String,
    val lozinka: String
)