package com.surovtsev.finitestatemachine.event

interface Event {
    val setLoadingStateBeforeProcessing: Boolean
    val doNotPushToQueue: Boolean
    val pushToHead: Boolean

    interface Init: Event
    interface CloseError: Event

    abstract class EventImp(
        override val setLoadingStateBeforeProcessing: Boolean,
        override val doNotPushToQueue: Boolean,
        override val pushToHead: Boolean,
    ): Event

    interface ProcessedByFSM: Event

    abstract class PauseResumeEvent: EventImp(
        false,
        false,
        true
    ), ProcessedByFSM

    abstract class Pause: PauseResumeEvent()
    abstract class Resume: PauseResumeEvent()
}
