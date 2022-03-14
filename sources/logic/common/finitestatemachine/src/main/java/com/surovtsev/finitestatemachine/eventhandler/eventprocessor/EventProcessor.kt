package com.surovtsev.finitestatemachine.eventhandler.eventprocessor

typealias EventProcessor = suspend () -> EventProcessingResult
