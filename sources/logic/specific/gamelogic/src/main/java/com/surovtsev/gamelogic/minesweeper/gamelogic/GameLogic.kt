/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.minesweeper.gamelogic

import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.utils.utils.gles.TextureUpdater
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
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
