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


package com.surovtsev.gamestateholder

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestate.logic.dagger.DaggerGameStateComponent
import com.surovtsev.gamestateholder.dagger.GameStateHolderScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

typealias GameStateFlow = StateFlow<GameState?>

@GameStateHolderScope
class GameStateHolder @Inject constructor(
    private val appComponentEntryPoint: AppComponentEntryPoint,
    private val timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint,
) {
    companion object {
        fun createGameState(
            appComponentEntryPoint: AppComponentEntryPoint,
            timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint,
            loadGame: Boolean,
        ): GameState {
            val gameStateComponent = DaggerGameStateComponent
                .builder()
                .appComponentEntryPoint(appComponentEntryPoint)
                .timeSpanComponentEntryPoint(timeSpanComponentEntryPoint)
                .loadGame(loadGame)
                .build()
            return gameStateComponent.gameState
        }
    }

    private val _gameStateFlow = MutableStateFlow<GameState?>(null)
    val gameStateFlow: GameStateFlow = _gameStateFlow.asStateFlow()

    private fun createGameState(
        tryToLoad: Boolean = false
    ): GameState {
        return Companion.createGameState(
            appComponentEntryPoint,
            timeSpanComponentEntryPoint,
            tryToLoad
        )
    }

    fun newGame(
        tryToLoad: Boolean
    ) {
        _gameStateFlow.value = createGameState(tryToLoad)
    }

    fun setGameStateToNull() {
        _gameStateFlow.value = null
    }
}
