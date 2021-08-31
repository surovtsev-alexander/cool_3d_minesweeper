package com.surovtsev.cool_3d_minesweeper.models.game.save.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus

class GameLogicToSave(
    val elapsedTime: Long,
    val gameStatus: GameStatus,
    val cubesToOpen: List<CellIndex>,
    val cubesToRemove: List<CellIndex>
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