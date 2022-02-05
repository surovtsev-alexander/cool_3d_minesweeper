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
        val init: E,
        val closeError: E,
        val closeErrorAndFinish: E,
        val handleScreenLeavingEventFactory: HandleScreenLeavingEventFactory<E>,
    ) {
        init {
            // TODO: 17.01.2022 refactor
            assert(
                init is Init
            )
            assert(
                closeError is CloseError
            )
            assert(
                closeErrorAndFinish is CloseErrorAndFinish
            )
        }
    }
}
