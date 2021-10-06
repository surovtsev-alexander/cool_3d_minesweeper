package com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces

import com.surovtsev.cool3dminesweeper.utils.statehelpers.IHaveUpdatableState
import glm_.vec2.Vec2

interface ITouchReceiver: IReceiver, IHaveUpdatableState {
    fun down(pos: Vec2, movementSaver: IStoreMovement)
    fun up()
}
