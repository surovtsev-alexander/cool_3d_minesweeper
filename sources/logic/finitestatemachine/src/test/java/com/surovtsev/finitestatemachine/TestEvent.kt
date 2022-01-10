package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.event.MandatoryEvents

abstract class TestEvent(
    override val skipIfFSMIsBusy: Boolean = false
): Event {

    object Init: TestEvent(), Event.Init
    object CloseError: TestEvent(), Event.CloseError
    object SkipEvent: TestEvent(skipIfFSMIsBusy = true)

    object MandatoryTestEvents: MandatoryEvents(
        init = Init,
        closeError =  CloseError
    )
}
