package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.event.Event

sealed class TestEvent(
    override val doNotPushToQueue: Boolean = false,
    override val pushToHead: Boolean = false,
    override val setLoadingStateBeforeProcessing: Boolean = true,
): Event.UserEvent {

    object Init: TestEvent()
    object CloseError: TestEvent()
    object EmptyEvent: TestEvent(doNotPushToQueue = true)
}
