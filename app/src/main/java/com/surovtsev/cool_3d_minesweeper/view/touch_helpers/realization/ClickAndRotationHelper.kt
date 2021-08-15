package com.surovtsev.cool_3d_minesweeper.view.touch_helpers.realization

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.logic.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.utils.LoggerConfig
import com.surovtsev.cool_3d_minesweeper.utils.Timer
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
    init {
        getAndRelease()
    }

    var prev = Vec2()
    val mTimer = Timer()
    val mMaxClickTimeMs = 100L
    var mMovingDistance = 0f
    val mMovindThreshold = 10

    override fun onTouch(event: MotionEvent) {
        do {
            val released = getAndRelease()

            if (event.action == MotionEvent.ACTION_DOWN || released) {
                prev = getVec(event)
                if (released) {
                    mTimer.push_hour_before()
                } else {
                    mTimer.push()
                }
                mMovingDistance = 0f

                break
            }

            val curr = getVec(event)

            if (event.action == MotionEvent.ACTION_MOVE) {
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
                    clickReceiver.getReceiver()?.handleClick(curr)
                }

                break
            }
        } while (false)
    }
}
