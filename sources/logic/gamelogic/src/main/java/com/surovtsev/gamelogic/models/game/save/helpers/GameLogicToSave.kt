package com.surovtsev.gamelogic.models.game.save.helpers

import com.surovtsev.gamelogic.minesweeper.gameState.GameState
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusHolder
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
        fun createObject(
            gameStatusHolder: GameStatusHolder,
            gameState: GameState,
            asyncTimeSpan: AsyncTimeSpan
        ): GameLogicToSave {
            return GameLogicToSave(
                asyncTimeSpan.getElapsed(),
                gameStatusHolder.gameStatus(),
                gameState.cubesToOpen,
                gameState.cubesToRemove
            )
        }
    }

    fun applySavedData(
        gameState: GameState,
        gameStatusHolder: GameStatusHolder,
    ) {
        gameStatusHolder.applySavedData(
            elapsedTime,
            gameStatus
        )
        gameState.applySavedData(
            cubesToOpen,
            cubesToRemove
        )
    }
}