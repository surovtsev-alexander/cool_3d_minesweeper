package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers

import glm_.vec2.Vec2

interface MoveReceiver: TouchListenerReceiver {
    fun move(proj1: Vec2, proj2: Vec2)
}
