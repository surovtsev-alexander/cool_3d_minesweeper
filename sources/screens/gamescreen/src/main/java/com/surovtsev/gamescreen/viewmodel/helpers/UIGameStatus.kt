package com.surovtsev.gamescreen.viewmodel.helpers

sealed interface UIGameStatus {
    object Unimportantly: UIGameStatus

    class Win(
        val place: Int
    ): UIGameStatus

    object Lose: UIGameStatus
}