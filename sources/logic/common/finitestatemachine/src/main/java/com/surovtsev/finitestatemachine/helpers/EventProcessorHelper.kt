package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventhandler.EventHandlers
import com.surovtsev.finitestatemachine.eventhandler.EventHandlingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessingresult.EventProcessingResult
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessorPriority
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat


class EventProcessorHelper(
    private val logConfig: LogConfig,
    private val stateHolder: StateHolder,
    private val eventHandlers: EventHandlers,
    private val fsmQueueHolder: FSMQueueHolder,
) {
    private val processingMutex = Mutex(locked = false)

    companion object {
        const val EVENT_PROCESSING_CHANGES = 1
    }

    enum class Errors(
        val message: String
    ) {
        FSM_INTERNAL_ERROR_001("FSM_INTERNAL_ERROR_001");
    }

    suspend fun processEvent(
        event: Event
    ) {
        processingMutex.withLock {
            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "processEvent; state before processing: ${stateHolder.state.value}" }
            }


            if (logConfig.logLevel.isGreaterThan1()) {
                logcat { "processEvent: $event" }
            }

            var attemptsToProcess = 0
            var eventToProcess = event
            var eventProcessorHelperResult: EventProcessorHelperResult? = null

            while (attemptsToProcess < EVENT_PROCESSING_CHANGES) {
                attemptsToProcess++

                eventProcessorHelperResult = tryToProcessEvent(eventToProcess)

                when (eventProcessorHelperResult) {
                    is EventProcessorHelperResult.Skipped -> {
                        break
                    }
                    is EventProcessorHelperResult.Processed -> {
                        break
                    }
                    is EventProcessorHelperResult.Error -> {
                        stateHolder
                            .publishErrorState(
                                eventProcessorHelperResult.message
                            )
                        break
                    }
                    is EventProcessorHelperResult.ChangeWith -> {
                        eventToProcess = eventProcessorHelperResult.event
                    }
                }
            }

            if (logConfig.logLevel.isGreaterThan1()) {
                logcat {
                    "result of processing event after $attemptsToProcess:" +
                            "$eventProcessorHelperResult"
                }
            }

            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "processEvent; state after processing: ${stateHolder.state.value}" }
            }

        }
    }

    private suspend fun tryToProcessEvent(
        event: Event
    ): EventProcessorHelperResult {
        if (logConfig.logLevel.isGreaterThan2()) {
            logcat { "tryToProcessEvent: $event" }
        }

        // step 1. Calculation EventHandlingResults for each fsm.
        // EventHandlingResults: Skip, Process, etc.
        val eventHandlingResults = calculateHandlingResults(event)


        // step 2. Stop processing event if each EventHandlingResult is Skip.
        if (areAllSkip(eventHandlingResults)) {
            return EventProcessorHelperResult.Skipped
        }


        // step 3. Finish processing this event and notify about error.
        val firstError = getFirstErrorResult(
            eventHandlingResults
        )
        if (firstError != null) {
            return EventProcessorHelperResult.Error(
                firstError.message
            )
        }

        // step 4. Changing events.
        // Change CloseEvent with CloseAndFinishEvent, for example.
        // Continue processing if there is no EventHandlingResult.ChangeWith.
        // Return new event if there is only one EventHandlingResult.ChangeWith.
        // Return error otherwise.
        val changeEventResults = getChangeEventsResult(
            eventHandlingResults
        )

        when(changeEventResults.count()) {
            0 -> { }
            1 -> {
                return EventProcessorHelperResult.ChangeWith(
                    changeEventResults[0].event
                )
            }
            else -> {
                return EventProcessorHelperResult.Error(
                    Errors.FSM_INTERNAL_ERROR_001.message
                )
            }
        }


        // step 5. Setting loading state before processing.
        // Setting new state is up to user in processing results step (6).
        if (event.setLoadingStateBeforeProcessing) {
            stateHolder.publishLoadingState()
        }


        // step 6. Processing results.
        val eventProcessingResults = calculateEventProcessingResults(
            eventHandlingResults,
            logConfig,
        )

        if (logConfig.logLevel.isGreaterThan3()) {
            logcat { "eventProcessingResults: $eventProcessingResults" }
        }


        // step 7. Pushing new events.
        pushNewEventsIfRequired(
            eventProcessingResults
        )

        return EventProcessorHelperResult.Processed
    }

    private sealed interface EventProcessorHelperResult {
        object Skipped: EventProcessorHelperResult
        object Processed: EventProcessorHelperResult
        class Error(
            val message: String
        ): EventProcessorHelperResult
        class ChangeWith(
            val event: Event
        ): EventProcessorHelperResult
    }

    /// region [auxiliary functions]
    private fun calculateHandlingResults(
        event: Event,
    ): List<EventHandlingResult> {
        return eventHandlers.map {
            it.handleEvent(event, stateHolder.state.value)
        }
    }

    private fun areAllSkip(
        eventHandlingResults: List<EventHandlingResult>
    ): Boolean {
        return eventHandlingResults.firstOrNull {
            it !is EventHandlingResult.Skip
        } == null
    }

    private fun getFirstErrorResult(
        eventHandlingResults: List<EventHandlingResult>
    ): EventHandlingResult.RaiseError? {
        return eventHandlingResults.firstOrNull {
            it is EventHandlingResult.RaiseError
        } as? EventHandlingResult.RaiseError
    }

    private fun getChangeEventsResult(
        eventHandlingResults: List<EventHandlingResult>
    ): List<EventHandlingResult.ChangeWith> {
        return eventHandlingResults
            .filterIsInstance<EventHandlingResult.ChangeWith>()
    }

    private suspend fun calculateEventProcessingResults(
        eventHandlingResults: List<EventHandlingResult>,
        logConfig: LogConfig,
    ): List<EventProcessingResult?> {
        val eventProcessorsCount = eventHandlingResults.size

        val res = Array<EventProcessingResult?>(eventProcessorsCount) { null }

        for (priority in EventProcessorPriority.values()) {
            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "calculateEventProcessingResults for: $priority" }
            }
            for (i in 0 until eventProcessorsCount) {
                val eventHandlingResult = eventHandlingResults[i]

                (eventHandlingResult as? EventHandlingResult.Process)?.let {
                    if (it.eventProcessor.priority == priority) {
                        res[i] = it.eventProcessor.action.invoke()
                    }
                }
            }
        }

        return res.toList()
    }

    private suspend fun pushNewEventsIfRequired(
        eventProcessingResults: List<EventProcessingResult?>
    ) {
        eventProcessingResults
            .filterIsInstance<EventProcessingResult.Ok>()
            .map {
                it.newEventToPush?.let { eventToPush ->
                    logcat { "eventToPush: $eventToPush" }
                    fsmQueueHolder.pushEvent(eventToPush)
                }
            }
    }
    /// endregion [auxiliary functions]
}