package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import kotlinx.coroutines.CoroutineScope

typealias FiniteStateMachineFactory<E, D> =
    (
            eventHandler: EventHandler<E, D>,
            coroutineScope: CoroutineScope
    ) -> FiniteStateMachine<E, D>
