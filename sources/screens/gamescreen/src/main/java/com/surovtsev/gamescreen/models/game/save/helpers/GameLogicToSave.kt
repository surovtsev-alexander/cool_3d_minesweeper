package com.surovtsev.gamescreen.models.game.save.helpers

import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.models.game.cellpointers.CellIndex
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.utils.timers.TimeSpan

class GameLogicToSave(
    private val elapsedTime: Long,
    private val gameStatus: GameStatus,
    private val cubesToOpen: List<CellIndex>,
    private val cubesToRemove: List<CellIndex>
) {

    companion object {
        fun createObject(gameLogic: GameLogic, timeSpan: TimeSpan): GameLogicToSave {
            val gameLogicStateHelper = gameLogic.gameLogicStateHelper

            return GameLogicToSave(
                timeSpan.getElapsed(),
                gameLogicStateHelper.gameStatus(),
                gameLogic.cubesToOpen,
                gameLogic.cubesToRemove
            )
        }
    }

    fun applySavedData(gameLogic: GameLogic) {
        gameLogic.gameLogicStateHelper.applySavedData(
            elapsedTime,
            gameStatus
        )
        gameLogic.applySavedData(
            cubesToOpen,
            cubesToRemove
        )
    }
}