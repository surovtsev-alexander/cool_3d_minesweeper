package com.surovtsev.core.utils.statehelpers

interface Releasable {
    fun release()
}

interface UpdatedStateHolder:
    Releasable
{
    fun isUpdated(): Boolean
}

interface Updatable:
    UpdatedStateHolder
{
    fun update()
}

interface SmartUpdatable:
    Updatable
{
    fun getAndRelease(): Boolean {
        val res = isUpdated()
        release()
        return res
    }
}

open class UpdatableImp(
    private var updated: Boolean = true
):
    SmartUpdatable
{
    override fun release() {
        updated = false
    }

    override fun isUpdated() = updated

    override fun update() {
        updated = true
    }
}
