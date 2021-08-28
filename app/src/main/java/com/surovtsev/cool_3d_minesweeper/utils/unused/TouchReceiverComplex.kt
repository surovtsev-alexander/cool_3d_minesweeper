package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers

import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2

class TouchReceiverComplex(private val customClock: CustomClock): ITouchReceiver {

    enum class TouchType {
        SHORT,
        LONG,
        DOUBLE
    }

    enum class State {
        IDLE,
        DELAY_BEFORE_LONG_CLICK,
        DELAY_BEFORE_DOUBLE_CLICK,
        WAIT_FOR_RELEASE
    }

    var state = State.IDLE
        private set

    var clickType = TouchType.SHORT
        private set

    var clickPos = Vec2()
        private set
    private var downTime = 0L
    private var clickTime = 0L

    var movementStorer: IStoreMovement? = null

    val movementThreshold = 10f
    val clickDelay = 100L
    val doubleClickDelay = 250L
    val longClickDelay = 300L

    override fun donw(pos: Vec2, movementStorer_: IStoreMovement) {
        if (state == State.DELAY_BEFORE_DOUBLE_CLICK) {
            state = State.WAIT_FOR_RELEASE
            clickType = TouchType.DOUBLE
        } else {
            clickPos = pos
            downTime = customClock.time

            movementStorer = movementStorer_

            state = State.DELAY_BEFORE_LONG_CLICK
        }
    }

    override fun up() {
        releaseIfMovedOrPerform {
            do {
                if (state == State.WAIT_FOR_RELEASE) {
                    break
                }

                val currTime = customClock.time

                if (currTime - downTime > clickDelay) {
                    release()
                    break
                }

                if (state == State.DELAY_BEFORE_LONG_CLICK) {
                    clickTime = currTime
                    state = State.DELAY_BEFORE_DOUBLE_CLICK
                }
                else {
                    state = State.IDLE
                }
            } while (false)
        }
    }

    private fun isMoved(): Boolean =
        (movementStorer?.getMovement()?:(movementThreshold + 1f)) >= movementThreshold

    private fun releaseIfMovedOrPerform(action: () -> Unit) {
        if (isMoved()) {
            release()
        } else {
            action()
        }
    }

    fun tick() {
        val currTime = customClock.time
        when (state) {
            State.DELAY_BEFORE_LONG_CLICK -> {
                releaseIfMovedOrPerform {
                    if (currTime - downTime > longClickDelay) {
                        state = State.WAIT_FOR_RELEASE
                        clickType = TouchType.LONG
                    }
                }
            }
            State.DELAY_BEFORE_DOUBLE_CLICK -> {
                releaseIfMovedOrPerform {
                    if (currTime - clickTime > doubleClickDelay) {
                        state = State.WAIT_FOR_RELEASE
                        clickType = TouchType.SHORT
                    }
                }
            }
        }
    }

    override fun release() {
        state = State.IDLE
    }

    override fun getState() = state == State.WAIT_FOR_RELEASE
}