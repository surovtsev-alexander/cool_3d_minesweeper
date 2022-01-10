package com.surovtsev.core.viewmodel

interface ScreenCommandHandler<in T: EventToViewModel> {
    fun handleCommand(command: T)
}

