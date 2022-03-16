package com.surovtsev.utils.statehelpers

class UpdatableOnOffSwitch(
    private val updatable: UpdatableImp = UpdatableImp(false)
):
    OnOffSwitchImp(),
    SmartUpdatable by updatable {

    override fun turnOn() {
        super.turnOn()
        updatable.update()
    }

    override fun turnOff() {
        super.turnOff()
        updatable.update()
    }
}