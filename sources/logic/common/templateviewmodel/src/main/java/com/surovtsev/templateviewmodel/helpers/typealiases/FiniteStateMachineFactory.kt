package com.surovtsev.templateviewmodel.helpers.typealiases

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.eventhandler.EventHandler

typealias FiniteStateMachineFactory =
    (
        userEventHandler: EventHandler,
    ) -> FiniteStateMachine
