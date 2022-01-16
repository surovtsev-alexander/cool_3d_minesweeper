package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventprocessor.EventProcessor
import com.surovtsev.finitestatemachine.helpers.concrete.*
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.CoroutineScope

open class FSMCoreHelper<E: Event, D: Data>(
    eventChecker: EventChecker<E, D>,
    eventProcessor: EventProcessor<E>,
    coroutineScope: CoroutineScope,
    publishStateInUIThread: Boolean,
    initialState: State<D>,
    logConfig: LogConfig,

    private val processingTrigger: ProcessingTrigger = ProcessingTriggerImp(),
    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp(),
    private val stateHolder: StateHolder<D> = StateHolderImp(
        initialState,
        publishStateInUIThread
    ),
    private val queueHolder: QueueHolder<E> = QueueHolderImp(
        eventChecker,
        eventProcessor,
        stateHolder,
        coroutineScope,
        logConfig,
        processingWaiter,
        processingTrigger,
    ),
):
    ProcessingTrigger by processingTrigger,
    ProcessingWaiter by processingWaiter,
    StateHolder<D> by stateHolder,
    QueueHolder<E> by queueHolder
