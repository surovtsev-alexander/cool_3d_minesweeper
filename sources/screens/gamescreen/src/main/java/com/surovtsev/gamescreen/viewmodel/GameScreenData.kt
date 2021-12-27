package com.surovtsev.gamescreen.viewmodel

import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows

sealed interface GameScreenData: ScreenData {
    object NoData: GameScreenData, ScreenData.NoData, ScreenData.InitializationIsNotFinished

    class GameInProgress(
        val uiGameControls: UIGameControlsFlows
    ): GameScreenData

    open class HasPrevData(
        val prevData: GameScreenData
    ): GameScreenData

    class GameMenu(
        prevData: GameScreenData
    ): HasPrevData(
        prevData
    )
}