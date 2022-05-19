package com.surovtsev.finitestatemachine.helpers


class FsmProcessingTrigger: MyMutex() {
    fun isBusy(): Boolean = !isLocked()

    private fun triggerProcessing() = safeUnlock()

    suspend fun waitForTriggerProcessing() = lock()

    fun kickFSM() = triggerProcessing()
}
