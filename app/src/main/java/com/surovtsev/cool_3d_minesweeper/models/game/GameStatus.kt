package com.surovtsev.cool_3d_minesweeper.models.game

enum class GameStatus {
    NO_BOBMS_PLACED,
    BOMBS_PLACED,
    WIN,
    LOSE
}

object GameStatusHelper {
    fun isGameOver(gameStatus: GameStatus) = (gameStatus == GameStatus.WIN || gameStatus == GameStatus.LOSE)

    fun isGameStarted(gameStatus: GameStatus) = (gameStatus == GameStatus.BOMBS_PLACED)
}
