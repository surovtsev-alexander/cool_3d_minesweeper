package com.surovtsev.templateviewmodel.finitestatemachine.eventtoviewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event

sealed interface EventToViewModel: Event.UserEvent {
    object Init: EventToViewModelImp()
    object Finish: EventToViewModelImp()
    object CloseError: EventToViewModelImp(), CloseErrorEvent
    object CloseErrorAndFinish: EventToViewModelImp(), CloseErrorEvent
    class HandleScreenLeaving(
        val owner: LifecycleOwner
    ): EventToViewModelImp()

    abstract class UserEvent: EventToViewModelImp()


    abstract class EventToViewModelImp(
        override val doNotPushToQueue: Boolean = false,
        override val pushToHead: Boolean = false,
        override val setLoadingStateBeforeProcessing: Boolean = true,
        override val doNotWaitEndOfProcessing: Boolean = false,
    ): EventToViewModel
}


interface CloseErrorEvent
