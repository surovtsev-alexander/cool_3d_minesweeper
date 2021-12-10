package com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.view.androidview.interaction.TouchType
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.interfaces.MovementHolder
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.interfaces.TouchReceiver
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpanHelper
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class TouchReceiverImp @Inject constructor(
    private val customClock: TimeSpanHelper
): TouchReceiver
{

    private enum class State {
        IDLE,
        DELAY_BEFORE_LONG_TOUCH,
        WAIT_FOR_RELEASE
    }

    private var state = State.IDLE

    var touchType = TouchType.SHORT
        private set

    var touchPos = Vec2()
        private set
    private var downTime = 0L

    @Suppress("SpellCheckingInspection")
    private var movementHolderStorer: MovementHolder? = null

    private val movementThreshold = 10f
    private val touchDelay = 100L
    private val longTouchDelay = 300L

    override fun down(pos: Vec2, movementHolderSaver: MovementHolder) {
        touchPos = pos
        downTime = customClock.timeAfterDeviceStartup

        this.movementHolderStorer = movementHolderSaver

        state =
            State.DELAY_BEFORE_LONG_TOUCH
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
                    state =
                        State.WAIT_FOR_RELEASE
                    touchType = TouchType.SHORT
                } else {
                    state = State.IDLE
                }
            } while (false)
        }
    }

    private fun isMoved(): Boolean =
        (movementHolderStorer?.getMovement() ?: (movementThreshold + 1f)) >= movementThreshold

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
        }
    }

    override fun release() {
        state = State.IDLE
    }

    override fun isUpdated() = state == State.WAIT_FOR_RELEASE
}