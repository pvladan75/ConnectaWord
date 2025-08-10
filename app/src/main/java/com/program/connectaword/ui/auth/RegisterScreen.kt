package com.program.connectaword.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var korisnickoIme by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lozinka by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState.isRegistrationSuccessful) {
            Toast.makeText(context, "Registracija uspešna! Molimo prijavite se.", Toast.LENGTH_LONG).show()
            // Vrati korisnika na login ekran nakon uspešne registracije
            navController.navigate("login") {
                // Očisti back stack da ne može da se vrati na registraciju
                popUpTo("login") { inclusive = true }
            }
        }
        if (authState.error != null) {
            Toast.makeText(context, "Greška: ${authState.error}", Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Kreiraj nalog", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = korisnickoIme,
                onValueChange = { korisnickoIme = it },
                label = { Text("Korisničko ime") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lozinka,
                onValueChange = { lozinka = it },
                label = { Text("Lozinka") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    authViewModel.register(korisnickoIme, email, lozinka)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.isLoading // Onemogući dugme dok se učitava
            ) {
                Text("Registruj se")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Već imaš nalog? Prijavi se",
                modifier = Modifier.clickable { navController.navigate("login") },
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (authState.isLoading) {
            CircularProgressIndicator()
        }
    }
}