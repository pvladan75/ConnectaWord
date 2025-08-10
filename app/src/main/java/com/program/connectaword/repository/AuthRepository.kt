package com.program.connectaword.repository

import com.program.connectaword.api.ApiService
import com.program.connectaword.data.AuthResponse
import com.program.connectaword.data.LoginRequest
import com.program.connectaword.data.RegisterRequest
import retrofit2.Response

// Definišemo interfejs da bismo lakše testirali i menjali implementacije
interface AuthRepository {
    suspend fun register(registerRequest: RegisterRequest): Response<AuthResponse>
    suspend fun login(loginRequest: LoginRequest): Response<AuthResponse>
}

// Kreiramo konkretnu implementaciju koja koristi naš ApiService
class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun register(registerRequest: RegisterRequest): Response<AuthResponse> {
        return apiService.register(registerRequest)
    }

    override suspend fun login(loginRequest: LoginRequest): Response<AuthResponse> {
        return apiService.login(loginRequest)
    }
}