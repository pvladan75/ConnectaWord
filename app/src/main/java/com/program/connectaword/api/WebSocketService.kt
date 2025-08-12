package com.program.connectaword.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
            session = client.webSocketSession {
                url("ws://10.0.2.2:8080/ws/game/$roomId")
            }
            session?.let {
                for (frame in it.incoming) {
                    if (frame is Frame.Text) {
                        val jsonString = frame.readText()
                        try {
                            // Покушавамо да десеријализујемо у познате типове
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