package com.surovtsev.gamestate.models.game.gamestatus

import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.gamestate.dagger.GameScope
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@GameScope
class GameStatusHolder @Inject constructor(
    private val asyncTimeSpan: AsyncTimeSpan,
) {
    private val _gameStatusWithElapsedFlow = MutableStateFlow(
        GameStatusWithElapsed()
    )
    val gameStatusWithElapsedFlow: GameStatusWithElapsedFlow = _gameStatusWithElapsedFlow.asStateFlow()

    private val _bombsLeftFlow = MutableStateFlow(0)
    val bombsLeftFlow: BombsLeftFlow = _bombsLeftFlow.asStateFlow()

    fun gameStatus() = gameStatusWithElapsedFlow.value.gameStatus

    fun isGameNotStarted() = (gameStatus() == GameStatus.NoBombsPlaced)

    fun isGameInProgress() = GameStatusHelper.isGameInProgress(gameStatus())

    fun isGameOver() = GameStatusHelper.isGameOver(gameStatus())

    init {
        asyncTimeSpan.flush()

        setGameStatus(GameStatus.NoBombsPlaced)
    }

    fun setGameStatus(newStatus: GameStatus) {
        _gameStatusWithElapsedFlow.value = GameStatusWithElapsed(
            newStatus,
            asyncTimeSpan.getElapsed()
        )

        if (isGameInProgress()) {
            resumeTimeSpan()
        } else if (isGameOver()) {
            pauseTimeSpan()
        }
    }

    fun resumeTimeSpan() {
        asyncTimeSpan.turnOn()
    }

    fun pauseTimeSpan() {
        asyncTimeSpan.turnOff()
    }

    fun applySavedData(elapsedTime: Long, gameStatus: GameStatus) {
        asyncTimeSpan.setElapsed(elapsedTime)
        setGameStatus(gameStatus)
    }

    fun setBombsLeft(v: Int) {
        _bombsLeftFlow.value = v
    }

    fun decBombsLeft() {
        _bombsLeftFlow.value -= 1
    }

    fun testIfWin() {
        if (_bombsLeftFlow.value == 0) {
            setGameStatus(GameStatus.Win)
        }
    }
}
