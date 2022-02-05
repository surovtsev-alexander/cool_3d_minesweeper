package com.surovtsev.gamestate.logic.models.game.gamestatus

import com.surovtsev.core.models.game.config.GameConfig
import kotlinx.coroutines.flow.StateFlow


data class GameStatusWithElapsedForGameConfig(
    val gameConfig: GameConfig,
    val gameStatus: GameStatus = GameStatusHelper.initStatus,
    val elapsed: Long = 0L,
)

typealias GameStatusWithElapsedForGameConfigFlow = StateFlow<GameStatusWithElapsedForGameConfig>
