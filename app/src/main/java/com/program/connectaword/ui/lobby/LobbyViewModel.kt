package com.program.connectaword.ui.lobby

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.api.ApiClient
import com.program.connectaword.api.WebSocketService
import com.program.connectaword.data.*
import com.program.connectaword.repository.LobbyRepository
import com.program.connectaword.repository.LobbyRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LobbyState(
    val isLoading: Boolean = false,
    val rooms: List<RoomResponse> = emptyList(),
    val error: String? = null
)

data class CreateRoomState(
    val isLoading: Boolean = false,
    val createdRoom: RoomResponse? = null,
    val error: String? = null
)

class LobbyViewModel : ViewModel() {
    private val lobbyRepository: LobbyRepository by lazy { LobbyRepositoryImpl(ApiClient.getApiService()) }
    private val webSocketService = WebSocketService()
    private var webSocketJob: Job? = null

    private val _lobbyState = MutableStateFlow(LobbyState())
    val lobbyState: StateFlow<LobbyState> = _lobbyState

    private val _createRoomState = MutableStateFlow(CreateRoomState())
    val createRoomState: StateFlow<CreateRoomState> = _createRoomState

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState = _gameState.asStateFlow()

    private val _announcements = MutableStateFlow<List<String>>(emptyList())
    val announcements = _announcements.asStateFlow()

    init {
        getRooms()
        observeWebSocketMessages()
    }

    private fun observeWebSocketMessages() {
        viewModelScope.launch {
            webSocketService.messages.collect { gameMessage ->
                Log.d("LobbyViewModel", "Received GameMessage: $gameMessage")

                when (gameMessage) {
                    is GameStateUpdate -> {
                        _gameState.value = gameMessage.gameState
                    }
                    is Announcement -> {
                        _announcements.update { currentAnnouncements ->
                            listOf(gameMessage.message) + currentAnnouncements
                        }
                    }
                    else -> {
                        Log.w("LobbyViewModel", "Received unexpected message type: $gameMessage")
                    }
                }
            }
        }
    }

    fun joinRoom(roomId: String) {
        webSocketJob?.cancel()
        _gameState.value = null
        _announcements.value = emptyList()
        webSocketJob = viewModelScope.launch {
            webSocketService.connect(roomId)
        }
    }

    fun sendStartGameMessage() = sendMessage(StartGame())
    fun sendPlayAgainMessage() = sendMessage(PlayAgain())
    fun sendGuess(guess: String) = sendMessage(MakeGuess(guess))
    fun sendSurrenderMessage() = sendMessage(SurrenderRound())

    private fun sendMessage(message: GameMessage) {
        viewModelScope.launch {
            webSocketService.sendMessage(message)
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
            val request = CreateRoomRequest(name = roomName, hostId = currentUserId, language = language, wordSource = wordSource)
            try {
                val response = lobbyRepository.createRoom(request)
                if (response.isSuccessful && response.body() != null) {
                    _createRoomState.value = CreateRoomState(createdRoom = response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to create room"
                    _createRoomState.value = CreateRoomState(error = errorMsg)
                }
            } catch (e: Exception) {
                _createRoomState.value = CreateRoomState(error = e.message ?: "An unknown error occurred")
            }
        }
    }

    fun onRoomCreationHandled() {
        _createRoomState.value = CreateRoomState()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            webSocketService.disconnect()
        }
    }

    fun clearLastAnnouncement() {
        _announcements.update { currentAnnouncements ->
            if (currentAnnouncements.isNotEmpty()) {
                currentAnnouncements.drop(1)
            } else {
                emptyList()
            }
        }
    }
}