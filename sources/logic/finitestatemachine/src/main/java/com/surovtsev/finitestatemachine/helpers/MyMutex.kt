package com.surovtsev.finitestatemachine.helpers

import kotlinx.coroutines.sync.Mutex


// does not throw IllegalStateException if mutex is not locked
open class MyMutex {
    private val mutex = Mutex(locked = false)

    fun isLocked(): Boolean {
        return mutex.isLocked
    }

    fun safeUnlock() {
        try {
            // if mutex is not locked,
            // calling unlock causes
            // java.lang.IllegalStateException: Mutex is not locked
            mutex.unlock()
        } catch (e: IllegalStateException) {
            // nothing to do.
        }
    }

    suspend fun lock() {
        mutex.lock()
    }
}