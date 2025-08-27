package com.program.connectaword.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val gson = Gson()

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val USER_DATA = "user_data"
        private const val SERVER_IP = "server_ip" // <-- НОВА КОНСТАНТА
    }

    fun saveSession(token: String, user: User) {
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_TOKEN, token)

        val userJson = gson.toJson(user)
        editor.putString(USER_DATA, userJson)
        editor.apply()

        UserManager.currentUser = user
    }

    // 👇 НОВА ФУНКЦИЈА ЗА ЧУВАЊЕ IP АДРЕСЕ 👇
    fun saveIpAddress(ip: String) {
        sharedPreferences.edit().putString(SERVER_IP, ip).apply()
    }

    // 👇 НОВА ФУНКЦИЈА ЗА ЧИТАЊЕ IP АДРЕСЕ 👇
    fun getLastUsedIp(): String? {
        return sharedPreferences.getString(SERVER_IP, null)
    }

    fun getActiveToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    fun getActiveUser(): User? {
        val userJson = sharedPreferences.getString(USER_DATA, null)
        return userJson?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        // Не бришемо IP адресу, да би је апликација запамтила и након одјаве
        editor.remove(AUTH_TOKEN)
        editor.remove(USER_DATA)
        editor.apply()
        UserManager.currentUser = null
    }

    fun loadUserFromSession() {
        val user = getActiveUser()
        if (user != null) {
            UserManager.currentUser = user
        }
    }
}