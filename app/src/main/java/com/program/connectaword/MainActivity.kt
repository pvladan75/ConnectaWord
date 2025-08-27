package com.program.connectaword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.program.connectaword.ui.WelcomeScreen
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

object Routes {
    const val WELCOME = "welcome"
    const val AUTH_GRAPH = "auth_graph"
    const val MAIN_GRAPH = "main_graph"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val LOBBY = "lobby"
    const val CREATE_ROOM = "create_room"
    const val GAME_SCREEN = "game_screen/{roomId}"
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(navController = navController)
        }

        authGraph(navController)
        mainGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Routes.LOGIN, route = Routes.AUTH_GRAPH) {
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Routes.REGISTER) {
            val authViewModel: AuthViewModel = viewModel()
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(startDestination = Routes.LOBBY, route = Routes.MAIN_GRAPH) {
        // Сваки од ових екрана сада користи исту, исправну методу
        // за добијање дељеног ViewModel-а.
        composable(Routes.LOBBY) { backStackEntry ->
            val lobbyViewModel: LobbyViewModel = backStackEntry.sharedViewModel(navController)
            GameLobbyScreen(navController = navController, lobbyViewModel = lobbyViewModel)
        }
        composable(Routes.CREATE_ROOM) { backStackEntry ->
            val lobbyViewModel: LobbyViewModel = backStackEntry.sharedViewModel(navController)
            CreateRoomScreen(navController = navController, lobbyViewModel = lobbyViewModel)
        }
        composable(
            route = Routes.GAME_SCREEN,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lobbyViewModel: LobbyViewModel = backStackEntry.sharedViewModel(navController)
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            GameScreen(
                navController = navController,
                roomId = roomId,
                lobbyViewModel = lobbyViewModel
            )
        }
    }
}

// Помоћна (helper) функција да избегнемо понављање кода
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}