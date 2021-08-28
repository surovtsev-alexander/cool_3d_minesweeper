package com.surovtsev.cool_3d_minesweeper.views.activities.multitouchTest

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import glm_.vec2.Vec2ui

class MultitouchView(context: Context?, attrs: AttributeSet?): View(context, attrs) {
    var pointer1 = Vec2ui()
    var pointer2 = Vec2ui()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}