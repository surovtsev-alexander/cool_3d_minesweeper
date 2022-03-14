package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat

class FSMQueueHolder(
    private val pausedStateHolder: PausedStateHolder,
    private val processingWaiter: ProcessingWaiter,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val logConfig: LogConfig,
) {
    private val queueMutex = Mutex(locked = false)

    private val eventsQueue = emptyList<Event>().toMutableList()

    private fun isQueueEmpty(): Boolean {
        return eventsQueue.isEmpty()
    }

    suspend fun pollEvent(): Event? {
        return queueMutex.withLock {
            if (eventsQueue.count() == 0) {
                null
            } else {
                if (!pausedStateHolder.paused) {
                    eventsQueue.removeAt(0)
                } else {
                    val first = eventsQueue[0]

                    if (first is com.surovtsev.finitestatemachine.event.Event.Pause || first is com.surovtsev.finitestatemachine.event.Event.Resume) {
                        eventsQueue.removeAt(0)
                    } else {
                        null
                    }
                }
            }
        }
    }

    suspend fun waitForEmptyQueue() {
        do {
            if (isQueueEmpty()) {
                break
            }
            processingWaiter.waitForNextProcessing()
        } while (true)
    }

    suspend fun pushEvent(
        event: Event
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

        fsmProcessingTrigger.kickFSM()
    }

    private fun lastPos(predicate: (Event) -> Boolean): Int {
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

}
