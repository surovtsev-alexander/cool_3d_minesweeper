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
    ): EventToViewModelImp(
        eventMode = Event.EventMode.DoNotWaitEndOfProcessing,
    )

    abstract class UserEvent: EventToViewModelImp()


    abstract class EventToViewModelImp(
        override val eventMode: Event.EventMode = Event.EventMode.Normal(
            setLoadingStateBeforeProcessing = true,
            pushToHead = false,
        ),
    ): EventToViewModel
}


interface CloseErrorEvent
