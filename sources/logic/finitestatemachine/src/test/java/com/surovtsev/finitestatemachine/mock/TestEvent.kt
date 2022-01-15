package com.surovtsev.finitestatemachine.mock

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.event.MandatoryEvents

interface TestEvent: Event {

    abstract class TestEventImp(
        override val doNotPushToQueue: Boolean = false,
        override val pushToHead: Boolean = false
    ): TestEvent

    object Init: TestEventImp(), Event.Init
    object CloseError: TestEventImp(), Event.CloseError
    object EmptyEvent: TestEventImp(doNotPushToQueue = true)

    object Pause: Event.Pause(), TestEvent
    object Resume: Event.Resume(), TestEvent

    object MandatoryTestEvents: MandatoryEvents(
        init = Init,
        closeError =  CloseError
    )
}
