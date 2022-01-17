package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gameState.GameStateHolder
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamestate.GameState
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusWithElapsedFlowForGameConfig
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class GameStatusHolderBridge @Inject constructor(
    private val gameStateHolder: GameStateHolder,
    private val gameNotPausedFlow: GameNotPausedFlow,
    private val customCoroutineScope: CustomCoroutineScope,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    private val _bombsLeftFlow = MutableStateFlow(0)
    val bombsLeftFlow: BombsLeftFlow = _bombsLeftFlow.asStateFlow()

    private val _gameStatusWithElapsedFlow = MutableStateFlow(gameStateHolder.gameStateFlow.value.gameStatusHolder.gameStatusWithElapsedFlow.value)
    val gameStatusWithElapsedFlow: GameStatusWithElapsedFlowForGameConfig = _gameStatusWithElapsedFlow.asStateFlow()

    private val _gameStatusHolderBridgeHelper = MutableStateFlow(
        createGameStatusHolderBridgeHelper(
            gameStateHolder.gameStateFlow.value,
            gameNotPausedFlow,
        )
    )

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            gameStateHolder.gameStateFlow.collectLatest {
                _gameStatusHolderBridgeHelper.value.stop()
                _gameStatusHolderBridgeHelper.value = createGameStatusHolderBridgeHelper(
                    it,
                    gameNotPausedFlow,
                )
            }
        }
    }

    private fun createGameStatusHolderBridgeHelper(
        gameState: GameState,
        gameNotPausedFlow: GameNotPausedFlow,
    ): GameStatusHolderBridgeHelper {
        return GameStatusHolderBridgeHelper(
            gameState.gameStatusHolder,
            gameNotPausedFlow,
            _bombsLeftFlow,
            _gameStatusWithElapsedFlow,
            customCoroutineScope,
        )
    }
}