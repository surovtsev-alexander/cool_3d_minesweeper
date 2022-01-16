package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.helpers.concrete.FSMState
import com.surovtsev.finitestatemachine.state.StateWithData
import kotlinx.coroutines.flow.StateFlow

abstract class TemplateScreenViewModelAlt<E: EventToViewModelAlt, D: ScreenData>(
    screenEventChecker: EventChecker<E, D>,
    screenEventProcessor: EventProcessor<E>,
    finishActionHolder: FinishActionHolder,
    initialState: FSMState<D>,
    private val mandatoryEvents: EventToViewModelAlt.MandatoryEvents<E>,
):
    ViewModel(),
    DefaultLifecycleObserver
{
    private val fsm = FiniteStateMachine(
        TemplateScreenViewModelEventChecker(screenEventChecker),
        TemplateScreenViewModelEventProcessor(
            screenEventProcessor,
            finishActionHolder,
        ),
        viewModelScope,
        true,
        initialState,
    )

    val state: StateFlow<StateWithData<out D>> = fsm.state

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        fsm.handleEvent(
            mandatoryEvents.handleScreenLeavingEventFactory(owner)
        )
    }
}
