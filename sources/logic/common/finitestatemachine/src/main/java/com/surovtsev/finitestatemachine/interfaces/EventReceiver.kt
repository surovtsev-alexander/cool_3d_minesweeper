package com.surovtsev.finitestatemachine.interfaces

import com.surovtsev.finitestatemachine.event.Event

interface EventReceiver<E: Event> {
    fun receiveEvent(
        event: E
    )
}
