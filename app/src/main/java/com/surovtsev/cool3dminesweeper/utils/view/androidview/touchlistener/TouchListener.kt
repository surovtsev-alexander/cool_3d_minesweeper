package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.ClickAndRotationHelper
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.MovingHelper
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.ScalingHelper
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.TouchHelper
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