package com.surovtsev.cool3dminesweeper.utils.statehelpers

interface Switch {
    fun turnOn()
    fun turnOff()

    fun isOn(): Boolean
}

open class SwitchImp(
    private var on: Boolean = false
): Switch {

    override fun turnOn() {
        on = true
    }

    override fun turnOff() {
        on = false
    }

    override fun isOn() = on
}
