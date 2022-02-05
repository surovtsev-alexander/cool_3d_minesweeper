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
