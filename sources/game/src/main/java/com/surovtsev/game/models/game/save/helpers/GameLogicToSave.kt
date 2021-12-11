package com.surovtsev.game.models.game.save.helpers

import com.surovtsev.game.minesweeper.gamelogic.GameLogic
import com.surovtsev.game.models.game.cellpointers.CellIndex
import com.surovtsev.game.models.game.gamestatus.GameStatus

class GameLogicToSave(
    private val elapsedTime: Long,
    val gameStatus: GameStatus,
    private val cubesToOpen: List<CellIndex>,
    private val cubesToRemove: List<CellIndex>
) {

    companion object {
        fun createObject(gameLogic: GameLogic): GameLogicToSave {
            val gameLogicStateHelper = gameLogic.gameLogicStateHelper

            return GameLogicToSave(
                gameLogicStateHelper.getElapsed(),
                gameLogicStateHelper.gameStatus,
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