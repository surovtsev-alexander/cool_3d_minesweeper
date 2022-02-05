package com.surovtsev.finitestatemachine.helpers

class PausedStateHolder {
    var paused: Boolean = false
        private set

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }
}
