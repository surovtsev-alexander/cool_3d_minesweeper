package com.surovtsev.utils.statehelpers

interface IsOn {
    fun isOn(): Boolean
}

interface OnOffSwitch: IsOn {
    fun turnOn()
    fun turnOff()

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
