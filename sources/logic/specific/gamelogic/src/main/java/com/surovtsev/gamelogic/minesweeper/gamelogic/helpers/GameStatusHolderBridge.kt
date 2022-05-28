package com.surovtsev.gamelogic.minesweeper.gamelogic.helpers

import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusWithElapsedForGameConfig
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@GameScope
class GameStatusHolderBridge @Inject constructor(
    private val gameStateHolder: GameStateHolder,
    private val gameNotPausedFlow: GameNotPausedFlow,
    private val restartableCoroutineScope: RestartableCoroutineScope,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    private val _bombsLeftFlow = MutableStateFlow(0)
    val bombsLeftFlow: BombsLeftFlow = _bombsLeftFlow.asStateFlow()

    private val _gameStatusWithElapsedFlow = MutableStateFlow<GameStatusWithElapsedForGameConfig?>(null)
    val gameStatusWithElapsedFlow = _gameStatusWithElapsedFlow.asStateFlow()

    private var gameStatusHolderBridgeHelper: GameStatusHolderBridgeHelper? = null

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            gameStateHolder.gameStateFlow.collectLatest {
                gameStatusHolderBridgeHelper?.stop()
                gameStatusHolderBridgeHelper = if (it == null) {
                    null
                } else {
                    createGameStatusHolderBridgeHelper(
                        it,
                        gameNotPausedFlow,
                    )
                }
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
            restartableCoroutineScope,
        )
    }
}