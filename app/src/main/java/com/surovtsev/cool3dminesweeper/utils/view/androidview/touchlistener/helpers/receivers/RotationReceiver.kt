package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers

import glm_.vec2.Vec2

interface RotationReceiver:
    TouchListenerReceiver {
    fun rotateBetweenProjections(prev: Vec2, curr: Vec2)
}