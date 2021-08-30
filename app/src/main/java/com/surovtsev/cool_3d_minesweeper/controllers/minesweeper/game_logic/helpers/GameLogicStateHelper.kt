package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResume
import com.surovtsev.cool_3d_minesweeper.utils.time.INeedToBeUpdated
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpan
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper

class GameLogicStateHelper(
    private val gameStatusesReceiver: IGameStatusesReceiver,
    private val timeUpdated: () -> Unit,
    timeSpanHelper: TimeSpanHelper
):
    INeedToBeUpdated, IHandlePauseResume
{
    var gameStatus: GameStatus
        private set

    private val timeSpan = TimeSpan(1000L, timeSpanHelper)

    fun isGameNotStarted() = (gameStatus == GameStatus.BOMBS_PLACED)

    fun isGameInProgress() = GameStatusHelper.isGameInProgress(gameStatus)

    fun isGameOver() = GameStatusHelper.isGameOver(gameStatus)

    init {
        gameStatus = GameStatus.NO_BOBMS_PLACED
    }

    override fun tick() {
        if (timeSpan.isOn()) {
            timeSpan.tick()
            if (timeSpan.getAndRelease()) {
                timeUpdated()
            }
        }
    }

    override fun onPause() {
        if (isGameInProgress()) {
            timeSpan.turnOff()
        }
    }

    override fun onResume() {
        if (isGameInProgress()) {
            timeSpan.turnOn()
        }
    }

    fun setGameState(newState: GameStatus) {
        gameStatus = newState
        gameStatusesReceiver.gameStatusUpdated(gameStatus)

        if (isGameInProgress()) {
            timeSpan.turnOn()
        } else if (isGameOver()) {
            timeSpan.turnOff()
        }
    }

    fun getElapsed() = timeSpan.getElapsed()
}
