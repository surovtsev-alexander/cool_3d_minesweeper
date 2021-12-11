package com.surovtsev.touchlistener

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import com.surovtsev.touchlistener.dagger.TouchListenerScope
import com.surovtsev.touchlistener.helpers.ClickAndRotationHelper
import com.surovtsev.touchlistener.helpers.MovingHelper
import com.surovtsev.touchlistener.helpers.ScalingHelper
import com.surovtsev.touchlistener.helpers.TouchHelper
import javax.inject.Inject
import javax.inject.Named

@TouchListenerScope
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