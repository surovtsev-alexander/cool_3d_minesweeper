package com.surovtsev.core.viewmodel

interface ScreenCommandHandler<in T: CommandFromScreen> {
    fun handleCommand(command: T)
}

