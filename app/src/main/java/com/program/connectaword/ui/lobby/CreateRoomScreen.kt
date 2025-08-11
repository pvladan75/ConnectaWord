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

    val languages = listOf("English", "Serbian")
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    val wordSources = listOf("SERVER", "PLAYER_SUBMITTED")
    var selectedWordSource by remember { mutableStateOf(wordSources[0]) }

    LaunchedEffect(createRoomState) {
        // ... same as before
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Room") })
        }
    ) { padding ->
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
                onValueChange = { roomName = it },
                label = { Text("Room Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !createRoomState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Language Dropdown
            SimpleDropdownMenu(
                label = "Language",
                options = languages,
                selectedOption = selectedLanguage,
                onOptionSelected = { selectedLanguage = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Word Source Dropdown
            SimpleDropdownMenu(
                label = "Word Source",
                options = wordSources,
                selectedOption = selectedWordSource,
                onOptionSelected = { selectedWordSource = it }
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { lobbyViewModel.createRoom(roomName, selectedLanguage, selectedWordSource) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !createRoomState.isLoading && roomName.isNotBlank()
            ) {
                Text("Create")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}