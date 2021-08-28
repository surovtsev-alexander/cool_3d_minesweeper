package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces

import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.IHaveUpdatableState
import glm_.vec2.Vec2

interface ITouchReceiver: IReceiver, IHaveUpdatableState {
    fun donw(pos: Vec2, movementStorer: IStoreMovement)
    fun up()
}
