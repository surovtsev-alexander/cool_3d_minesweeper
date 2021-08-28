package com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers

import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2

class ClickHelper(private val customClock: CustomClock): ITouchReceiver {

    enum class State {
        IDLE,
        DELAY_BEFORE_LONG_TOUCH,
        WAIT_FOR_RELEASE
    }

    var state = State.IDLE
        private set

    var touchType = TouchType.SHORT
        private set

    var clickPos = Vec2()
        private set
    private var downTime = 0L

    private var movementStorer: IStoreMovement? = null

    private val movementThreshold = 10f
    private val clickDelay = 100L
    private val longClickDelay = 300L

    override fun donw(pos: Vec2, movementStorer_: IStoreMovement) {
        clickPos = pos
        downTime = customClock.time

        movementStorer = movementStorer_

        state = State.DELAY_BEFORE_LONG_TOUCH
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

                if (state == State.DELAY_BEFORE_LONG_TOUCH) {
                    state = State.WAIT_FOR_RELEASE
                    touchType = TouchType.SHORT
                } else {
                    state = State.IDLE
                }
            } while (false)
        }
    }

    private fun isMoved(): Boolean =
        (movementStorer?.getMovement() ?: (movementThreshold + 1f)) >= movementThreshold

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
                    if (currTime - downTime > longClickDelay) {
                        state = State.WAIT_FOR_RELEASE
                        touchType = TouchType.LONG
                    }
                }
            }
        }
    }

    override fun release() {
        state = State.IDLE
    }

    fun isClicked() = state == State.WAIT_FOR_RELEASE
}