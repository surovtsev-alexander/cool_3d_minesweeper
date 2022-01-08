package com.surovtsev.gamelogic.minesweeper.gameState

import com.surovtsev.gamestate.GameState
import com.surovtsev.gamestate.dagger.GameScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

typealias GameStateFlow = StateFlow<GameState>

@GameScope
class GameStateHolder @Inject constructor(
    gameState: GameState,
) {
    private val _gameStateFlow = MutableStateFlow(gameState)
    val gameStateFlow: GameStateFlow = _gameStateFlow.asStateFlow()
}
