package com.surovtsev.gamescreen.viewmodel.helpers

import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.GameScreenData
import com.surovtsev.gamescreen.viewmodel.GameScreenStateFlow
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.Subscription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@GameScreenScope
class GameNotPausedFlowHolder @Inject constructor(
    private val gameScreenStateFlow: GameScreenStateFlow,
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

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        val gameNotPausedFlowLocal = runBlocking {
            gameScreenStateFlow.map { screenState ->
                screenState.description is StateDescription.Idle &&
                        screenState.data is GameScreenData.GameInProgress
            }.stateIn(
                customCoroutineScope
            )
        }

        customCoroutineScope.launch {
            gameNotPausedFlowLocal.collectLatest {
                _gameNotPausedFlow.value = it
            }
        }
    }
}