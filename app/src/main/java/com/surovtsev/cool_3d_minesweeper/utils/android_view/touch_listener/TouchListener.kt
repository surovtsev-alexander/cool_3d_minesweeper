package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.MovingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.TouchHelper
import javax.inject.Inject
import javax.inject.Named

@GameScope
class TouchListener @Inject constructor(
    private val clickAndRotationHelper: ClickAndRotationHelper,
    private val scalingHelper: ScalingHelper,
    private val movingHelper: MovingHelper,
    @Named(PrevPointerCount)
    private var prevPointerCount: Int,
    private var currTouchHelper: TouchHelper
): View.OnTouchListener {

    companion object {
        const val PrevPointerCount = "prevCounterCount"
    }

    fun connectToGLSurfaceView(
        gLSurfaceView: GLSurfaceView
    ) {
        clickAndRotationHelper.gLSurfaceView = gLSurfaceView
        gLSurfaceView.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        val pointerCount = event.pointerCount

        val changeHelper = pointerCount != prevPointerCount
        if (changeHelper) {
            currTouchHelper.release()

            prevPointerCount = pointerCount

            currTouchHelper = when (pointerCount) {
                1 -> {
                    clickAndRotationHelper
                }
                2 -> {
                    scalingHelper
                }
                else -> {
                    movingHelper
                }
            }

            currTouchHelper.update()
        }

        currTouchHelper.onTouch(event)

        return true
    }
}