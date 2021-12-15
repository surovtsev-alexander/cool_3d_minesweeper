package com.surovtsev.core.viewmodel

interface ScreenCommandsHandler<T: CommandsFromScreen> {
    fun handleCommand(command: T)
}

