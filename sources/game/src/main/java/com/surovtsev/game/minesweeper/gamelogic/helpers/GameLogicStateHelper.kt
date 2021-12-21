package com.surovtsev.game.minesweeper.gamelogic.helpers

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.timers.TimeSpan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@GameScope
class GameLogicStateHelper @Inject constructor(
    private val timeSpan: TimeSpan
):
    DefaultLifecycleObserver
{
    private val _gameStatusWithElapsedFlow = MutableStateFlow(
        GameStatusWithElapsed()
    )
    val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow = _gameStatusWithElapsedFlow.asStateFlow()

    fun gameStatus() = gameStatusWithElapsedFlow.value.gameStatus

    fun isGameNotStarted() = (gameStatus() == GameStatus.NoBombsPlaced)

    fun isGameInProgress() = GameStatusHelper.isGameInProgress(gameStatus())

    fun isGameOver() = GameStatusHelper.isGameOver(gameStatus())

    init {
        timeSpan.flush()

        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            GameStatus.NoBombsPlaced,
            timeSpan.getElapsed()
        )
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

    fun setGameState(newStatus: GameStatus) {
        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            newStatus,
            timeSpan.getElapsed()
        )

        if (isGameInProgress()) {
            timeSpan.turnOn()
        } else if (isGameOver()) {
            timeSpan.turnOff()
        }
    }

    fun applySavedData(elapsedTime: Long, gameStatus: GameStatus) {
        timeSpan.setElapsed(elapsedTime)
        setGameState(gameStatus)
    }
}
