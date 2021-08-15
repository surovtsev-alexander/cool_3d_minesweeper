package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces

import glm_.vec2.Vec2

interface IMoveReceiver: IReceiver {
    fun move(proj1: Vec2, proj2: Vec2)
}
