package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers

import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.holders.MovementHolder
import com.surovtsev.utils.statehelpers.Releasable
import glm_.vec2.Vec2

interface TouchReceiver: TouchListenerReceiver, Releasable {
    fun down(pos: Vec2, movementHolderSaver: MovementHolder)
    fun up()
}
