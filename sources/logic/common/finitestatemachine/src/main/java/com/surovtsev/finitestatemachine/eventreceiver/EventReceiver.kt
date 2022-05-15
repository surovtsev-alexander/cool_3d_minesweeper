package com.surovtsev.finitestatemachine.eventreceiver

import com.surovtsev.finitestatemachine.event.Event

interface EventReceiver {
    fun receiveEvent(
        event: Event
    )
}
