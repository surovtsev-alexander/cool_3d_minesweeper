package com.surovtsev.game.minesweeper.gamelogic.helpers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.surovtsev.game.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.utils.timers.Tickable
import com.surovtsev.game.utils.time.TimeSpan
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import com.surovtsev.game.viewmodel.helpers.GameScreenEventsReceiver
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
    private val timeSpan: TimeSpan
):
    Tickable,
    DefaultLifecycleObserver
{
    init {
        timeSpan.subscribe(this)
    }

    var gameStatus: GameStatus
        private set

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

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        pauseIfNeeded()
    }

    fun pauseIfNeeded() {
        if (isGameInProgress()) {
            timeSpan.turnOff()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        resumeIfNeeded()
    }

    fun resumeIfNeeded() {
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
