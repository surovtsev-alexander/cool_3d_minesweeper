package com.surovtsev.core.viewmodel

interface EventHandler<in T: EventToViewModel> {
    fun handleEvent(event: T)
}

