package com.surovtsev.touchlistener.helpers

import com.surovtsev.touchlistener.dagger.TouchListenerScope
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler
import com.surovtsev.touchlistener.helpers.holders.MovementHolder
import com.surovtsev.touchlistener.helpers.receivers.TouchReceiver
import com.surovtsev.utils.androidview.interaction.TouchType
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscriber
import com.surovtsev.utils.coroutines.customcoroutinescope.subscriptions.Subscription
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import glm_.vec2.Vec2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@TouchListenerScope
class TouchReceiverImp @Inject constructor(
    private val timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    private val touchHandler: TouchHandler,
    subscriber: Subscriber,
): Subscription, TouchReceiver
{

    init {
        subscriber
            .addSubscription(this)
    }

    override fun initSubscription(customCoroutineScope: CustomCoroutineScope) {
        customCoroutineScope.launch {
            timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.collectLatest {
                tick(it)
            }
        }
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
        downTime = timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value

        this.movementHolderStorer = movementHolderSaver

        state =
            State.DELAY_BEFORE_LONG_TOUCH
    }

    override fun up() {
        releaseIfMovedOrPerform {
            do {
                val currTime = timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value

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

    private fun tick(currTime: Long) {
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