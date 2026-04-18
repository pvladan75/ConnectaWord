package com.program.connectaword.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.data.SessionManager
import com.program.connectaword.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    fun getLastUsedIp(): String? = sessionManager.getLastUsedIp()

    fun getActiveUser(): User? = sessionManager.getActiveUser()

    fun saveIpAddress(ip: String) {
        viewModelScope.launch {
            sessionManager.saveIpAddress(ip)
        }
    }
}