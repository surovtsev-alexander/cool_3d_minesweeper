package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentLinkedQueue

interface QueueHolder<E: Event> {
    fun isQueueEmpty(): Boolean

    fun pushEvent(
        event: E
    )

    suspend fun waitForEmptyQueue()
}

class QueueHolderImp<E: Event>(
    private val coroutineScope: CoroutineScope,
    private val logConfig: LogConfig,
    private val processingWaiter: ProcessingWaiter,
    private val processingTrigger: ProcessingTrigger,
): QueueHolder<E> {
    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    private val eventsQueue = ConcurrentLinkedQueue<E>()

    private fun pollEvent(): E? {
        return eventsQueue.poll()
    }

    override fun isQueueEmpty(): Boolean {
        return eventsQueue.isEmpty()
    }

    override suspend fun waitForEmptyQueue() {
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


            if (logConfig.logLevel.isGreaterThan2()) {
                logcat { "$funcName; event: $event" }
            }

            delay(1000)

        } while (true)
    }

    override fun pushEvent(
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
        processingTrigger.triggerProcessing()
    }
}