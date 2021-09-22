package com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler.MoveHandler
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
 import glm_.vec2.Vec2
import javax.inject.Inject
import kotlin.math.abs


@GameControllerScope
class ClickAndRotationHelper @Inject constructor(
    private val touchReceiver: TouchReceiver,
    private val moveHandler: MoveHandler,
) : TouchHelper(), IStoreMovement {
    init {
        getAndRelease()
    }

    var gLSurfaceView: GLSurfaceView? = null

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

                touchReceiver.donw(curr, this)
                downed = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (downed) {
                    val delta = curr - prev

                    gLSurfaceView?.queueEvent(object : Runnable {
                        val p = prev
                        val c = curr
                        override fun run() {
                            moveHandler.rotateBetweenProjections(
                                p, c
                            )
                        }
                    })

                    movement += abs(delta[0]) + abs(delta[1])

                    prev = curr
                }
            }
            MotionEvent.ACTION_UP -> {
                if (downed) {
                    touchReceiver.up()
                    downed = false
                }
            }
        }
    }

    override fun release() {
        downed = false
        touchReceiver.release()
    }
}
