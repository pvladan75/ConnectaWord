package com.program.connectaword.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.program.connectaword.App // <-- ДОДАЈ ОВАЈ IMPORT
import com.program.connectaword.data.*
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebSocketService {
    private val _messages = MutableSharedFlow<GameMessage>()
    val messages = _messages.asSharedFlow()

    private val gson = Gson()
    private val client = HttpClient {
        install(WebSockets)
    }

    private var session: WebSocketSession? = null

    suspend fun connect(roomId: String) {
        try {
            // 👇 КЉУЧНА ИЗМЕНА ЈЕ ОВДЕ 👇
            // 1. Узимамо токен из SessionManager-а
            val token = App.instance.sessionManager.getActiveToken()
            if (token == null) {
                println("WebSocket connection error: No active token found!")
                // Овде бисмо могли емитовати и неку грешку ка UI-у
                return
            }

            session = client.webSocketSession {
                val ipAddress = ServerConfig.serverIp
                // 2. Додајемо токен као query параметар у URL
                url("ws://$ipAddress:8080/ws/game/$roomId?token=$token")
            }

            session?.let {
                for (frame in it.incoming) {
                    if (frame is Frame.Text) {
                        val jsonString = frame.readText()
                        try {
                            val gameMessage = when {
                                jsonString.contains("gameState") -> gson.fromJson(jsonString, GameStateUpdate::class.java)
                                jsonString.contains("message") -> gson.fromJson(jsonString, Announcement::class.java)
                                else -> null
                            }
                            gameMessage?.let { msg -> _messages.emit(msg) }
                        } catch (e: JsonSyntaxException) {
                            println("Error parsing WebSocket message: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("WebSocket connection error: ${e.message}")
        }
    }

    suspend fun sendMessage(message: GameMessage) {
        val jsonString = gson.toJson(message)
        session?.send(Frame.Text(jsonString))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}