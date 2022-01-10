package com.surovtsev.finitestatemachine.event

interface Event {
    val skipIfFSMIsBusy: Boolean


    interface Init: Event
    interface CloseError: Event
}