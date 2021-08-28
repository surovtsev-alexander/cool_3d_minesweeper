package com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.interfaces

interface IReceiverCalculator<R: IReceiver> {
    fun getReceiver(): R?
}
