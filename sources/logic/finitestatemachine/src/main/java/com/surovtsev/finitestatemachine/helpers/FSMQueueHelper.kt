package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.config.LogLevel
import com.surovtsev.finitestatemachine.event.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import logcat.logcat
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentLinkedQueue

open class FSMQueueHelper<E: Event>(
    private val coroutineScope: CoroutineScope,
    val logConfig: LogConfig,
) {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val triggerProcessingMutex = Mutex(locked = false)

    private val eventsQueue = ConcurrentLinkedQueue<Event>()

    init {
        coroutineScope.launch(ioDispatcher) {
            handlingEventsLoop()
        }
    }

    private suspend fun handlingEventsLoop() {
        do {
            val event = eventsQueue.poll()

            if (event == null) {
                triggerProcessingMutex.lock()
                continue
            }

            delay(1000)

        } while (true)
    }

    protected fun isFSMBusy(): Boolean {
        return !triggerProcessingMutex.isLocked
    }

    protected fun pushEvent(
        event: E
    ) {
        eventsQueue.add(event)

        if (logConfig.logLevel.isGreaterThan3()) {
            val sb = StringBuilder()
            sb.appendLine("queue:")
            eventsQueue.map {
                sb.appendLine("\t$it")
            }
            logcat { sb.toString() }
        }

        kickFSM()
    }

    private fun kickFSM() {
        triggerProcessingMutex.unlock()
    }
}