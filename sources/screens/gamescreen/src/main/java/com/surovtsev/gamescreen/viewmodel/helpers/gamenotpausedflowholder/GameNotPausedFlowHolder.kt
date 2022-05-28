package com.surovtsev.gamescreen.viewmodel.helpers.gamenotpausedflowholder

import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.customcoroutinescope.RestartableCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
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