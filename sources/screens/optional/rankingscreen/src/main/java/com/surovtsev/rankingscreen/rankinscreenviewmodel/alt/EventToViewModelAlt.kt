package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.finitestatemachine.event.Event

typealias HandleScreenLeavingEventFactoryAlt<E> = (owner: LifecycleOwner) -> E


interface EventToViewModelAlt: Event {
    interface HandleScreenLeaving: EventToViewModelAlt {
        val owner: LifecycleOwner
    }

    interface CloseError: EventToViewModelAlt
    interface CloseErrorAndFinish: CloseError

    interface Init: EventToViewModelAlt
    interface Finish: EventToViewModelAlt

    abstract class MandatoryEvents <E: EventToViewModelAlt>(
        val init: Init,
        val closeError: CloseError,
        val closeErrorAndFinish: CloseErrorAndFinish,
        val handleScreenLeavingEventFactory: HandleScreenLeavingEventFactoryAlt<E>,
    )
}