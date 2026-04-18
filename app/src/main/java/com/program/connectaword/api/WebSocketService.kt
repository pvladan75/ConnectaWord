package com.program.connectaword.api

import com.program.connectaword.data.Announcement
import com.program.connectaword.data.GameMessage
import com.program.connectaword.data.GameStateUpdate
import com.program.connectaword.data.SessionManager
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketService @Inject constructor(
    private val sessionManager: SessionManager,
    private val json: Json,
    private val client: HttpClient
) {
    private val _messages = MutableSharedFlow<GameMessage>(replay = 1)
    val messages = _messages.asSharedFlow()

    private var session: WebSocketSession? = null

    suspend fun connect(roomId: String) {
        try {
            val token = sessionManager.getActiveToken()
            if (token == null) {
                println("WebSocket connection error: No active token found!")
                return
            }

            session = client.webSocketSession {
                val ipAddress = ServerConfig.serverIp
                url("ws://$ipAddress:8080/ws/game/$roomId?token=$token")
            }

            session?.let { socketSession ->
                for (frame in socketSession.incoming) {
                    if (frame is Frame.Text) {
                        val jsonString = frame.readText()
                        try {
                            val gameMessage: GameMessage? = when {
                                jsonString.contains("gameState") -> json.decodeFromString<GameStateUpdate>(jsonString)
                                jsonString.contains("message") -> json.decodeFromString<Announcement>(jsonString)
                                else -> null
                            }
                            gameMessage?.let { msg -> _messages.emit(msg) }
                        } catch (e: Exception) {
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
        val jsonString = json.encodeToString(message)
        session?.send(Frame.Text(jsonString))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}