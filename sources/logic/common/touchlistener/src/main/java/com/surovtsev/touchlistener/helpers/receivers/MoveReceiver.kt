package com.surovtsev.touchlistener.helpers.receivers

import glm_.vec2.Vec2

interface MoveReceiver: TouchListenerReceiver {
    fun move(prev: Vec2, curr: Vec2)
}
