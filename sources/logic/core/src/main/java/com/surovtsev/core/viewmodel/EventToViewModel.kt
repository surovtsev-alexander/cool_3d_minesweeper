package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event

typealias HandleScreenLeavingEventFactory<E> = (owner: LifecycleOwner) -> E

interface EventToViewModel: Event {

    interface HandleScreenLeaving: EventToViewModel {
        val owner: LifecycleOwner
    }

    interface CloseError: EventToViewModel
    interface CloseErrorAndFinish: CloseError

    interface Init: EventToViewModel
    interface Finish: EventToViewModel

    abstract class MandatoryEvents <E: EventToViewModel>(
        val init: Init,
        val closeError: CloseError,
        val closeErrorAndFinish: CloseErrorAndFinish,
        val handleScreenLeavingEventFactory: HandleScreenLeavingEventFactory<E>,
    )
}
