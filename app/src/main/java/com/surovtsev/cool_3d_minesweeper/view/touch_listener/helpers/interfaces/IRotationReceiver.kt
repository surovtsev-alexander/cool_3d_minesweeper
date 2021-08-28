package com.surovtsev.cool_3d_minesweeper.view.touch_listener.helpers.interfaces

import glm_.vec2.Vec2

interface IRotationReceiver:
    IReceiver {
    fun rotateBetweenProjections(prev: Vec2, curr: Vec2)
}