package com.surovtsev.finitestatemachine.helpers.concrete

import com.surovtsev.finitestatemachine.helpers.concrete.auxiliary.MyMutex

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