package com.program.connectaword.ui.lobby

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    navController: NavController,
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    var roomName by remember { mutableStateOf("") }
    val createRoomState by lobbyViewModel.createRoomState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(createRoomState) {
        if (createRoomState.success) {
            Toast.makeText(context, "Room created!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        if (createRoomState.error != null) {
            Toast.makeText(context, createRoomState.error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Room") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it }, // Corrected: onValueChange
                    label = { Text("Room Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !createRoomState.isLoading
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { lobbyViewModel.createRoom(roomName) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !createRoomState.isLoading && roomName.isNotBlank()
                ) {
                    Text("Create")
                }
            }
            if (createRoomState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}