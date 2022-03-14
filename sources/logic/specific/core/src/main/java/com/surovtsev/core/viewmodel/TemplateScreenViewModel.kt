package com.surovtsev.core.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.core.viewmodel.finitestatemachine.eventhandler.TemplateScreenViewModelEventHandler
import com.surovtsev.core.viewmodel.helpers.FinishActionHolder
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder

abstract class TemplateScreenViewModel(
    final override val mandatoryEvents: EventToViewModel.MandatoryEvents,
):
    ViewModel(),
    ErrorDialogPlacer,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    val finishActionHolder = FinishActionHolder()

    protected val stateHolder: StateHolder = StateHolder(true)
    override val screenStateFlow: ScreenStateFlow
        get() = stateHolder.state

    private val templateScreenViewModelEventHandler = TemplateScreenViewModelEventHandler(
        stateHolder,
        finishActionHolder,
    )

    fun createFiniteStateMachine(
        eventHandler: EventHandler,
        subscriptionsHolder: SubscriptionsHolder,
    ): FiniteStateMachine {
        return FiniteStateMachine(
            stateHolder,
            arrayOf(
                templateScreenViewModelEventHandler,
                eventHandler,
            ),
            subscriptionsHolder,
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        finiteStateMachine.receiveEvent(
            EventToViewModel.HandleScreenLeaving(owner)
        )
    }
}
