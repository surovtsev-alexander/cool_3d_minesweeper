package com.surovtsev.gamelogic.models.game.save.helpers

import com.surovtsev.gamelogic.minesweeper.gameState.GameState
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
            gameState: GameState,
            asyncTimeSpan: AsyncTimeSpan
        ): GameLogicToSave {
            return GameLogicToSave(
                asyncTimeSpan.getElapsed(),
                gameState.gameStatusHolder.gameStatus(),
                gameState.cubesToOpen,
                gameState.cubesToRemove
            )
        }
    }

    fun applySavedData(
        gameState: GameState,
    ) {
        gameState.gameStatusHolder.applySavedData(
            elapsedTime,
            gameStatus
        )
        gameState.applySavedData(
            cubesToOpen,
            cubesToRemove
        )
    }
}