package com.surovtsev.finitestatemachine.interfaces

import com.surovtsev.finitestatemachine.event.Event

interface EventReceiver {
    fun receiveEvent(
        event: Event
    )
}
