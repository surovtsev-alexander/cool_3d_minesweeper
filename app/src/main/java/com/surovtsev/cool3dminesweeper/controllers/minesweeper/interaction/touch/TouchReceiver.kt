package com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch

import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.androidview.interaction.TouchType
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces.IStoreMovement
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.interfaces.ITouchReceiver
import com.surovtsev.cool3dminesweeper.utils.time.TimeSpanHelper
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class TouchReceiver @Inject constructor(
    private val customClock: TimeSpanHelper
): ITouchReceiver
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
    private var movementStorer: IStoreMovement? = null

    private val movementThreshold = 10f
    private val touchDelay = 100L
    private val longTouchDelay = 300L

    override fun down(pos: Vec2, movementSaver: IStoreMovement) {
        touchPos = pos
        downTime = customClock.timeAfterDeviceStartup

        this.movementStorer = movementSaver

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
        (movementStorer?.getMovement() ?: (movementThreshold + 1f)) >= movementThreshold

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