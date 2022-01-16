package com.surovtsev.finitestatemachine.helpers.auxiliary

interface ProcessingTrigger {
    fun isBusy(): Boolean
    fun triggerProcessing()
    suspend fun waitForTriggerProcessing()
}

class ProcessingTriggerImp: ProcessingTrigger, MyMutex() {
    override fun isBusy(): Boolean = !isLocked()

    override fun triggerProcessing() = safeUnlock()

    override suspend fun waitForTriggerProcessing() = lock()
}