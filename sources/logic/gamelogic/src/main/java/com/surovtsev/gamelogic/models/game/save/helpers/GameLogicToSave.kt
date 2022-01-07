package com.surovtsev.gamelogic.models.game.save.helpers

import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.models.game.cellpointers.CellIndex
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatus
import com.surovtsev.utils.timers.async.AsyncTimeSpan

class GameLogicToSave(
    private val elapsedTime: Long,
    private val gameStatus: GameStatus,
    private val cubesToOpen: List<CellIndex>,
    private val cubesToRemove: List<CellIndex>
) {

    companion object {
        fun createObject(gameLogic: GameLogic, asyncTimeSpan: AsyncTimeSpan): GameLogicToSave {
            val gameLogicStateHelper = gameLogic.gameStatusHolder

            return GameLogicToSave(
                asyncTimeSpan.getElapsed(),
                gameLogicStateHelper.gameStatus(),
                gameLogic.cubesToOpen,
                gameLogic.cubesToRemove
            )
        }
    }

    fun applySavedData(gameLogic: GameLogic) {
        gameLogic.gameStatusHolder.applySavedData(
            elapsedTime,
            gameStatus
        )
        gameLogic.applySavedData(
            cubesToOpen,
            cubesToRemove
        )
    }
}