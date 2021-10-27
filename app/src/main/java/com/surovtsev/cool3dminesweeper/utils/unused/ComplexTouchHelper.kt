package com.surovtsev.cool3dminesweeper.utils.unused

import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpanHelper
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces.MovementHolder
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces.TouchReceiver
import glm_.vec2.Vec2

@Suppress("unused")
class ComplexTouchHelper(private val customClock: TimeSpanHelper): TouchReceiver {
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

    private var touchType =
        TouchType.SHORT

    private var touchPos = Vec2()
    private var downTime = 0L
    private var clickTime = 0L

    private var movementHolder: MovementHolder? = null

    private val movementThreshold = 10f
    private val touchDelay = 100L
    private val doubleTouchDelay = 250L
    private val longTouchDelay = 300L

    override fun down(pos: Vec2, movementHolderSaver: MovementHolder) {
        if (state == State.DELAY_BEFORE_DOUBLE_TOUCH) {
            state =
                State.WAIT_FOR_RELEASE
            touchType =
                TouchType.DOUBLE_TOUCH
        } else {
            touchPos = pos
            downTime = customClock.timeAfterDeviceStartup

            this.movementHolder = movementHolderSaver

            state = State.DELAY_BEFORE_LONG_TOUCH
        }
    }

    override fun up() {
        releaseIfMovedOrPerform {
            do {
                if (state == State.WAIT_FOR_RELEASE) {
                    break
                }

                val currTime = customClock.timeAfterDeviceStartup

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
        (movementHolder?.getMovement()?:(movementThreshold + 1f)) >= movementThreshold

    private fun releaseIfMovedOrPerform(action: () -> Unit) {
        if (isMoved()) {
            release()
        } else {
            action()
        }
    }

    fun tick() {
        val currTime = customClock.timeAfterDeviceStartup
        if (state == State.DELAY_BEFORE_LONG_TOUCH) {
            releaseIfMovedOrPerform {
                if (currTime - downTime > longTouchDelay) {
                    state = State.WAIT_FOR_RELEASE
                    touchType = TouchType.LONG
                }
            }
        } else if (state == State.DELAY_BEFORE_DOUBLE_TOUCH) {
            if (currTime - clickTime > doubleTouchDelay) {
                state = State.WAIT_FOR_RELEASE
                touchType = TouchType.SHORT
            }
        }
    }

    override fun release() {
        state = State.IDLE
    }

    override fun isUpdated() = state == State.WAIT_FOR_RELEASE
}