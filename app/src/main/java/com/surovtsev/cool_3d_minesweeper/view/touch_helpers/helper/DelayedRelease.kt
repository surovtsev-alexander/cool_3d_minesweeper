package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.helper

open class DelayedRelease {
    private var updated = true

    fun update() {
        updated = true
    }

    fun getAndRelease(): Boolean {
        val res = updated
        updated = false
        return res
    }
}