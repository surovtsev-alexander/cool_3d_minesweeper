package com.surovtsev.cool3dminesweeper.utils.statehelpers

interface ISwitch {
    fun turnOn()
    fun turnOff()

    fun isOn(): Boolean
}

open class Switch(
    private var on: Boolean = false
): ISwitch {

    override fun turnOn() {
        on = true
    }

    override fun turnOff() {
        on = false
    }

    override fun isOn() = on
}