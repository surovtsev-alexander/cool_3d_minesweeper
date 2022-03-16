package com.surovtsev.utils.statehelpers

interface OnOffSwitch {
    fun turnOn()
    fun turnOff()

    fun isOn(): Boolean
}

open class OnOffSwitchImp(
    private var on: Boolean = false
): OnOffSwitch {

    override fun turnOn() {
        on = true
    }

    override fun turnOff() {
        on = false
    }

    override fun isOn() = on
}
