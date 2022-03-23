package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.screendata.ScreenData
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows

sealed class GameScreenData: ScreenData.UserData {
    open fun rootScreenData(): GameScreenData = this

    class GameInProgress(
        val uiGameControls: UIGameControlsFlows
    ): GameScreenData()

    open class HasPrevData(
        val prevData: GameScreenData
    ): GameScreenData() {
        override fun rootScreenData(): GameScreenData {
            return prevData.rootScreenData()
        }
    }

    class GameMenu(
        prevData: GameScreenData
    ): HasPrevData(
        prevData
    )
}
