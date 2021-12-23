package com.surovtsev.gamescreen.minesweeper.gamelogic.helpers

import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatusHelper
import kotlinx.coroutines.flow.StateFlow


data class GameStatusWithElapsed(
    val gameStatus: GameStatus = GameStatusHelper.initStatus,
    val elapsed: Long = 0L,
)

typealias GameStatusWithElapsedFlow = StateFlow<GameStatusWithElapsed>
