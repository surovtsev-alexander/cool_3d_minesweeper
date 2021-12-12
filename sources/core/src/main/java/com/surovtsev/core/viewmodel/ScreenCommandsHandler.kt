package com.surovtsev.core.viewmodel

interface ScreenCommandsHandler<T> {
    fun handleEvent(event: T)
}

