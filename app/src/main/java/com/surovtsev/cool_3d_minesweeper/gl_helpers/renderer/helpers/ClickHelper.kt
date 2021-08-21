package com.surovtsev.cool_3d_minesweeper.gl_helpers.renderer.helpers

import com.surovtsev.cool_3d_minesweeper.view.touch_helpers.interfaces.IClickReceiver
import glm_.vec2.Vec2

class ClickHelper(private val rendererTimer: RendererTimer): IClickReceiver {

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

    override fun handleClick(point: Vec2) {
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

    override fun handleLongClick(point: Vec2) {
        state = State.WAIT_FOR_RELEASE
        clickType = ClickType.LONG_CLICK
        clickPos = point
    }

    fun isClicked() = state == State.WAIT_FOR_RELEASE
}