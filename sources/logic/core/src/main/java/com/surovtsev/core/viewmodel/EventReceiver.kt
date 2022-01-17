package com.surovtsev.core.viewmodel

interface EventReceiver<in T: EventToViewModel> {
    fun receiveEvent(event: T)
}

