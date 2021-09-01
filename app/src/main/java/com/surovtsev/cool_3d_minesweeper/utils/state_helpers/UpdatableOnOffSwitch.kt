package com.surovtsev.cool_3d_minesweeper.utils.state_helpers

class UpdatableOnOffSwitch(
    private val updatable: Updatable = Updatable(false)
):
    Switch(),
    ICanBeSmartUpdated by updatable {

    override fun turnOn() {
        super.turnOn()
        updatable.update()
    }

    override fun turnOff() {
        super.turnOff()
        updatable.update()
    }
}