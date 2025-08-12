package com.program.connectaword

import android.app.Application
import com.program.connectaword.data.SessionManager

class App : Application() {
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionManager = SessionManager(applicationContext)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}