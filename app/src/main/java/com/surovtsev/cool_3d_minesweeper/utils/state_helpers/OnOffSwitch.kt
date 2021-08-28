package com.surovtsev.cool_3d_minesweeper.utils.state_helpers

interface ISwitch {
    fun isOn(): Boolean

    fun turnOn()
    fun turnOff()
}

class Switch: ISwitch {
    var on = false
        private set

    override fun isOn() = on

    override fun turnOn() {
        on = true
    }

    override fun turnOff() {
        on = false
    }
}