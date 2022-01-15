package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.helpers.concrete.*
import com.surovtsev.finitestatemachine.state.data.Data
import kotlinx.coroutines.CoroutineScope

open class FSMCoreHelper<E: Event, D: Data>(
    coroutineScope: CoroutineScope,
    private val initialState: FSMState<D>,
    private val logConfig: LogConfig,

    private val processingTrigger: ProcessingTrigger = ProcessingTriggerImp(),
    private val processingWaiter: ProcessingWaiter = ProcessingWaiterImp(),
    private val fsmStateHelper: FSMStateHelper<D> = FSMStateHelperImp(initialState),
    private val queueHolder: QueueHolder<E> = QueueHolderImp(
        coroutineScope,
        logConfig,
        processingWaiter,
        processingTrigger,
    ),
):
    ProcessingTrigger by processingTrigger,
    ProcessingWaiter by processingWaiter,
    FSMStateHelper<D> by fsmStateHelper,
    QueueHolder<E> by queueHolder
