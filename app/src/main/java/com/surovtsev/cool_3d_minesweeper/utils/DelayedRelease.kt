package com.surovtsev.cool_3d_minesweeper.utils

open class DelayedRelease {
    private var updated = true

    open fun tryToRelease() {
        update()
    }

    fun update() {
        updated = true
    }

    fun getAndRelease(): Boolean {
        val res = updated
        updated = false
        return res
    }
}