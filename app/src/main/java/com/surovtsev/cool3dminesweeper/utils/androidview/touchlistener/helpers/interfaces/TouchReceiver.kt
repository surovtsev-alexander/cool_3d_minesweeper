package com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces

import com.surovtsev.cool3dminesweeper.utils.statehelpers.UpdatedStateHolder
import glm_.vec2.Vec2

interface TouchReceiver: TouchListenerReceiver, UpdatedStateHolder {
    fun down(pos: Vec2, movementHolderSaver: MovementHolder)
    fun up()
}
