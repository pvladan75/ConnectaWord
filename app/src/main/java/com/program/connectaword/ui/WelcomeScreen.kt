package com.program.connectaword.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.program.connectaword.Routes

@Composable
fun WelcomeScreen(
    navController: NavController,
    welcomeViewModel: WelcomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Покушавамо да учитамо последњу IP или поставимо подразумевану
    var customIp by remember { mutableStateOf(welcomeViewModel.getLastUsedIp() ?: "192.168.0.23") }
    val radioOptions = listOf("Emulator", "Physical Device")
    // Покушавамо да запамтимо последњи избор уређаја
    var selectedOption by remember {
        mutableStateOf(
            when (welcomeViewModel.getLastUsedIp()) {
                "10.0.2.2" -> "Emulator"
                null -> null
                else -> "Physical Device"
            }
        )
    }

    // LaunchedEffect се извршава само једном када се екран покрене
    LaunchedEffect(Unit) {
        val activeUser = welcomeViewModel.getActiveUser()
        // Ако постоји сачуван корисник И сачувана IP адреса, улогуј га аутоматски
        if (activeUser != null && welcomeViewModel.getLastUsedIp() != null) {
            Toast.makeText(context, "Welcome back, ${activeUser.korisnickoIme}!", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.MAIN_GRAPH) {
                popUpTo(Routes.WELCOME) { inclusive = true }
            }
        }
    }

    val onContinueClicked = {
        val ipAddress = if (selectedOption == "Emulator") "10.0.2.2" else customIp

        // Чувамо изабрану IP адресу за следећи пут
        welcomeViewModel.saveIpAddress(ipAddress)

        // Навигација ка екрану за пријаву
        navController.navigate(Routes.AUTH_GRAPH) {
            popUpTo(Routes.WELCOME) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Connect-a-Word!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Game instructions will be displayed here in the final version.", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(48.dp))
        Text("--- DEV OPTIONS (temporary) ---", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { selectedOption = text }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { selectedOption = text }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text)
            }
        }

        if (selectedOption == "Physical Device") {
            OutlinedTextField(
                value = customIp,
                onValueChange = { customIp = it },
                label = { Text("Your Computer's Local IP") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinueClicked,
            enabled = selectedOption != null && (selectedOption == "Emulator" || customIp.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}