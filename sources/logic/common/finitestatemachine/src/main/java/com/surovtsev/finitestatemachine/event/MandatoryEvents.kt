package com.surovtsev.finitestatemachine.event

abstract class MandatoryEvents(
    val init: Event.Init,
    val closeError: Event.CloseError,
)
