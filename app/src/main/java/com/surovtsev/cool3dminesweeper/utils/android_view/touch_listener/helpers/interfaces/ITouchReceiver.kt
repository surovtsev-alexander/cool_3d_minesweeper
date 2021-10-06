package com.surovtsev.cool3dminesweeper.utils.android_view.touch_listener.helpers.interfaces

import com.surovtsev.cool3dminesweeper.utils.state_helpers.IHaveUpdatableState
import glm_.vec2.Vec2

interface ITouchReceiver: IReceiver, IHaveUpdatableState {
    fun down(pos: Vec2, movementSaver: IStoreMovement)
    fun up()
}
