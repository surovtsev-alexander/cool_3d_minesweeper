package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.helper

open class DelayedRelease {
    private var needToBeReleased = true

    fun release() {
        needToBeReleased = true
    }

    fun getAndFlush(): Boolean {
        val res = needToBeReleased
        needToBeReleased = false
        return res
    }
}