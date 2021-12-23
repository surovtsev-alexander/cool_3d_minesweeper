package com.surovtsev.gamescreen.minesweeper.gamelogic.helpers

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.timers.TimeSpan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@GameScope
class GameLogicStateHelper @Inject constructor(
    private val timeSpan: TimeSpan
) {
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

    fun pauseIfNeeded() {
        if (isGameInProgress()) {
            timeSpan.turnOff()
        }
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
