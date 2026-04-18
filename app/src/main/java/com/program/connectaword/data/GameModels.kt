package com.program.connectaword.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface GameMessage

@Serializable
data class StartGame(val action: String = "start") : GameMessage

@Serializable
data class MakeGuess(val guess: String, val action: String = "guess") : GameMessage

@Serializable
data class SurrenderRound(val action: String = "surrender") : GameMessage

@Serializable
data class PlayAgain(val action: String = "play_again") : GameMessage

@Serializable
data class SubmitWord(val word: String, val action: String = "submit_word") : GameMessage

@Serializable
data class GameStateUpdate(val gameState: GameState) : GameMessage

@Serializable
data class Announcement(val message: String) : GameMessage

@Serializable
data class BaseAction(val action: String)

@Serializable
data class PlayerProgress(
    val pattern: String,
    val previousGuesses: List<String>,
    val commonLetters: Set<Char>,
    val remainingAttempts: Int,
    val isWordFinished: Boolean
)

@Serializable
data class PlayerData(
    val id: String,
    val username: String,
    val rating: Int,
    val totalScore: Int,
    val currentWordIndex: Int,
    val progress: PlayerProgress?,
    val isGameFinished: Boolean
)

@Serializable
data class GameState(
    val gameStatus: String,
    val players: List<PlayerData>,
    val hostId: String,
    val finalWords: List<String>? = null,
    // --- NOVO POLJE: Informacije o tome ko je poslao reč ---
    val submissionInfo: Map<String, Boolean>? = null,
    // --- NOVO POLJE: Ukupan broj rundi ---
    val totalRounds: Int = 5
)