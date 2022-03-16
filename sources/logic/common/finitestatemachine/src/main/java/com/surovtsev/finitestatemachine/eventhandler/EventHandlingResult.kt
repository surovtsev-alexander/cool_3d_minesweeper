package com.surovtsev.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessor

sealed interface EventHandlingResult {
    class RaiseError(
        val message: String
    ): EventHandlingResult

    class Process(
        val eventProcessor: EventProcessor
    ): EventHandlingResult

    object Skip: EventHandlingResult

    class ChangeWith(
        val event: Event,
    ): EventHandlingResult

    object GeneratorHelper {
        fun processOrSkipIfNull(
            eventProcessor: EventProcessor?,
        ): EventHandlingResult {
            return if (eventProcessor == null) {
                Skip
            } else {
                Process(
                    eventProcessor
                )
            }
        }
    }
}