package com.program.connectaword.ui.lobby

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.program.connectaword.api.WebSocketService
import com.program.connectaword.data.*
import com.program.connectaword.repository.LobbyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val lobbyRepository: LobbyRepository,
    private val webSocketService: WebSocketService,
    private val sessionManager: com.program.connectaword.data.SessionManager
) : ViewModel() {
    private var webSocketJob: Job? = null

    val lobbyState: StateFlow<LobbyState> = lobbyRepository.getAllRooms()
        .map { rooms -> LobbyState(rooms = rooms) }
        .onStart { refreshRooms() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LobbyState(isLoading = true)
        )

    private val _createRoomState = MutableStateFlow(CreateRoomState())
    val createRoomState: StateFlow<CreateRoomState> = _createRoomState

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState = _gameState.asStateFlow()

    private val _announcements = MutableStateFlow<List<String>>(emptyList())
    val announcements = _announcements.asStateFlow()

    init {
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
                        _announcements.update { currentAnnouncements: List<String> ->
                            listOf(gameMessage.message) + currentAnnouncements
                        }
                    }
                    // Dodajemo SubmitWord u else granu da ne bi bio logovan kao neočekivana poruka
                    is SubmitWord -> { /* Ne radimo ništa, klijent samo šalje ovu poruku */ }
                    else -> {
                        Log.w("LobbyViewModel", "Received unexpected message type: $gameMessage")
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
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
    // --- NOVA FUNKCIJA ---
    fun sendSubmitWordMessage(word: String) = sendMessage(SubmitWord(word))

    private fun sendMessage(message: GameMessage) {
        viewModelScope.launch {
            webSocketService.sendMessage(message)
        }
    }

    fun refreshRooms() {
        viewModelScope.launch {
            lobbyRepository.refreshRooms()
        }
    }

    fun getRooms() {
        refreshRooms()
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