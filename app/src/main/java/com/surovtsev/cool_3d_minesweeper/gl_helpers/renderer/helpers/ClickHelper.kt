package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.ITouchReceiver
import glm_.vec2.Vec2
import glm_.vec2.Vec2l

class ClickHelper(private val rendererTimer: RendererTimer): ITouchReceiver {

    private val clickDelay = 200L

    enum class ClickType {
        CLICK,
        LONG_CLICK,
        DOUBLE_CLICK
    }

    enum class State {
        IDLE,
        DELAY,
        WAIT_FOR_RELEASE
    }

    var state = State.IDLE
        private set

    var clickType = ClickType.CLICK
        private set

    var clickPos = Vec2()
        private set
    private var clickTime = 0L

    val movementThreshold = 0.1
    val clickMaxTimeMs = 100L
    val longClickTimeBorders = Vec2l(100L, 1000L)

    override fun handletouch(point: Vec2, movement: Float, diffTime: Long) {
        if (movement > movementThreshold) {
            release()
            return
        }

        if (
            diffTime >= longClickTimeBorders.x &&
            diffTime <= longClickTimeBorders.y) {

            handleLongClick(point)
            return
        }

        if (diffTime <= clickMaxTimeMs) {
            handleClick(point)
        }
    }

    fun tick() {
        if (state != State.DELAY) {
            return
        }
        val currTime = rendererTimer.time

        if (currTime - clickTime >= clickDelay) {
            state = State.WAIT_FOR_RELEASE
            clickType = ClickType.CLICK
        }
    }

    fun release() {
        state = State.IDLE
    }

    private fun handleClick(point: Vec2) {
        val currTime = rendererTimer.time

        when (state) {
            State.IDLE -> {
                clickPos = point
                clickTime = currTime

                state = State.DELAY
            }
            State.DELAY -> {
                if (currTime - clickTime < clickDelay) {
                    state = State.WAIT_FOR_RELEASE
                    clickType = ClickType.DOUBLE_CLICK
                }
            }
            else -> {
                state = State.IDLE
            }
        }
    }

    private fun handleLongClick(point: Vec2) {
        state = State.WAIT_FOR_RELEASE
        clickType = ClickType.LONG_CLICK
        clickPos = point
    }

    fun isClicked() = state == State.WAIT_FOR_RELEASE
}