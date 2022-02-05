package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.state.data.Data

class EventHandler<E: Event, D: Data>(
    val eventChecker: EventChecker<E, D>,
    val eventProcessor: EventProcessor<E>,
)
