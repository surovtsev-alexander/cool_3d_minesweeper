package com.surovtsev.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import logcat.logcat

interface ViewModelCoroutineScopeHelper {
    val exceptionHandler: CoroutineExceptionHandler

    companion object {
        val uiDispatcher = Dispatchers.Main
        val ioDispatcher = Dispatchers.IO
    }

    fun ViewModel.launchWithExceptionHandler(
        coroutineDispatcher: CoroutineDispatcher,
        coroutineExceptionHandler: CoroutineExceptionHandler = exceptionHandler,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(
            coroutineDispatcher + coroutineExceptionHandler,
            start,
            block
        )
    }

    fun ViewModel.launchOnIOThread(
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(
            ioDispatcher,
            block = block
        )
    }

    suspend fun <T> ViewModel.withUIContext(
        block: suspend CoroutineScope.() -> T
    ): T {
        return withContext(
            uiDispatcher,
            block = block)
    }
}

class ViewModelCoroutineScopeHelperImpl(
    exceptionHandle: CoroutineExceptionHandler = defaultExceptionHandler
): ViewModelCoroutineScopeHelper {
    companion object {
        val defaultExceptionHandler = CoroutineExceptionHandler { _, exception ->
            val exceptionMessage = exception.message
            val logMessage = "Error in viewModelCoroutineScope: ${exceptionMessage}"
            logcat { logMessage }
        }
    }

    override val exceptionHandler: CoroutineExceptionHandler = exceptionHandle
}