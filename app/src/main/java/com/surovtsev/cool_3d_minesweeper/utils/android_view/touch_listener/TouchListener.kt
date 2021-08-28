package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener

import android.view.MotionEvent
import android.view.View
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.MovingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.TouchHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.receiver.TouchListenerReceiver

class TouchListener(receiver: TouchListenerReceiver): View.OnTouchListener {
    val clickAndRotationHelper = ClickAndRotationHelper(
        receiver.touchReceiver,
        receiver.rotationReceiver,
        receiver.clickEventQueueHandler
    )
    val scalingHelper = ScalingHelper(
        receiver.scaleReceiver,
        receiver.moveReceiver
    )
    val movingHelper = MovingHelper(
        receiver.moveReceiver
    )


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