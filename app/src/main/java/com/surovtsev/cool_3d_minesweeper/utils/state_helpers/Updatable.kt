package com.surovtsev.cool_3d_minesweeper.utils.state_helpers

interface ICanBeReleased {
    fun release()
}


interface IHaveUpdatableState:
    ICanBeReleased
{
    fun getState(): Boolean
    fun getAndRelease(): Boolean {
        val res = getState()
        release()
        return res
    }
}

interface ICanBeUpdated:
    IHaveUpdatableState
{
    fun update()
}


open class Updatable(
    private var updated: Boolean = true
):
    ICanBeUpdated
{
    override fun release() {
        updated = false
    }

    override fun getState() = updated

    override fun update() {
        updated = true
    }
}
