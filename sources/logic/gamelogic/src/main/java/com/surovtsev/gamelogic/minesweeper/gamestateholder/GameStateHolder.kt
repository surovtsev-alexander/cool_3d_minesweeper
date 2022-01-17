package com.surovtsev.gamelogic.minesweeper.gamestateholder

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.dagger.dependencies.GameStateDependencies
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamestate.GameState
import com.surovtsev.gamestate.dagger.DaggerGameStateComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

typealias GameStateFlow = StateFlow<GameState?>

@GameScope
class GameStateHolder @Inject constructor(
    private val gameStateDependencies: GameStateDependencies,
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
            gameStateDependencies.appComponentEntryPoint,
            gameStateDependencies.timeSpanComponentEntryPoint,
            tryToLoad
        )
    }

    fun newGame(
        tryToLoad: Boolean
    ) {
        _gameStateFlow.value = createGameState(tryToLoad)
    }
}
