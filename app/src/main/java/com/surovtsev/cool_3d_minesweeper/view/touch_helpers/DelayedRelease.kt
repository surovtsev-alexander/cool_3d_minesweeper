package com.surovtsev.cool_3d_minesweeper.view.touch_helpers

class DelayedRelease {
    private var needToBeReleased = false

    fun release() {
        needToBeReleased = true
    }

    fun getAndFlush(): Boolean {
        val res = needToBeReleased
        needToBeReleased = false
        return res
    }
}