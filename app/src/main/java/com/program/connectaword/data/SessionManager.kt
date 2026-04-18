package com.program.connectaword.data

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.sessionDataStore by dataStore(
    fileName = "session_data.json",
    serializer = SessionSerializer
)

class SessionManager(private val context: Context) {

    val sessionFlow: Flow<SessionData> = context.sessionDataStore.data

    suspend fun saveSession(token: String, user: User) {
        context.sessionDataStore.updateData { currentData ->
            currentData.copy(
                authToken = token,
                user = user
            )
        }
        UserManager.currentUser = user
    }

    suspend fun saveIpAddress(ip: String) {
        context.sessionDataStore.updateData { currentData ->
            currentData.copy(lastUsedIp = ip)
        }
    }

    fun getLastUsedIp(): String? = runBlocking {
        sessionFlow.first().lastUsedIp
    }

    fun getActiveToken(): String? = runBlocking {
        sessionFlow.first().authToken
    }

    fun getActiveUser(): User? = runBlocking {
        sessionFlow.first().user
    }

    suspend fun clearSession() {
        context.sessionDataStore.updateData { currentData ->
            // Brišemo token i korisnika, ali čuvamo IP adresu
            currentData.copy(
                authToken = null,
                user = null
            )
        }
        UserManager.currentUser = null
    }

    fun loadUserFromSession() {
        val user = getActiveUser()
        if (user != null) {
            UserManager.currentUser = user
        }
    }
}