package com.program.connectaword.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.api.RetrofitInstance
import com.program.connectaword.api.WebSocketService
import com.program.connectaword.data.CreateRoomRequest
import com.program.connectaword.data.RoomResponse
import com.program.connectaword.data.UserManager
import com.program.connectaword.repository.LobbyRepository
import com.program.connectaword.repository.LobbyRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LobbyState(
    val isLoading: Boolean = false,
    val rooms: List<RoomResponse> = emptyList(),
    val error: String? = null
)

data class CreateRoomState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class LobbyViewModel : ViewModel() {
    private val lobbyRepository: LobbyRepository = LobbyRepositoryImpl(RetrofitInstance.api)
    private val webSocketService = WebSocketService()
    private var webSocketJob: Job? = null

    private val _lobbyState = MutableStateFlow(LobbyState())
    val lobbyState: StateFlow<LobbyState> = _lobbyState

    private val _createRoomState = MutableStateFlow(CreateRoomState())
    val createRoomState: StateFlow<CreateRoomState> = _createRoomState

    private val _webSocketMessages = MutableStateFlow<List<String>>(emptyList())
    val webSocketMessages = _webSocketMessages.asStateFlow()

    init {
        getRooms()
        observeWebSocketMessages()
    }

    private fun observeWebSocketMessages() {
        viewModelScope.launch {
            webSocketService.messages.collect { message ->
                val currentMessages = _webSocketMessages.value.toMutableList()
                currentMessages.add(message)
                _webSocketMessages.value = currentMessages
            }
        }
    }

    fun joinRoom(roomId: String) {
        webSocketJob?.cancel()
        _webSocketMessages.value = emptyList()
        webSocketJob = viewModelScope.launch {
            webSocketService.connect(roomId)
        }
    }

    fun getRooms() {
        viewModelScope.launch {
            _lobbyState.value = LobbyState(isLoading = true)
            try {
                val response = lobbyRepository.getRooms()
                if (response.isSuccessful && response.body() != null) {
                    _lobbyState.value = LobbyState(rooms = response.body()!!)
                } else {
                    _lobbyState.value = LobbyState(error = "Failed to fetch rooms")
                }
            } catch (e: Exception) {
                _lobbyState.value = LobbyState(error = e.message ?: "An unknown error occurred")
            }
        }
    }

    fun createRoom(roomName: String, language: String, wordSource: String) {
        val currentUserId = UserManager.currentUser?.id
        if (currentUserId == null) {
            _createRoomState.value = CreateRoomState(error = "User not logged in.")
            return
        }

        viewModelScope.launch {
            _createRoomState.value = CreateRoomState(isLoading = true)
            val request = CreateRoomRequest(
                name = roomName,
                hostId = currentUserId,
                language = language,
                wordSource = wordSource
            )
            try {
                val response = lobbyRepository.createRoom(request)
                if (response.isSuccessful) {
                    _createRoomState.value = CreateRoomState(success = true)
                    getRooms() // Refresh the room list after creating a new one
                } else {
                    _createRoomState.value = CreateRoomState(error = "Failed to create room")
                }
            } catch (e: Exception) {
                _createRoomState.value = CreateRoomState(error = e.message ?: "An unknown error occurred")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            webSocketService.disconnect()
        }
    }
}