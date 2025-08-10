package com.program.connectaword.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.program.connectaword.ui.lobby.LobbyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    roomId: String,
    lobbyViewModel: LobbyViewModel = viewModel() // We can reuse this for now
) {
    val messages by lobbyViewModel.webSocketMessages.collectAsState()

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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { message ->
                    Text(message)
                }
            }
        }
    }
}