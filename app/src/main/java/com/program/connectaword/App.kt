package com.program.connectaword

import android.app.Application
import com.program.connectaword.api.ApiClient
import com.program.connectaword.data.SessionManager

class App : Application() {

    lateinit var sessionManager: SessionManager
        private set

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(applicationContext)

        sessionManager.loadUserFromSession()

        // 👇 НОВА ЛОГИКА ЗА АУТОМАТСКУ ИНИЦИЈАЛИЗАЦИЈУ 👇
        val lastIp = sessionManager.getLastUsedIp()
        if (lastIp != null) {
            ApiClient.initialize(lastIp)
        }
    }
}