package com.surovtsev.finitestatemachine.eventhandlerOld

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandlerOld.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.data.Data

class EventHandlerOld<E: Event, D: Data>(
    val eventChecker: EventChecker<E, D>,
    val eventProcessor: EventProcessor<E>,
)
