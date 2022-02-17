package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

typealias EventProcessor<E> = suspend () -> EventProcessingResult<E>
