package com.surovtsev.cool_3d_minesweeper.utils.state_helpers

open class Updatable {
    private var updated: Boolean

    constructor() {
        updated = true
    }

    constructor(updated_: Boolean) {
        updated = updated_
    }

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