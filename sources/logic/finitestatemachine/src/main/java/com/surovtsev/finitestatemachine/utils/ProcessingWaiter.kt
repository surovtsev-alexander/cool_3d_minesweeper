package com.surovtsev.finitestatemachine.utils

interface ProcessingWaiter {
    fun processingHasStarted()
    suspend fun waitForNextProcessing()
}

class ProcessingWaiterImp: ProcessingWaiter, MyMutex() {
    override fun processingHasStarted() = safeUnlock()
    override suspend fun waitForNextProcessing() = lock()
}
