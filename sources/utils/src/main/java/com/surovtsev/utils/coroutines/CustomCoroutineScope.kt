package com.surovtsev.utils.coroutines

import com.surovtsev.utils.statehelpers.SwitchImp
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomCoroutineScope(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): CoroutineScope, SwitchImp(true) {
    private var parentJob: CompletableJob = Job()


    override val coroutineContext: CoroutineContext
        get() = dispatcher + parentJob


    override fun turnOn() {
        super.turnOn()

        parentJob = Job()
    }

    override fun turnOff() {
        super.turnOff()
        // The whole scope can be cancelled also
        // with 'cancel(cause: CancellationException).
    }

    fun restart() {
        if (isOn()) {
            turnOff()
        }
        turnOn()
    }
}