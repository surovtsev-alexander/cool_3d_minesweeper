package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamestate.models.game.gamestatus.GameStatus
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHelper
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusWithElapsed
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameStatusHolderBridgeHelper(
    private val gameStatusHolder: GameStatusHolder,
    private val gameNotPausedFlow: GameNotPausedFlow,
    private val bombsLeftFlow: MutableStateFlow<Int>,
    private val gameStatusWithElapsedFlow: MutableStateFlow<GameStatusWithElapsed>,
    customCoroutineScope: CustomCoroutineScope,
) {
    var jobs: MutableList<Job> = emptyList<Job>().toMutableList()

    init {
        jobs += customCoroutineScope.launch {
             gameNotPausedFlow.combine(
                gameStatusHolder.gameStatusWithElapsedFlow
            ) { gameNotPaused: Boolean, (gameStatus: GameStatus, _) ->

                gameNotPaused and GameStatusHelper.isGameInProgress(gameStatus)

            }.collectLatest { turnOn ->
                if (turnOn) {
                    gameStatusHolder.resumeTimeSpan()
                } else {
                    gameStatusHolder.pauseTimeSpan()
                }
            }
        }

        jobs += customCoroutineScope.launch {
            gameStatusHolder.bombsLeftFlow.collectLatest {
                bombsLeftFlow.value = it
            }
        }

        jobs += customCoroutineScope.launch {
            gameStatusHolder.gameStatusWithElapsedFlow.collectLatest {
                gameStatusWithElapsedFlow.value = it
            }
        }
    }

    fun stop() {
        jobs.map {
            it.cancel()
        }
        jobs.clear()
    }
}