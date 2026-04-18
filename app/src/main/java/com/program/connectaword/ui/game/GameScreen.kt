package com.program.connectaword.ui.game

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.program.connectaword.Routes
import com.program.connectaword.data.PlayerData
import com.program.connectaword.data.PlayerProgress
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
    val context = LocalContext.current

    LaunchedEffect(announcements) {
        announcements.firstOrNull()?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            lobbyViewModel.clearLastAnnouncement()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Game Room: $roomId") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (gameState?.gameStatus) {
                "WAITING" -> {
                    WaitingView(
                        players = gameState?.players ?: emptyList(),
                        isHost = UserManager.currentUser?.id == gameState?.hostId
                    ) {
                        lobbyViewModel.sendStartGameMessage()
                    }
                }
                // --- NOVI EKRAN ZA UNOS REČI ---
                "SUBMITTING_WORDS" -> {
                    WordSubmissionView(
                        state = gameState!!,
                        onSubmitWord = { word -> lobbyViewModel.sendSubmitWordMessage(word) }
                    )
                }
                "IN_PROGRESS" -> {
                    InProgressView(
                        state = gameState!!,
                        lobbyViewModel = lobbyViewModel
                    )
                }
                "FINISHED" -> {
                    FinishedView(
                        state = gameState!!,
                        navController = navController,
                        lobbyViewModel = lobbyViewModel
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        Text("Connecting to the room...", modifier = Modifier.padding(top = 64.dp))
                    }
                }
            }
        }
    }
}

// --- CEO NOVI COMOSABLE ZA UNOS REČI ---
@Composable
fun WordSubmissionView(
    state: com.program.connectaword.data.GameState,
    onSubmitWord: (String) -> Unit
) {
    val currentUserId = UserManager.currentUser?.id
    var word by remember { mutableStateOf("") }
    val mySubmissionStatus = state.submissionInfo?.get(currentUserId) == true

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Submit a Word", style = MaterialTheme.typography.headlineMedium)
        Text("Each player submits one word for others to guess.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Prikaz statusa svih igrača
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                state.players.forEach { player ->
                    val hasSubmitted = state.submissionInfo?.get(player.id) == true
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(player.username, fontWeight = if (player.id == currentUserId) FontWeight.Bold else FontWeight.Normal)
                        Icon(
                            imageVector = if (hasSubmitted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                            contentDescription = if (hasSubmitted) "Submitted" else "Waiting",
                            tint = if (hasSubmitted) Color.Green else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Unos reči
        if (mySubmissionStatus) {
            Text("Word submitted! Waiting for other players...", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        } else {
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("Enter a 5 or 6 letter word") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { if (word.isNotBlank()) onSubmitWord(word) },
                modifier = Modifier.fillMaxWidth(),
                enabled = word.isNotBlank()
            ) {
                Text("Submit Word")
            }
        }
    }
}


@Composable
fun WaitingView(players: List<PlayerData>, isHost: Boolean, onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Players in Room:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        players.forEach { player ->
            Text("${player.username} (${player.rating})", style = MaterialTheme.typography.bodyLarge)
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
fun InProgressView(state: com.program.connectaword.data.GameState, lobbyViewModel: LobbyViewModel) {
    val currentUserId = UserManager.currentUser?.id
    val myPlayerData = state.players.find { it.id == currentUserId }
    val myProgress = myPlayerData?.progress

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderView(
            currentRound = (myPlayerData?.currentWordIndex ?: 0) + 1,
            // --- IZMENA: Koristimo dinamički broj rundi ---
            totalRounds = state.totalRounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlayersPanel(players = state.players)
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        if (myProgress != null) {
            CurrentPlayerProgressView(
                progress = myProgress,
                onGuess = { guess -> lobbyViewModel.sendGuess(guess) },
                onSurrender = { lobbyViewModel.sendSurrenderMessage() }
            )
        } else {
            val isGameFinishedForMe = myPlayerData?.isGameFinished == true
            if (isGameFinishedForMe) {
                Text(
                    "You have finished the game! Waiting for other players...",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Text("Waiting for the next round...")
            }
        }
    }
}

@Composable
fun HeaderView(currentRound: Int, totalRounds: Int) {
    Text(
        text = "Round $currentRound / $totalRounds",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun PlayersPanel(players: List<PlayerData>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Players:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            players.forEach { player ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${player.username} (${player.rating})")
                    Text("Score: ${player.totalScore}")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrentPlayerProgressView(progress: PlayerProgress, onGuess: (String) -> Unit, onSurrender: () -> Unit) {
    var guessText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            // --- IZMENA: Prikazujemo pattern direktno ---
            text = progress.pattern,
            fontSize = 32.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Remaining attempts: ${progress.remainingAttempts}")
        Spacer(modifier = Modifier.height(16.dp))

        if (progress.commonLetters.isNotEmpty()) {
            Text("Found letters:", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                progress.commonLetters.sorted().forEach { char ->
                    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
                        Text(
                            text = char.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!progress.isWordFinished) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = guessText,
                    onValueChange = { guessText = it },
                    label = { Text("Enter your guess") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (guessText.isNotBlank()) {
                        onGuess(guessText)
                        guessText = ""
                    }
                }) {
                    Text("Guess")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onSurrender) {
                Text("Surrender round")
            }

        } else {
            Text("You have finished this round! Waiting for the next one...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }

        if(progress.previousGuesses.isNotEmpty()) {
            GuessHistoryView(guesses = progress.previousGuesses)
        }
    }
}

@Composable
fun GuessHistoryView(guesses: List<String>) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Your guesses:", style = MaterialTheme.typography.titleSmall)
        LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)) {
            items(guesses) { guess ->
                Text(guess, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun FinishedView(
    state: com.program.connectaword.data.GameState,
    navController: NavController,
    lobbyViewModel: LobbyViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        state.finalWords?.let { words ->
            Text(
                text = "Words were: ${words.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("Final Scores:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        state.players.sortedByDescending { it.totalScore }.forEach { player ->
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${player.username} (${player.rating})", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("${player.totalScore} points", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val isHost = UserManager.currentUser?.id == state.hostId
        if (isHost) {
            Button(onClick = { lobbyViewModel.sendPlayAgainMessage() }) {
                Text("Play Again")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navController.navigate(Routes.LOBBY) {
                popUpTo(Routes.MAIN_GRAPH) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }) {
            Text("Back to Lobby")
        }
    }
}