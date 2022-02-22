package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder

typealias FiniteStateMachineFactory<E, D> =
    (
        eventHandler: EventHandler<E, D>,
        subscriptionsHolder: SubscriptionsHolder,
    ) -> FiniteStateMachine<E, D>
