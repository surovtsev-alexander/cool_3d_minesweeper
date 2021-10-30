package com.surovtsev.cool3dminesweeper.utils.viewmodel

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