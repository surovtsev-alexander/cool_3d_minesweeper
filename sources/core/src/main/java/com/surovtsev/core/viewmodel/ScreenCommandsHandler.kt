package com.surovtsev.core.viewmodel

interface ScreenCommandsHandler<T> {
    fun handleCommand(command: T)
}

