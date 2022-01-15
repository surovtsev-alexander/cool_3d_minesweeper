package com.surovtsev.finitestatemachine.event

interface Event {
    val doNotPushToQueue: Boolean
    val pushToHead: Boolean

    interface Init: Event
    interface CloseError: Event

    abstract class Pause(
        override val doNotPushToQueue: Boolean = false,
        override val pushToHead: Boolean = true,
    ): Event
    abstract class Resume(
        override val doNotPushToQueue: Boolean = false,
        override val pushToHead: Boolean = true
    ): Event
}
