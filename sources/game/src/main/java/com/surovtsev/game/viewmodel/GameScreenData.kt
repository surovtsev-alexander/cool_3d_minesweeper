package com.surovtsev.game.viewmodel

sealed class GameScreenData {
    object NoData: GameScreenData()

    object GameInProgress: GameScreenData()

    open class HasPrevData(
        val prevData: GameScreenData
    ): GameScreenData()

    class MainMenu(
        prevData: GameScreenData
    ): HasPrevData(
        prevData
    )

    class Paused(
        prevData: GameScreenData
    ): HasPrevData(
        prevData
    )
}