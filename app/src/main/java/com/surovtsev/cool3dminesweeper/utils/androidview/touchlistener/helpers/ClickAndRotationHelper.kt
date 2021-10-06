package com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.move.MoveHandler
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces.IStoreMovement
import glm_.vec2.Vec2
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.abs

@GameScope
class ClickAndRotationHelper @Inject constructor(
    private val touchReceiver: TouchReceiver,
    private val moveHandler: MoveHandler,
    @Named(Prev)
    private var prev: Vec2,
    @Named(Movement)
    private var movement: Float,
    @Named(Downed)
    private var downed: Boolean,
) : TouchHelper(), IStoreMovement {
    init {
        getAndRelease()
    }

    companion object {
        const val Prev = "prev"
        const val Movement = "movement"
        const val Downed = "downed"
    }

    /* SEE: TouchListener::connectToGLSurfaceView */
    var gLSurfaceView: GLSurfaceView? = null

    override fun getMovement(): Float = movement

    override fun onTouch(event: MotionEvent) {
        val curr = getVec(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                prev = getVec(event)
                movement = 0f

                touchReceiver.down(curr, this)
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
