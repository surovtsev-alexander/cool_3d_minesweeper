package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.handlers

import com.surovtsev.utils.androidview.interaction.TouchType
import glm_.vec2.Vec2

interface TouchHandler {
    fun handleTouch(
        point: Vec2,
        touchType: TouchType
    )
}
