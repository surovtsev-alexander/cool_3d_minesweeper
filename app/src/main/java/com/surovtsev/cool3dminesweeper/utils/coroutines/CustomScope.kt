package com.surovtsev.cool3dminesweeper.utils.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class CustomScope(
    val dispatcher: CoroutineDispatcher = Dispatchers.Main
): CoroutineScope {
    private var parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + parentJob

    fun onStart() {
        parentJob = Job()
    }

    fun onStop() {
        parentJob.cancel()
        // The whole scope can be cancelled also
        // with 'cancel(cause: CancellationException).
    }
}