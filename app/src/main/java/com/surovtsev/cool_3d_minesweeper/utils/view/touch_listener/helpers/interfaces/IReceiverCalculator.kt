package com.surovtsev.cool_3d_minesweeper.utils.view.touch_listener.helpers.interfaces

interface IReceiverCalculator<R: IReceiver> {
    fun getReceiver(): R?
}
