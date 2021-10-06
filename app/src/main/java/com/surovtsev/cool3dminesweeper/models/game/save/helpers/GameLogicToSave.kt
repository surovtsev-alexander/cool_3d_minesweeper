package com.surovtsev.cool3dminesweeper.models.game.save.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool3dminesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatus

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