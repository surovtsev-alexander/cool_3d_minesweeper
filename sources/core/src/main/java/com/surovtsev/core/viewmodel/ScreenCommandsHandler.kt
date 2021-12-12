package com.surovtsev.core.viewmodel

interface ScreenCommandsHandler<T> {
    fun handleCommand(event: T)
}

