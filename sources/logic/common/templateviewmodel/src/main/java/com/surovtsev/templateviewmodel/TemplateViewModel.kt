package com.surovtsev.templateviewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.templateviewmodel.finitestatemachine.eventhandler.TemplateViewModelEventHandler
import com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.templateviewmodel.helpers.errordialog.ErrorDialogPlacer
import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.templateviewmodel.helpers.finishactionholder.FinishActionHolder
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl

abstract class TemplateViewModel:
    ViewModel(),
    ErrorDialogPlacer,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl(),
    DefaultLifecycleObserver
{
    val finishActionHolder = FinishActionHolder()

    protected val stateHolder: StateHolder = StateHolder(true)
    override val screenStateFlow: ScreenStateFlow
        get() = stateHolder.state

    private val templateViewModelEventHandler = TemplateViewModelEventHandler(
        stateHolder,
        finishActionHolder,
    )

    fun createFiniteStateMachine(
        userEventHandler: EventHandler,
    ): FiniteStateMachine {
        return FiniteStateMachine(
            stateHolder,
            listOf(
                templateViewModelEventHandler,
                userEventHandler,
            ),
            logConfig = LogConfig(
                LogLevel.LOG_LEVEL_3,
            )
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        finiteStateMachine.eventReceiver.receiveEvent(
            EventToViewModel.HandleScreenLeaving(owner)
        )
    }

    fun restartFSM(
        startingEvent: Event = EventToViewModel.Init,
    ) {
        launchOnIOThread {
            finiteStateMachine.restartFSM(
                startingEvent
            )
        }
    }
}
