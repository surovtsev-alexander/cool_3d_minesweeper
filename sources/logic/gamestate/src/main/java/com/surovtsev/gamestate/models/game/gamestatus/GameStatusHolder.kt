package com.surovtsev.gamestate.models.game.gamestatus

import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.dagger.GameStateScope
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import logcat.logcat
import javax.inject.Inject


@GameStateScope
class GameStatusHolder @Inject constructor(
    private val asyncTimeSpan: AsyncTimeSpan,
    private val gameConfig: GameConfig,
) {
    private val _gameStatusWithElapsedFlow = MutableStateFlow(
        GameStatusWithElapsedForGameConfig(gameConfig)
    )
    val gameStatusWithElapsedFlow: GameStatusWithElapsedFlowForGameConfig = _gameStatusWithElapsedFlow.asStateFlow()

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
        logcat { "setGameStatus; newStatus: $newStatus; currGameStatus: ${gameStatusWithElapsedFlow.value}" }
        _gameStatusWithElapsedFlow.value = GameStatusWithElapsedForGameConfig(
            gameConfig,
            newStatus,
            asyncTimeSpan.getElapsed()
        )
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
