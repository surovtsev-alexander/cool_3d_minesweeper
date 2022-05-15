package com.surovtsev.finitestatemachine.event

sealed interface Event {
    object ToDefault: EventImp(
        false,
        false,
        true,
        true,
    )
    object Pause: PauseResumeEvent()
    object Resume: PauseResumeEvent()

    interface UserEvent: Event


    abstract class PauseResumeEvent: EventImp(
        false,
        false,
        true,
        false,
    )

    abstract class EventImp(
        override val setLoadingStateBeforeProcessing: Boolean,
        override val doNotPushToQueue: Boolean,
        override val pushToHead: Boolean,
        override val doNotWaitEndOfProcessing: Boolean,
    ): Event


    val setLoadingStateBeforeProcessing: Boolean
    val doNotPushToQueue: Boolean
    val pushToHead: Boolean
    val doNotWaitEndOfProcessing: Boolean
}
