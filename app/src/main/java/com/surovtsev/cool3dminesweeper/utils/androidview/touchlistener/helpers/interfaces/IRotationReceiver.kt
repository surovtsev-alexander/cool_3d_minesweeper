package com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces

import glm_.vec2.Vec2

interface IRotationReceiver:
    IReceiver {
    fun rotateBetweenProjections(prev: Vec2, curr: Vec2)
}