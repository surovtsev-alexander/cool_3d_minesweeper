package com.surovtsev.finitestatemachine.event

sealed interface Event {
    object Pause: PauseResumeEvent()
    object Resume: PauseResumeEvent()

    interface UserEvent: Event


    abstract class PauseResumeEvent: EventImp(
        false,
        false,
        true
    )

    abstract class EventImp(
        override val setLoadingStateBeforeProcessing: Boolean,
        override val doNotPushToQueue: Boolean,
        override val pushToHead: Boolean,
    ): Event


    val setLoadingStateBeforeProcessing: Boolean
    val doNotPushToQueue: Boolean
    val pushToHead: Boolean
}
