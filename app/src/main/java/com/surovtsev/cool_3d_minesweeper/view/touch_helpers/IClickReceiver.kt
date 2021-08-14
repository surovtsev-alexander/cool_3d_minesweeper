package com.surovtsev.cool_3d_minesweeper.view.touch_helpers

import glm_.vec2.Vec2

interface IClickReceiver: IReceiver {
    fun handleClick(point: Vec2)
}