package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.event.Event

interface EventReceiver<E: Event> {
    fun pushEventAsync(
        event: E
    )

    suspend fun pushEvent(
        event: E
    )
}
