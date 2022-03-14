package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event

sealed interface EventToViewModel: Event.UserEvent {
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
    ): EventToViewModel

    abstract class MandatoryEvents(
        val init: Event,
    ) {
        init {
            // TODO: 17.01.2022 refactor
            assert(
                init is InitEvent
            )
        }
    }
}


interface CloseErrorEvent
interface InitEvent
