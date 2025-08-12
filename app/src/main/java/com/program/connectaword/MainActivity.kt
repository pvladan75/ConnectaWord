package com.program.connectaword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.program.connectaword.data.UserManager
import com.program.connectaword.ui.auth.AuthViewModel
import com.program.connectaword.ui.auth.LoginScreen
import com.program.connectaword.ui.auth.RegisterScreen
import com.program.connectaword.ui.game.GameScreen
import com.program.connectaword.ui.lobby.CreateRoomScreen
import com.program.connectaword.ui.lobby.GameLobbyScreen
import com.program.connectaword.ui.lobby.LobbyViewModel
import com.program.connectaword.ui.theme.ConnectaWordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectaWordTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val lobbyViewModel: LobbyViewModel = viewModel()

    // Проверавамо да ли постоји активна сесија
    val sessionManager = App.instance.sessionManager
    val startDestination = if (sessionManager.getActiveToken() != null) {
        // Ако постоји, учитавамо корисника и идемо у лоби
        UserManager.currentUser = sessionManager.getActiveUser()
        "lobby"
    } else {
        // Ако не постоји, идемо на пријаву
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("lobby") {
            GameLobbyScreen(navController = navController, lobbyViewModel = lobbyViewModel)
        }
        composable("create_room") {
            CreateRoomScreen(navController = navController, lobbyViewModel = lobbyViewModel)
        }
        composable(
            route = "game_screen/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            GameScreen(
                navController = navController,
                roomId = roomId,
                lobbyViewModel = lobbyViewModel
            )
        }
    }
}