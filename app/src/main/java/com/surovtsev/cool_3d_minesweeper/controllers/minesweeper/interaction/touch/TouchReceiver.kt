package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction_handler

import com.surovtsev.cool_3d_minesweeper.utils.android_view.interaction.TouchType
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2

class TouchReceiver(private val customClock: CustomClock): ITouchReceiver {

    private enum class State {
        IDLE,
        DELAY_BEFORE_LONG_CLICK,
        WAIT_FOR_RELEASE
    }

    private var state =
        State.IDLE

    var clickType = TouchType.SHORT
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

        state =
            State.DELAY_BEFORE_LONG_CLICK
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
                    state =
                        State.WAIT_FOR_RELEASE
                    clickType = TouchType.SHORT
                } else {
                    state =
                        State.IDLE
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
            State.DELAY_BEFORE_LONG_CLICK -> {
                releaseIfMovedOrPerform {
                    if (currTime - downTime > longClickDelay) {
                        state =
                            State.WAIT_FOR_RELEASE
                        clickType = TouchType.LONG
                    }
                }
            }
        }
    }

    override fun release() {
        state =
            State.IDLE
    }

    fun isClicked() = state == State.WAIT_FOR_RELEASE
}