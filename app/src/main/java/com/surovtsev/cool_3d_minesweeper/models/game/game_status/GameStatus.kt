package com.surovtsev.cool_3d_minesweeper.models.game.game_status

enum class GameStatus {
    NO_BOBMS_PLACED,
    BOMBS_PLACED,
    WIN,
    LOSE
}

object GameStatusHelper {
    val initStatus = GameStatus.NO_BOBMS_PLACED

    fun isGameNotStarted(gameStatus: GameStatus) = (gameStatus == GameStatus.NO_BOBMS_PLACED)

    fun isGameInProgress(gameStatus: GameStatus) = (gameStatus == GameStatus.BOMBS_PLACED)

    fun isGameOver(gameStatus: GameStatus) = (gameStatus == GameStatus.WIN || gameStatus == GameStatus.LOSE)
}
