package com.program.connectaword.repository

import com.program.connectaword.api.ApiService
import com.program.connectaword.data.AuthResponse
import com.program.connectaword.data.LoginRequest
import com.program.connectaword.data.RegisterRequest
import com.program.connectaword.data.SessionManager
import retrofit2.Response

import javax.inject.Inject

interface AuthRepository {
    suspend fun register(registerRequest: RegisterRequest): Response<AuthResponse>
    suspend fun login(loginRequest: LoginRequest): Response<AuthResponse>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(registerRequest: RegisterRequest): Response<AuthResponse> {
        val response = apiService.register(registerRequest)
        // Ako je registracija uspešna, odmah čuvamo sesiju (korisnik je ulogovan)
        if (response.isSuccessful) {
            response.body()?.let { authResponse ->
                sessionManager.saveSession(authResponse.token, authResponse.korisnik)
            }
        }
        return response
    }

    override suspend fun login(loginRequest: LoginRequest): Response<AuthResponse> {
        val response = apiService.login(loginRequest)
        // Ako je prijava uspešna, čuvamo sesiju
        if (response.isSuccessful) {
            response.body()?.let { authResponse ->
                sessionManager.saveSession(authResponse.token, authResponse.korisnik)
            }
        }
        return response
    }
}