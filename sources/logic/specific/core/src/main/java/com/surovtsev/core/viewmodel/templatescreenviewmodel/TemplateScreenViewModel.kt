package com.surovtsev.core.viewmodel.templatescreenviewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventhandler.TemplateScreenViewModelEventHandler
import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.errordialog.ErrorDialogPlacer
import com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.finishactionholder.FinishActionHolder
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl

abstract class TemplateScreenViewModel:
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
        userEventHandler: EventHandler,
    ): FiniteStateMachine {
        return FiniteStateMachine(
            stateHolder,
            listOf(
                templateScreenViewModelEventHandler,
                userEventHandler,
            ),
            logConfig = LogConfig(
                LogLevel.LOG_LEVEL_1,
            )
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        finiteStateMachine.receiveEvent(
            EventToViewModel.HandleScreenLeaving(owner)
        )
    }
}
