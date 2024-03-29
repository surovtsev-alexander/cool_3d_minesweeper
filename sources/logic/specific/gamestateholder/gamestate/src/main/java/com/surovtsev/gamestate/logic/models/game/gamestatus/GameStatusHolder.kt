/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamestate.logic.models.game.gamestatus

import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.dagger.GameStateScope
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
    val gameStatusWithElapsedFlow: GameStatusWithElapsedForGameConfigFlow = _gameStatusWithElapsedFlow.asStateFlow()

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
