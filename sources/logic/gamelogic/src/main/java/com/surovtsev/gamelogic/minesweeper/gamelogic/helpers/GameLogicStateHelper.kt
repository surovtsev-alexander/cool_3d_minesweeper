package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatus
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@GameScope
class GameLogicStateHelper @Inject constructor(
    private val asyncTimeSpan: AsyncTimeSpan
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
        asyncTimeSpan.flush()

        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            GameStatus.NoBombsPlaced,
            asyncTimeSpan.getElapsed()
        )
    }

    fun pauseIfNeeded() {
        if (isGameInProgress()) {
            asyncTimeSpan.turnOff()
        }
    }

    fun resumeIfNeeded() {
        if (isGameInProgress()) {
            asyncTimeSpan.turnOn()
        }
    }

    fun setGameState(newStatus: GameStatus) {
        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            newStatus,
            asyncTimeSpan.getElapsed()
        )

        if (isGameInProgress()) {
            asyncTimeSpan.turnOn()
        } else if (isGameOver()) {
            asyncTimeSpan.turnOff()
        }
    }

    fun applySavedData(elapsedTime: Long, gameStatus: GameStatus) {
        asyncTimeSpan.setElapsed(elapsedTime)
        setGameState(gameStatus)
    }
}
