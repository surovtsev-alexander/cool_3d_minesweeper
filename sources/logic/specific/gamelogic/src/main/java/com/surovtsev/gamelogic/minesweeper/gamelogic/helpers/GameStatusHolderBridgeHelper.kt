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


package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatus
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusWithElapsedForGameConfig
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameStatusHolderBridgeHelper(
    private val gameStatusHolder: GameStatusHolder,
    private val gameNotPausedFlow: GameNotPausedFlow,
    private val bombsLeftFlow: MutableStateFlow<Int>,
    private val gameStatusWithElapsedFlow: MutableStateFlow<GameStatusWithElapsedForGameConfig?>,
    restartableCoroutineScope: RestartableCoroutineScope,
) {
    private var jobs: MutableList<Job> = emptyList<Job>().toMutableList()

    init {
        jobs += restartableCoroutineScope.launch {
             gameNotPausedFlow.combine(
                gameStatusHolder.gameStatusWithElapsedFlow
            ) { gameNotPaused: Boolean, (_, gameStatus: GameStatus, _) ->

                gameNotPaused and GameStatusHelper.isGameInProgress(gameStatus)

            }.collectLatest { turnOn ->
                if (turnOn) {
                    gameStatusHolder.resumeTimeSpan()
                } else {
                    gameStatusHolder.pauseTimeSpan()
                }
            }
        }

        jobs += restartableCoroutineScope.launch {
            gameStatusHolder.bombsLeftFlow.collectLatest {
                bombsLeftFlow.value = it
            }
        }

        jobs += restartableCoroutineScope.launch {
            gameStatusHolder.gameStatusWithElapsedFlow.collectLatest {
                gameStatusWithElapsedFlow.value = it
            }
        }
    }

    fun stop() {
        bombsLeftFlow.value = 0
        gameStatusWithElapsedFlow.value = null
        jobs.map {
            it.cancel()
        }
        jobs.clear()
    }
}