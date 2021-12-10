package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.utils.time.timers.Tickable
import com.surovtsev.utils.androidview.interaction.TouchType
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.holders.MovementHolder
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers.TouchReceiver
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpanHelper
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.handlers.TouchHandler
import glm_.vec2.Vec2
import javax.inject.Inject

@GameScope
class TouchReceiverImp @Inject constructor(
    private val customClock: TimeSpanHelper,
    private val touchHandler: TouchHandler,
): TouchReceiver, Tickable
{

    init {
        customClock.subscribe(this)
    }

    private enum class State {
        IDLE,
        DELAY_BEFORE_LONG_TOUCH
    }

    private var state = State.IDLE

    var touchType = TouchType.SHORT
        private set

    var touchPos = Vec2()
        private set
    private var downTime = 0L

    @Suppress("SpellCheckingInspection")
    private var movementHolderStorer: MovementHolder? = null

    companion object {
        private const val MOVEMENT_THRESHOLD = 10f
        private const val TOUCH_DELAY = 100L
        private const val LONG_TOUCH_DELAY = 300L
    }

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
                val currTime = customClock.timeAfterDeviceStartup

                if (currTime - downTime > TOUCH_DELAY) {
                    release()
                    break
                }

                if (state == State.DELAY_BEFORE_LONG_TOUCH) {
                    notifyHandler(TouchType.SHORT)
                } else {
                    state = State.IDLE
                }
            } while (false)
        }
    }

    private fun isMoved(): Boolean =
        (movementHolderStorer?.getMovement() ?: (MOVEMENT_THRESHOLD + 1f)) >= MOVEMENT_THRESHOLD

    private fun releaseIfMovedOrPerform(action: () -> Unit) {
        if (isMoved()) {
            release()
        } else {
            action()
        }
    }

    override fun tick() {
        val currTime = customClock.timeAfterDeviceStartup
        if (state == State.DELAY_BEFORE_LONG_TOUCH) {
            releaseIfMovedOrPerform {
                if (currTime - downTime > LONG_TOUCH_DELAY) {
                    notifyHandler(TouchType.LONG)
                }
            }
        }
    }

    private fun notifyHandler(
        touchType: TouchType
    ) {
        touchHandler.handleTouch(
            touchPos,
            touchType
        )

        this.release()
    }

    override fun release() {
        state = State.IDLE
    }
}