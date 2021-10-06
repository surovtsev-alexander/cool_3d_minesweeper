package com.surovtsev.cool3dminesweeper.models.game.gamestatus

enum class GameStatus {
    NoBombsPlaced,
    BombsPlaced,
    Win,
    Lose
}

object GameStatusHelper {
    val initStatus = GameStatus.NoBombsPlaced

    fun isGameInProgress(gameStatus: GameStatus) = (gameStatus == GameStatus.BombsPlaced)

    fun isGameOver(gameStatus: GameStatus) = (gameStatus == GameStatus.Win || gameStatus == GameStatus.Lose)
}
