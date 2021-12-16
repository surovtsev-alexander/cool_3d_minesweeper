package com.surovtsev.core.viewmodel

interface ScreenCommandHandler<T: CommandFromScreen> {
    fun handleCommand(command: T)
}

