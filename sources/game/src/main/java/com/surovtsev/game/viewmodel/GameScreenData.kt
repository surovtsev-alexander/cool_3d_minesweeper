package com.surovtsev.game.viewmodel

import com.surovtsev.core.viewmodel.ScreenData

sealed class GameScreenData: ScreenData {
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