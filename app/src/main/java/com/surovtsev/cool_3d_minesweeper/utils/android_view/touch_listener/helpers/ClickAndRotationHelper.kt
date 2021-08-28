package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IRotationReceiver
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.ITouchReceiver
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

    private var downed = false

    override fun getMovement(): Float = movement

    override fun onTouch(event: MotionEvent) {
        val curr = getVec(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prev = getVec(event)
                movement = 0f

                touchReceiver.getReceiver()?.donw(curr, this)
                downed = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (downed) {
                    val delta = curr - prev

                    clickEventQueueHandler.queueEvent(object : Runnable {
                        val p = prev
                        val c = curr
                        override fun run() {
                            rotationReceiver.getReceiver()?.rotateBetweenProjections(
                                p, c
                            )
                        }
                    })

                    movement += abs(delta.x) + abs(delta.y)

                    prev = curr
                }
            }
            MotionEvent.ACTION_UP -> {
                if (downed) {
                    touchReceiver.getReceiver()?.up()
                    downed = false
                }
            }
        }
    }

    override fun release() {
        downed = false
        touchReceiver.getReceiver()?.release()
    }
}
