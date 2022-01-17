package com.surovtsev.finitestatemachine

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessingResult
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.finitestatemachine.utils.ProcessingTrigger
import com.surovtsev.finitestatemachine.utils.ProcessingTriggerImp
import com.surovtsev.finitestatemachine.utils.ProcessingWaiter
import com.surovtsev.finitestatemachine.utils.ProcessingWaiterImp
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

open class FiniteStateMachine<E: Event, D: Data>(
    val stateHolder: StateHolder<D>,
    private val eventChecker: EventChecker<E, D>,
    private val eventProcessor: EventProcessor<E>,
    private val coroutineScope: CoroutineScope,
    private val logConfig: LogConfig = LogConfig(logLevel = LogLevel.LOG_LEVEL_1),

    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp(),
    private val processingTrigger: ProcessingTrigger = ProcessingTriggerImp(),
) {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val processingMutex = Mutex(locked = false)

    private val queueMutex = Mutex(locked = false)

    private val eventsQueue = emptyList<E>().toMutableList()

    private var paused: Boolean = false
        private set

    private suspend fun pollEvent(): E? {
        return queueMutex.withLock {
            if (eventsQueue.count() == 0) {
                null
            } else {
                if (!paused) {
                    eventsQueue.removeAt(0)
                } else {
                    val first = eventsQueue[0]

                    if (first is Event.Pause || first is Event.Resume) {
                        eventsQueue.removeAt(0)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun isQueueEmpty(): Boolean {
        return eventsQueue.isEmpty()
    }

    suspend fun waitForEmptyQueue() {
        do {
            if (isQueueEmpty()) {
                break
            }
            processingWaiter.waitForNextProcessing()
        } while (true)
    }

    init {
        coroutineScope.launch(ioDispatcher) {
            handlingEventsLoop()
        }
    }

    private suspend fun handlingEventsLoop() {
        val funcName = "handlingEventsLoop"

        do {
            val event = pollEvent()

            if (event == null) {
                if (logConfig.logLevel.isGreaterThan3()) {
                    logcat { "$funcName; wait for trigger processing" }
                }
                processingTrigger.waitForTriggerProcessing()
                processingWaiter.processingHasStarted()
                continue
            }

            processEvent(event)
        } while (true)
    }

    fun handleEvent(
        event: E
    ) {
        if (logConfig.logLevel.isGreaterThan0()) {
            logcat { "handleEvent: $event" }
        }

        if (event.doNotPushToQueue) {
            if (processingTrigger.isBusy()) {
                if (logConfig.logLevel.isGreaterThan0()) {
                    logcat { "doNotPushToQueue and skipIfBusy are true in event; FSM is Busy; skipping: $event" }
                }
                return
            }
            coroutineScope.launch(ioDispatcher) {
                processEvent(event)
            }
        } else {
            coroutineScope.launch(ioDispatcher) {
                pushEvent(event)
            }
        }
    }

    private suspend fun pushEvent(
        event: E
    ) {
        queueMutex.withLock {
            val posToPush = if (!event.pushToHead) {
                eventsQueue.size
            } else {
                when (event) {
                    is Event.Pause -> {
                        // push right after the latest Pause Event
                        // or at the beginning of the list is there is no Pause Event in queue
                        val lastPausePos = lastPausePos()
                        if (lastPausePos < 0) {
                            0
                        } else {
                            lastPausePos + 1
                        }
                    }
                    is Event.Resume -> {
                        // push right after the latest Resume Event
                        // or right after the latest Pause Event is there is no Resume Events in queue
                        // or at the beginning of the list is there is no Pause Event in queue
                        val lastResumePos = lastResumePos()
                        if (lastResumePos < 0) {
                            val lastPausePos = lastPausePos()
                            if (lastPausePos < 0) {
                                0
                            } else {
                                lastPausePos + 1
                            }
                        } else {
                            lastResumePos + 1
                        }
                    }
                    else -> {
                        0
                    }
                }
            }
            eventsQueue.add(posToPush, event)

            if (logConfig.logLevel.isGreaterThan3()) {
                val sb = StringBuilder()
                sb.appendLine("queue:")
                eventsQueue.map {
                    sb.appendLine("\t$it")
                }
                logcat { sb.toString() }
            }
        }

        kickFSM()
    }

    private fun kickFSM() {
        processingTrigger.triggerProcessing()
    }

    private fun lastPos(predicate: (E) -> Boolean): Int {
        val len = eventsQueue.size
        for (i in (0 until len).reversed()) {
            if (predicate(eventsQueue[i])) {
                return i
            }
        }
        return -1
    }

    private fun lastPausePos() = lastPos { it is Event.Pause }
    private fun lastResumePos() = lastPos { it is Event.Resume }

    private suspend fun processEvent(
        event: E
    ) {
        processingMutex.withLock {
            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "processEvent; event: $event" }
            }

            when (event) {
                is Event.Pause -> {
                    paused = true
                }
                is Event.Resume -> {
                    paused = false
                    kickFSM()
                }
            }

            val eventCheckingResult = eventChecker.check(
                event,
                stateHolder.state.value
            )

            val errorMessage = when (eventCheckingResult) {
                is EventCheckerResult.Pass -> {
                    if (event.setLoadingStateBeforeProcessing) {
                        stateHolder.publishLoadingState()
                    }
                    val eventProcessingResult = eventProcessor.processEvent(event)
                    if (eventProcessingResult !is EventProcessingResult.Processed<*>) {
                        "internal error 1"
                    } else {
                        null
                    }
                }
                is EventCheckerResult.RaiseError -> {
                    eventCheckingResult.message
                }
                is EventCheckerResult.Unchecked -> {
                    "internal error 2"
                }
                is EventCheckerResult.ChangeWith -> {
                    // todo: add implementation
                    assert(false)
                    null
                }
            }

            errorMessage?.let {
                stateHolder.publishErrorState(
                    it
                )
            }
        }
    }
}