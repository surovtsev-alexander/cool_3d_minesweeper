package com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.GameScreenEventsReceiver
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatusHelper
import com.surovtsev.cool3dminesweeper.utils.interfaces.PauseResumeHandler
import com.surovtsev.cool3dminesweeper.utils.time.timers.Tickable
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpan
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpanHelper
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
    Tickable, PauseResumeHandler
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
