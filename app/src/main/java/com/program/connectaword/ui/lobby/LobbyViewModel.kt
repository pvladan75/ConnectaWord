package com.program.connectaword.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.api.RetrofitInstance
import com.program.connectaword.api.WebSocketService
import com.program.connectaword.data.*
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
    val createdRoom: RoomResponse? = null,
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
                when (gameMessage) {
                    is GameStateUpdate -> {
                        _gameState.value = gameMessage.gameState
                    }
                    is Announcement -> {
                        val currentAnnouncements = _announcements.value.toMutableList()
                        currentAnnouncements.add(0, gameMessage.message)
                        _announcements.value = currentAnnouncements
                    }
                    else -> {
                        // Игноришемо поруке које клијент не треба да прими
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

    fun sendStartGameMessage() {
        viewModelScope.launch {
            // 👇 ИСПРАВКА ЈЕ ОВДЕ (додате су заграде) 👇
            webSocketService.sendMessage(StartGame())
        }
    }

    fun sendGuess(guess: String) {
        viewModelScope.launch {
            webSocketService.sendMessage(MakeGuess(guess))
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
                    _createRoomState.value = CreateRoomState(error = "Failed to create room")
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
}