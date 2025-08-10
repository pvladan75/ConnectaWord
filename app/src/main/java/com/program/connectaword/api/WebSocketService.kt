package com.program.connectaword.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebSocketService {
    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

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
                        _messages.emit(frame.readText())
                    }
                }
            }
        } catch (e: Exception) {
            _messages.emit("Error: ${e.message}")
        }
    }

    suspend fun sendMessage(message: String) {
        session?.send(Frame.Text(message))
    }

    // Add the 'suspend' keyword here
    suspend fun disconnect() {
        session?.close()
        session = null
    }
}