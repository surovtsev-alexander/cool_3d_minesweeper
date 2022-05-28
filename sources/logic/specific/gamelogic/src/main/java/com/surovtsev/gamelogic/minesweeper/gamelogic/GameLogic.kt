package com.surovtsev.gamelogic.minesweeper.gamelogic

import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.restartablecoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablecoroutinescope.subscriptionsholder.SubscriptionsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GameLogic(
    private val gameStateHolder: GameStateHolder,
    private val textureUpdater: TextureUpdater,
    private val gameControls: GameControls,
    subscriptionsHolder: SubscriptionsHolder,
): Subscription {
    private val _gameTouchHandlerFlow = MutableStateFlow<GameTouchHandler?>(null)
    val gameTouchHandlerFlow = _gameTouchHandlerFlow.asStateFlow()

    init {
        subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        restartableCoroutineScope.launch {
            gameStateHolder.gameStateFlow.collectLatest {
                _gameTouchHandlerFlow.value = if (it == null) {
                    null
                } else {
                    createGameTouchHandler(it)
                }
            }
        }
    }

    private fun createGameTouchHandler(
        gameState: GameState
    ): GameTouchHandler {
        return GameTouchHandler(
            gameState,
            gameControls,
            textureUpdater,
        )
    }
}
