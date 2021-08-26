package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IStoreMovement
import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2

class ClickHelper(private val rendererTimer: RendererTimer): ITouchReceiver {

    enum class ClickType {
        CLICK,
        LONG_CLICK,
    }

    enum class State {
        IDLE,
        DELAY_BEFORE_LONG_CLICK,
        WAIT_FOR_RELEASE
    }

    var state = State.IDLE
        private set

    var clickType = ClickType.CLICK
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
        clickPos = pos
        downTime = rendererTimer.time

        movementStorer = movementStorer_

        state = State.DELAY_BEFORE_LONG_CLICK
    }

    override fun up() {
        releaseIfMovedOrPerform {
            do {
                if (state == State.WAIT_FOR_RELEASE) {
                    break
                }

                val currTime = rendererTimer.time

                if (currTime - downTime > clickDelay) {
                    release()
                    break
                }

                if (state == State.DELAY_BEFORE_LONG_CLICK) {
                    state = State.WAIT_FOR_RELEASE
                    clickType = ClickType.CLICK
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
        val currTime = rendererTimer.time
        when (state) {
            State.DELAY_BEFORE_LONG_CLICK -> {
                releaseIfMovedOrPerform {
                    if (currTime - downTime > longClickDelay) {
                        state = State.WAIT_FOR_RELEASE
                        clickType = ClickType.LONG_CLICK
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