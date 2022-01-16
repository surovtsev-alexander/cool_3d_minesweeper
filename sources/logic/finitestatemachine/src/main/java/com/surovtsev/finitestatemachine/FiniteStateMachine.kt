package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.QueueHolder
import com.surovtsev.finitestatemachine.state.data.Data

open class FiniteStateMachine<E: Event, D: Data>(
    val queueHolder: QueueHolder<E, D>,
)
