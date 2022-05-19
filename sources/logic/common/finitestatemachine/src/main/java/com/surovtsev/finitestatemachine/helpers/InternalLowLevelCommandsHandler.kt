package com.surovtsev.finitestatemachine.helpers

import com.surovtsev.finitestatemachine.config.LogConfig
import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import logcat.logcat

class InternalLowLevelCommandsHandler(
    private val restartableCoroutineScopeEntryPoint: RestartableCoroutineScopeEntryPoint,
    private val queueHolder: FSMQueueHolder,
    private val stateHolder: StateHolder,
    private val pausedStateHolder: PausedStateHolder,
    private val fsmProcessingTrigger: FsmProcessingTrigger,
    private val logConfig: LogConfig,
) {
    fun restart(
        startingEvent: Event,
    ) {
        if (logConfig.logLevel.isGreaterThan1()) {
            logcat { "restarting fsm" }
        }
        restartableCoroutineScopeEntryPoint
            .subscriberImp
            .restart {
                pause()

                queueHolder.emptyQueue()

                stateHolder.pushInitialState()

                queueHolder.pushEvent(
                    startingEvent
                )

                resume()
            }
    }

    fun stop() {
        logcat { "stopping fsm" }
        restartableCoroutineScopeEntryPoint
            .subscriberImp
            .stop()
    }

    fun pause() {
        pausedStateHolder.pause()
    }

    fun resume() {
        pausedStateHolder.resume()

        fsmProcessingTrigger.kickFSM()
    }
}