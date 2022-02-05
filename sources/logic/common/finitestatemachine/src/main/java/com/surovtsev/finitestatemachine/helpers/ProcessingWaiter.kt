package com.surovtsev.finitestatemachine.helpers

interface ProcessingWaiter {
    fun processingHasStarted()
    suspend fun waitForNextProcessing()
}

class ProcessingWaiterImp: ProcessingWaiter, MyMutex() {
    override fun processingHasStarted() = safeUnlock()
    override suspend fun waitForNextProcessing() = lock()
}
