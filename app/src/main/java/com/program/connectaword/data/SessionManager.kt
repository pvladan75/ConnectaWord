package com.program.connectaword.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson // <-- КОРИСТИМО GSON УМЕСТО KOTLINX.SERIALIZATION

class SessionManager(context: Context) {

    private val gson = Gson() // Креирамо инстанцу Gson-а

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
    }

    fun saveSession(token: String, user: User) {
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_TOKEN, token)

        // Користимо Gson да претворимо User објекат у JSON стринг
        val userJson = gson.toJson(user)
        editor.putString(USER_DATA, userJson)
        editor.apply()

        // Такође ажурирамо наш UserManager
        UserManager.currentUser = user
    }

    fun getActiveToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    fun getActiveUser(): User? {
        val userJson = sharedPreferences.getString(USER_DATA, null)
        return userJson?.let {
            try {
                // Користимо Gson да претворимо JSON стринг назад у User објекат
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        UserManager.currentUser = null
    }
}