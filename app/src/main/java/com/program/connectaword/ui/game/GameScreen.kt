package com.program.connectaword.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.program.connectaword.data.GameState
import com.program.connectaword.data.UserManager
import com.program.connectaword.ui.lobby.LobbyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    roomId: String,
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    val gameState by lobbyViewModel.gameState.collectAsState()
    val announcements by lobbyViewModel.announcements.collectAsState()
    var guessText by remember { mutableStateOf("") }
    val currentUserId = UserManager.currentUser?.id

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Game Room: $roomId") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Приказујемо стање игре само ако постоји
            gameState?.let { state ->
                // Ако игра чека на почетак
                if (state.status == "WAITING") {
                    WaitingView(
                        state = state,
                        isHost = currentUserId == state.hostId
                    ) {
                        // onClick за Start дугме
                        lobbyViewModel.sendStartGameMessage()
                    }
                }
                // Ако је игра у току или завршена
                else {
                    GameContentView(
                        state = state,
                        guessText = guessText,
                        onGuessTextChanged = { guessText = it },
                        onSendGuess = {
                            if (guessText.isNotBlank()) {
                                lobbyViewModel.sendGuess(guessText)
                                guessText = "" // Очисти поље
                            }
                        }
                    )
                }
            } ?: run {
                // Приказује се док се не добије прво стање игре
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Connecting to the room...", modifier = Modifier.padding(top = 64.dp))
                }
            }

            // Листа са обавештењима увек на дну
            AnnouncementsList(announcements)
        }
    }
}

@Composable
fun WaitingView(state: GameState, isHost: Boolean, onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Players in Room:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        state.players.forEach { player ->
            Text(player.username, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(32.dp))

        if (isHost) {
            Text("You are the host. Start the game when ready!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStartClick) {
                Text("Start Game")
            }
        } else {
            Text("Waiting for the host to start...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun GameContentView(
    state: GameState,
    guessText: String,
    onGuessTextChanged: (String) -> Unit,
    onSendGuess: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatus(state)
        Spacer(modifier = Modifier.height(16.dp))

        if (!state.isGameOver) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = guessText,
                    onValueChange = onGuessTextChanged,
                    label = { Text("Enter your guess") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSendGuess) {
                    Text("Guess")
                }
            }
        }
    }
}

@Composable
fun AnnouncementsList(announcements: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Фиксна висина за доњи део екрана
            .padding(top = 16.dp),
        reverseLayout = true
    ) {
        items(announcements) { announcement ->
            Text(
                text = announcement,
                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GameStatus(state: GameState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.pattern,
            fontSize = 32.sp,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Remaining Guesses: ${state.remainingGuesses}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Players: ${state.players.joinToString(", ") { it.username }}")

        if (state.isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Game Over! The word was: ${state.wordToGuess}",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
    }
}