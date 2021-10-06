package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.model_views.game_screen_view_model.helpers.GameScreenEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResume
import com.surovtsev.cool_3d_minesweeper.utils.time.INeedToBeUpdated
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpan
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import javax.inject.Inject

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

@GameScope
class GameLogicStateHelper @Inject constructor(
    private val gameScreenEventsReceiver: GameScreenEventsReceiver,
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    timeSpanHelper: TimeSpanHelper
):
    INeedToBeUpdated, IHandlePauseResume
{
    var gameStatus: GameStatus
        private set

    private val timeSpan = TimeSpan(1000L, timeSpanHelper)

    fun isGameNotStarted() = (gameStatus == GameStatus.NoBombsPlaced)

    fun isGameInProgress() = GameStatusHelper.isGameInProgress(gameStatus)

    fun isGameOver() = GameStatusHelper.isGameOver(gameStatus)

    init {
        gameStatus = GameStatus.NoBombsPlaced
    }

    fun notifyTimeUpdated() {
        gameScreenEventsReceiver.timeUpdated(getElapsed())
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
        val elapsed = getElapsed()
        minesweeperGameStatusReceiver.gameStatusUpdated(
            gameStatus,
            elapsed)
        gameScreenEventsReceiver.gameStatusUpdated(
            gameStatus,
            elapsed
        )

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
