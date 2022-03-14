package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event

typealias HandleScreenLeavingEventFactory = (owner: LifecycleOwner) -> Event

interface EventToViewModel: Event {

    interface HandleScreenLeaving: EventToViewModel {
        val owner: LifecycleOwner
    }

    interface CloseError: EventToViewModel
    interface CloseErrorAndFinish: CloseError

    interface Init: EventToViewModel
    interface Finish: EventToViewModel

    abstract class MandatoryEvents(
        val init: Event,
        val closeError: Event,
        val closeErrorAndFinish: Event,
        val handleScreenLeavingEventFactory: HandleScreenLeavingEventFactory,
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
