package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.util.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.util.Timer
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IClickReceiver
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IReceiverCalculator
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IRotationReceiver
import glm_.vec2.Vec2
import kotlin.math.abs


class ClickAndRotationHelper(
    val clickReceiver: IReceiverCalculator<IClickReceiver>,
    val rotationReceiver: IReceiverCalculator<IRotationReceiver>,
    val clickEventQueueHandler: GLSurfaceView
) : TouchHelper() {

    var prev = Vec2()
    val mTimer = Timer()
    val mMaxClickTimeMs = 100L
    var mMovingDistance = 0f
    val mMovindThreshold = 10

    override fun onTouch(event: MotionEvent) {
        do {
            val needToBeInited = event.action == MotionEvent.ACTION_DOWN || getAndFlush()
            if (needToBeInited) {
                prev = Vec2(event.x, event.y)
                mTimer.push()
                mMovingDistance = 0f

                break
            }

            val moved = event.action == MotionEvent.ACTION_MOVE
            if (moved) {
                val curr = Vec2(event.x, event.y)

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

                mMovingDistance = abs(delta[0]) + abs(delta[1])

                prev = curr

                break
            }

            val up = event.action == MotionEvent.ACTION_UP
            if (up) {
                mTimer.push()
                val moved = mMovingDistance >= mMovindThreshold ||  mTimer.diff() >= mMaxClickTimeMs
                if (!moved) {
                    if (LoggerConfig.LOG_GAME_ACTIVITY_ACTIONS) {
                        ApplicationController.instance!!.messagesComponent!!.addMessageUI("clicked")
                    }
                    val curr = Vec2(event.x, event.y)
                    clickReceiver.getReceiver()?.handleClick(curr)
                }

                break
            }
        } while (false)
    }
}
