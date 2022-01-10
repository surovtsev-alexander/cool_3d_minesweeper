package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.helpers.concrete.auxiliary.MyMutex
import kotlinx.coroutines.sync.Mutex

interface ProcessingWaiter {
    fun processingHasStarted()
    suspend fun waitForNextProcessing()
}

class ProcessingWaiterImp: ProcessingWaiter, MyMutex() {
    override fun processingHasStarted() = safeUnlock()
    override suspend fun waitForNextProcessing() = lock()
}