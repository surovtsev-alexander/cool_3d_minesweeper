package com.surovtsev.cool3dminesweeper.utils.android_view.touch_listener.helpers.interfaces

import glm_.vec2.Vec2

interface IMoveReceiver: IReceiver {
    fun move(proj1: Vec2, proj2: Vec2)
}
