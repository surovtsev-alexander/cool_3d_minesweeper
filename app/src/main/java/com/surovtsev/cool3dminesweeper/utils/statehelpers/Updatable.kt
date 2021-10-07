package com.surovtsev.cool3dminesweeper.utils.statehelpers

interface ICanBeReleased {
    fun release()
}

interface IHaveUpdatableState:
    ICanBeReleased
{
    fun isUpdated(): Boolean
}

interface ICanBeUpdated:
    IHaveUpdatableState
{
    fun update()
}

interface ICanBeSmartUpdated:
    ICanBeUpdated
{
    fun getAndRelease(): Boolean {
        val res = isUpdated()
        release()
        return res
    }
}

open class Updatable(
    private var updated: Boolean = true
):
    ICanBeSmartUpdated
{
    override fun release() {
        updated = false
    }

    override fun isUpdated() = updated

    override fun update() {
        updated = true
    }
}