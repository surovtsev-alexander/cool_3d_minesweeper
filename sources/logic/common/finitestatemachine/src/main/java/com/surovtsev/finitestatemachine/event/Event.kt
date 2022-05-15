package com.surovtsev.finitestatemachine.event

sealed interface Event {
    object ToDefault: EventImp(
        EventMode.DoNotWaitEndOfProcessing,
    )
    object Pause: PauseResumeEvent()
    object Resume: PauseResumeEvent()

    interface UserEvent: Event


    abstract class PauseResumeEvent: EventImp(
        EventMode.Normal(
            setLoadingStateBeforeProcessing = false,
            pushToHead = true,
        )
    )

    abstract class EventImp(
        override val eventMode: EventMode,
    ): Event


    val eventMode: EventMode

    sealed interface EventMode {
        val setLoadingStateBeforeProcessing: Boolean
        val pushToHead: Boolean
        val doNotPushToQueue: Boolean

        class Normal(
            override val setLoadingStateBeforeProcessing: Boolean = true,
            override val pushToHead: Boolean = false,
        ): EventMode {
            override val doNotPushToQueue: Boolean = false
        }

        class DoNotPushToQueue(
            override val setLoadingStateBeforeProcessing: Boolean = false,
            override val pushToHead: Boolean = false,
        ): EventMode {
            override val doNotPushToQueue: Boolean = true
        }

        object DoNotWaitEndOfProcessing: EventMode {
            override val setLoadingStateBeforeProcessing: Boolean = false
            override val pushToHead: Boolean = false
            override val doNotPushToQueue: Boolean = true
        }
    }

}
