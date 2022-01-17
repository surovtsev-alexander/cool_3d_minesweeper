package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.finitestatemachine.stateholder.StateHolderImp
import kotlinx.coroutines.flow.StateFlow

abstract class TemplateScreenViewModelAlt<E: EventToViewModelAlt, D: ScreenData>(
    screenEventChecker: EventChecker<E, D>,
    screenEventProcessor: EventProcessor<E>,
    finishActionHolder: FinishActionHolder,
    initialState: State<D>,
    private val mandatoryEvents: EventToViewModelAlt.MandatoryEvents<E>,
):
    ViewModel(),
    DefaultLifecycleObserver
{
    private val stateHolder = StateHolderImp(
        initialState,
        true,
    )
    private val fsm = FiniteStateMachine(
        stateHolder,
        TemplateScreenViewModelEventChecker(screenEventChecker),
        TemplateScreenViewModelEventProcessor(
            screenEventProcessor,
            finishActionHolder,
        ),
        viewModelScope,
    )

    val stateFlow: StateFlow<StateDescriptionWithData<out D>> = fsm.stateHolder.state

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        fsm.receiveEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }
}
