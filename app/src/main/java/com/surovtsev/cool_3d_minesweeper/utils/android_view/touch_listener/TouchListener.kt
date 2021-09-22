package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.MovingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.TouchHelper
import javax.inject.Inject

@GameControllerScope
class TouchListener @Inject constructor(
    touchReceiver: TouchReceiver,
    moveHandler: MoveHandler
): View.OnTouchListener {
    private val clickAndRotationHelper = ClickAndRotationHelper(
        touchReceiver,
        moveHandler
    )
    private val scalingHelper = ScalingHelper(
        moveHandler
    )
    private val movingHelper = MovingHelper(
        moveHandler
    )

    fun connectToGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        clickAndRotationHelper.gLSurfaceView = gLSurfaceView
        gLSurfaceView.setOnTouchListener(this)
    }


    private var prevPointerCount = 0
    var currTouchHelper: TouchHelper = clickAndRotationHelper

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        val pointerCount = event.pointerCount

        val changeHelper = pointerCount != prevPointerCount
        if (changeHelper) {
            currTouchHelper.release()

            prevPointerCount = pointerCount

            when (pointerCount) {
                1 -> {
                    currTouchHelper = clickAndRotationHelper
                }
                2 -> {
                    currTouchHelper = scalingHelper
                }
                else -> {
                    currTouchHelper = movingHelper
                }
            }

            currTouchHelper.update()
        }

        currTouchHelper.onTouch(event)

        return true
    }
}