package com.program.connectaword.data

// Sealed интерфејс који представља све могуће поруке у игри
sealed interface GameMessage

// Порука коју домаћин шаље да започне игру
data class StartGame(val action: String = "start") : GameMessage

// Порука коју клијент шаље серверу када погађа реч
data class MakeGuess(val guess: String) : GameMessage

// Порука коју сервер шаље свим играчима са тренутним стањем игре
data class GameStateUpdate(val gameState: GameState) : GameMessage

// Порука коју сервер шаље као обавештење
data class Announcement(val message: String) : GameMessage

// Представља стање игре
data class GameState(
    val wordToGuess: String,
    val pattern: String,
    val remainingGuesses: Int,
    val players: List<PlayerData>,
    val isGameOver: Boolean = false,
    val hostId: String,
    val status: String
)

// Подаци о играчу
data class PlayerData(
    val id: String,
    val username: String,
    val score: Int
)