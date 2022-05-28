package com.surovtsev.utils.coroutines.customcoroutinescope

import com.surovtsev.utils.statehelpers.OnOffSwitchImp
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

typealias BeforeStartAction = suspend () -> Unit

class RestartableCoroutineScope(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): CoroutineScope, OnOffSwitchImp(true) {

    private var parentJob: CompletableJob = Job()


    override val coroutineContext: CoroutineContext
        get() = dispatcher + parentJob


    override fun turnOn() {
        super.turnOn()

        parentJob = Job()
    }

    override fun turnOff() {
        super.turnOff()
        parentJob.cancel()
        // The whole scope can be cancelled also
        // with 'cancel(cause: CancellationException).
    }

    fun restart(
        beforeStartAction: BeforeStartAction?
    ) {
        if (isOn()) {
            turnOff()
        }
        runBlocking {
            beforeStartAction?.invoke()
        }
        turnOn()
    }
}
