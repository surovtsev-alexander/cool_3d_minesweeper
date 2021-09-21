package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResume
import com.surovtsev.cool_3d_minesweeper.utils.time.INeedToBeUpdated
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpan
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//class GameLogicStateHelper @AssistedInject constructor(
//    @Assisted private val gameEventsReceiver: GameEventsReceiver,
//    @Assisted private val gameStatusReceiver: IGameStatusReceiver,
//    @Assisted timeSpanHelper: TimeSpanHelper
//):
//    INeedToBeUpdated, IHandlePauseResume
//{
//    @AssistedFactory
//    interface Factory {
//        fun create(
//            gameEventsReceiver: IGameEventsReceiver,
//            gameStatusReceiver: IGameStatusReceiver,
//            timeSpanHelper: TimeSpanHelper
//        ): GameLogicStateHelper
//    }

class GameLogicStateHelper constructor(
    private val gameEventsReceiver: GameEventsReceiver,
    private val gameStatusReceiver: IGameStatusReceiver,
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

    fun notifyTimeUpdated() {
        gameEventsReceiver.timeUpdated(getElapsed())
    }

    override fun tick() {
        if (timeSpan.isOn()) {
            timeSpan.tick()
            if (timeSpan.getAndRelease()) {
                notifyTimeUpdated()
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
        gameStatusReceiver.gameStatusUpdated(gameStatus)
        gameEventsReceiver.gameStatusUpdated(gameStatus)

        if (isGameInProgress()) {
            timeSpan.turnOn()
        } else if (isGameOver()) {
            timeSpan.turnOff()
        }
    }

    fun getElapsed() = timeSpan.getElapsed()

    fun applySavedData(elapsedTime: Long, gameStatus: GameStatus) {
        timeSpan.setElapsed(elapsedTime)
        setGameState(gameStatus)
    }
}
