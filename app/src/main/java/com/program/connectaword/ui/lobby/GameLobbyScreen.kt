package com.program.connectaword.ui.lobby

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.program.connectaword.App
import com.program.connectaword.data.RoomResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLobbyScreen(
    navController: NavController,
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    val lobbyState by lobbyViewModel.lobbyState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Lobby") },
                actions = {
                    IconButton(onClick = {
                        App.instance.sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_room") }) {
                Icon(Icons.Default.Add, contentDescription = "Create Room")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (lobbyState.isLoading) {
                CircularProgressIndicator()
            } else if (lobbyState.error != null) {
                Text(text = lobbyState.error!!)
            } else {
                RoomList(
                    rooms = lobbyState.rooms,
                    onRoomClick = { roomId ->
                        lobbyViewModel.joinRoom(roomId)
                        navController.navigate("game_screen/$roomId")
                    }
                )
            }
        }
    }
}

@Composable
fun RoomList(rooms: List<RoomResponse>, onRoomClick: (String) -> Unit) {
    if (rooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No available rooms. Create one!")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rooms) { room ->
                RoomItem(room = room, onClick = { onRoomClick(room.id) })
            }
        }
    }
}

@Composable
fun RoomItem(room: RoomResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = room.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Hosted by: ${room.hostId}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}