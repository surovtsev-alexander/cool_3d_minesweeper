package com.surovtsev.gamestate.models.game.gamestatus

import kotlinx.coroutines.flow.StateFlow


data class GameStatusWithElapsed(
    val gameStatus: GameStatus = GameStatusHelper.initStatus,
    val elapsed: Long = 0L,
)

typealias GameStatusWithElapsedFlow = StateFlow<GameStatusWithElapsed>
