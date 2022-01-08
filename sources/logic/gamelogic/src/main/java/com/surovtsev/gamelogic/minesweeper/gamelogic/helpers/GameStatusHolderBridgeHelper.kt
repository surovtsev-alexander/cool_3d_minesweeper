package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatus
import com.surovtsev.gamelogic.models.game.gamestatus.GameStatusHelper
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GameStatusHolderBridgeHelper(
    private val gameStatusHolder: GameStatusHolder,
    private val gameNotPausedFlow: GameNotPausedFlow,
    customCoroutineScope: CustomCoroutineScope,
) {
    var job: Job? = null

    init {
        job = customCoroutineScope.launch {
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
    }

    fun stop() {
        job?.cancel()
    }
}