package com.surovtsev.cool_3d_minesweeper.utils.unused

import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2

class ComplexTouchHelper(private val customClock: CustomClock): ITouchReceiver {

    enum class TouchType {
        SHORT,
        LONG,
        DOUBLE_TOUCH
    }

    private enum class State {
        IDLE,
        DELAY_BEFORE_LONG_TOUCH,
        DELAY_BEFORE_DOUBLE_TOUCH,
        WAIT_FOR_RELEASE
    }

    private var state =
        State.IDLE

    var touchType =
        TouchType.SHORT
        private set

    var touchPos = Vec2()
        private set
    private var downTime = 0L
    private var clickTime = 0L

    var movementStorer: IStoreMovement? = null

    val movementThreshold = 10f
    val touchDelay = 100L
    val doubleTouchDelay = 250L
    val longTouchDelay = 300L

    override fun donw(pos: Vec2, movementStorer_: IStoreMovement) {
        if (state == State.DELAY_BEFORE_DOUBLE_TOUCH) {
            state =
                State.WAIT_FOR_RELEASE
            touchType =
                TouchType.DOUBLE_TOUCH
        } else {
            touchPos = pos
            downTime = customClock.time

            movementStorer = movementStorer_

            state = State.DELAY_BEFORE_LONG_TOUCH
        }
    }

    override fun up() {
        releaseIfMovedOrPerform {
            do {
                if (state == State.WAIT_FOR_RELEASE) {
                    break
                }

                val currTime = customClock.time

                if (currTime - downTime > touchDelay) {
                    release()
                    break
                }

                if (state == State.DELAY_BEFORE_LONG_TOUCH) {
                    clickTime = currTime
                    state =
                        State.DELAY_BEFORE_DOUBLE_TOUCH
                }
                else {
                    state =
                        State.IDLE
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
            State.DELAY_BEFORE_LONG_TOUCH -> {
                releaseIfMovedOrPerform {
                    if (currTime - downTime > longTouchDelay) {
                        state = State.WAIT_FOR_RELEASE
                        touchType = TouchType.LONG
                    }
                }
            }
            State.DELAY_BEFORE_DOUBLE_TOUCH -> {
                releaseIfMovedOrPerform {
                    if (currTime - clickTime > doubleTouchDelay) {
                        state = State.WAIT_FOR_RELEASE
                        touchType = TouchType.SHORT
                    }
                }
            }
        }
    }

    override fun release() {
        state = State.IDLE
    }

    override fun isUpdated() = state == State.WAIT_FOR_RELEASE
}