package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatus
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusWithElapsedForGameConfig
import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
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