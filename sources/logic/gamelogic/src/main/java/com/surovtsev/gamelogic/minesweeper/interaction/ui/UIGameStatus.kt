package com.surovtsev.gamelogic.minesweeper.interaction.ui

sealed interface UIGameStatus {
    object Unimportant: UIGameStatus

    class Win(
        val place: Int,
        val elapsed: Long,
    ): UIGameStatus

    object Lose: UIGameStatus
}
