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


package com.surovtsev.gamescreen.viewmodel.helpers.gamenotpausedflowholder

import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.restartablescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscription.Subscription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 *   Game is not paused if only
 * screenState.description is Idle and
 * screenState.data is GameInProgress.
 */
@GameScreenScope
class GameNotPausedFlowHolder @Inject constructor(
    private val screenStateFlow: ScreenStateFlow,
    restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
): Subscription {

    private val _gameNotPausedFlow = MutableStateFlow(false)
    val gameNotPausedFlow: GameNotPausedFlow = _gameNotPausedFlow.asStateFlow()

    init {
        SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
            restartableCoroutineScopeComponent,
            "GameScreen:GameNotPausedFlowHolder"
        ).subscriptionsHolder.addSubscription(this)
    }

    override fun initSubscription(restartableCoroutineScope: RestartableCoroutineScope) {
        val gameNotPausedFlowLocal = runBlocking {
            screenStateFlow.map { screenState ->
                screenState.description is Description.Idle &&
                        screenState.data is GameScreenData.GameInProgress
            }.stateIn(
                restartableCoroutineScope
            )
        }

        restartableCoroutineScope.launch {
            gameNotPausedFlowLocal.collectLatest {
                _gameNotPausedFlow.value = it
            }
        }
    }
}