package com.surovtsev.touchlistener.helpers.receivers

import glm_.vec2.Vec2

interface RotationReceiver:
    TouchListenerReceiver {
    fun rotate(prev: Vec2, curr: Vec2)
}