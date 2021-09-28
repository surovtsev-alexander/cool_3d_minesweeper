package com.surovtsev.cool_3d_minesweeper.models.game.game_status

enum class GameStatus {
    NoBombsPlaced,
    BombsPlaced,
    Win,
    Lose
}

object GameStatusHelper {
    val initStatus = GameStatus.NoBombsPlaced

    fun isGameNotStarted(gameStatus: GameStatus) = (gameStatus == GameStatus.NoBombsPlaced)

    fun isGameInProgress(gameStatus: GameStatus) = (gameStatus == GameStatus.BombsPlaced)

    fun isGameOver(gameStatus: GameStatus) = (gameStatus == GameStatus.Win || gameStatus == GameStatus.Lose)
}
