package com.surovtsev.finitestatemachine.utils

interface FsmProcessingTrigger {
    fun isBusy(): Boolean
    fun triggerProcessing()
    suspend fun waitForTriggerProcessing()

    fun kickFSM() = triggerProcessing()
}

class FsmProcessingTriggerImp: FsmProcessingTrigger, MyMutex() {
    override fun isBusy(): Boolean = !isLocked()

    override fun triggerProcessing() = safeUnlock()

    override suspend fun waitForTriggerProcessing() = lock()
}