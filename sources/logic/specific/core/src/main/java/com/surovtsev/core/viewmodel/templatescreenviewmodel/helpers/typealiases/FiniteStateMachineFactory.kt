package com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.typealiases

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder

typealias FiniteStateMachineFactory =
    (
        userEventHandler: EventHandler,
    ) -> FiniteStateMachine
