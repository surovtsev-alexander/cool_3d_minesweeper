package com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.interfaces

import glm_.vec2.Vec2

interface ITouchReceiver: IReceiver {
    fun donw(pos: Vec2, movementStorer: IStoreMovement)
    fun up()
    fun release()
}
