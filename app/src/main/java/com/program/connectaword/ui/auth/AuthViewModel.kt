package com.program.connectaword.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.api.RetrofitInstance
import com.program.connectaword.data.LoginRequest // Potreban import
import com.program.connectaword.data.RegisterRequest
import com.program.connectaword.repository.AuthRepository
import com.program.connectaword.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Proširujemo AuthState
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val isLoginSuccessful: Boolean = false // NOVO
)

class AuthViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepositoryImpl(RetrofitInstance.api)

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    // POSTOJEĆA register FUNKCIJA (bez izmena)
    fun register(korisnickoIme: String, email: String, lozinka: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val request = RegisterRequest(korisnickoIme, email, lozinka)
                val response = authRepository.register(request)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState(isRegistrationSuccessful = true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Nepoznata greška"
                    _authState.value = AuthState(error = errorMsg)
                }
            } catch (e: IOException) {
                _authState.value = AuthState(error = "Greška sa mrežom: ${e.message}")
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Došlo je do greške: ${e.message}")
            }
        }
    }

    // NOVA login FUNKCIJA
    fun login(email: String, lozinka: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val request = LoginRequest(email, lozinka)
                val response = authRepository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    // Ovde bismo sačuvali token iz response.body().token
                    _authState.value = AuthState(isLoginSuccessful = true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Netačan email ili lozinka"
                    _authState.value = AuthState(error = errorMsg)
                }
            } catch (e: IOException) {
                _authState.value = AuthState(error = "Greška sa mrežom: ${e.message}")
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Došlo je do greške: ${e.message}")
            }
        }
    }
}