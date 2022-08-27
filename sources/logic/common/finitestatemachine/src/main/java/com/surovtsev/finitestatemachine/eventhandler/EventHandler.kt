package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.state.State


interface EventHandler {
    fun handleEvent(
        event: Event,
        state: State
    ): EventHandlingResult

    // TODO: populate in children and add usage
    val transitions: List<Transition>

    data class Transition(
        val initialState: State,
        val event: Event,
        val resultStates: State,
    )
}
