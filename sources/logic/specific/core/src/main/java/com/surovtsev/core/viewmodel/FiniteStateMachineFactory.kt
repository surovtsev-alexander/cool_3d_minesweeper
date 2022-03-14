package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder

typealias FiniteStateMachineFactory<D> =
    (
        eventHandler: EventHandler<D>,
        subscriptionsHolder: SubscriptionsHolder,
    ) -> FiniteStateMachine<D>
