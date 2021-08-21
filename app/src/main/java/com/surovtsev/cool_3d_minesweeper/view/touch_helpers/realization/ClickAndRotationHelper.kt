package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2
import kotlin.math.abs


class ClickAndRotationHelper(
    val touchReceiver: IReceiverCalculator<ITouchReceiver>,
    val rotationReceiver: IReceiverCalculator<IRotationReceiver>,
    val clickEventQueueHandler: GLSurfaceView
) : TouchHelper(), IStoreMovement {
    init {
        getAndRelease()
    }

    private var prev = Vec2()
    private var movement = 0f

    override fun getMovement(): Float = movement

    override fun onTouch(event: MotionEvent) {
        val curr = getVec(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prev = getVec(event)
                movement = 0f

                touchReceiver.getReceiver()?.donw(curr, this)
            }
            MotionEvent.ACTION_MOVE -> {
                val delta = curr - prev

                clickEventQueueHandler.queueEvent(object: Runnable {
                    val p = prev
                    val c = curr
                    override fun run() {
                        rotationReceiver.getReceiver()?.rotateBetweenProjections(
                            p, c
                        )
                    }
                })

                movement = abs(delta[0]) + abs(delta[1])

                prev = curr
            }
            MotionEvent.ACTION_UP -> {
                touchReceiver.getReceiver()?.up()
            }
        }
    }

    override fun tryToRelease() {
        getAndRelease()
        touchReceiver.getReceiver()?.release()
    }
}
