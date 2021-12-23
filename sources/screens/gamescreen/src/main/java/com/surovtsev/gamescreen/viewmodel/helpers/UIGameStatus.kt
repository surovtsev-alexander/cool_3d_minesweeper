package com.surovtsev.gamescreen.viewmodel.helpers

sealed interface UIGameStatus {
    object Unimportant: UIGameStatus

    class Win(
        val place: Int
    ): UIGameStatus

    object Lose: UIGameStatus
}
