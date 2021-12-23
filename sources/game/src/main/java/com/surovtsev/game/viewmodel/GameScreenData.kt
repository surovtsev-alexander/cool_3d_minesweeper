package com.surovtsev.game.viewmodel

import com.surovtsev.core.viewmodel.ScreenData

sealed interface GameScreenData: ScreenData {
    object NoData: GameScreenData, ScreenData.NoData, ScreenData.InitializationIsNotFinished

    object GameInProgress: GameScreenData

    open class HasPrevData(
        val prevData: GameScreenData
    ): GameScreenData

    class GameMenu(
        prevData: GameScreenData
    ): HasPrevData(
        prevData
    )
}