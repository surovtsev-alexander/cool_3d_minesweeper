package com.surovtsev.cool_3d_minesweeper.view.touch_helpers

interface IReceiverCalculator<R: IReceiver> {
    fun getReceiver(): R?
}
