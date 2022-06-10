/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.utils.utils.unused

//import TimeSpanHelperImp
//import com.surovtsev.touchlistener.helpers.receivers.TouchReceiver
//import glm_.vec2.Vec2
//
//@Suppress("unused")
//class ComplexTouchHelper(private val customClock: TimeSpanHelperImp):
//    TouchReceiver {
//    enum class TouchType {
//        SHORT,
//        LONG,
//        DOUBLE_TOUCH
//    }
//
//    private enum class State {
//        IDLE,
//        DELAY_BEFORE_LONG_TOUCH,
//        DELAY_BEFORE_DOUBLE_TOUCH,
//        WAIT_FOR_RELEASE
//    }
//
//    private var state =
//        State.IDLE
//
//    private var touchType =
//        TouchType.SHORT
//
//    private var touchPos = Vec2()
//    private var downTime = 0L
//    private var clickTime = 0L
//
//    private var movementHolder: com.surovtsev.touchlistener.helpers.holders.MovementHolder? = null
//
//    private val movementThreshold = 10f
//    private val touchDelay = 100L
//    private val doubleTouchDelay = 250L
//    private val longTouchDelay = 300L
//
//    override fun down(pos: Vec2, movementHolderSaver: com.surovtsev.touchlistener.helpers.holders.MovementHolder) {
//        if (state == State.DELAY_BEFORE_DOUBLE_TOUCH) {
//            state =
//                State.WAIT_FOR_RELEASE
//            touchType =
//                TouchType.DOUBLE_TOUCH
//        } else {
//            touchPos = pos
//            downTime = customClock.timeAfterDeviceStartup
//
//            this.movementHolder = movementHolderSaver
//
//            state = State.DELAY_BEFORE_LONG_TOUCH
//        }
//    }
//
//    override fun up() {
//        releaseIfMovedOrPerform {
//            do {
//                if (state == State.WAIT_FOR_RELEASE) {
//                    break
//                }
//
//                val currTime = customClock.timeAfterDeviceStartup
//
//                if (currTime - downTime > touchDelay) {
//                    release()
//                    break
//                }
//
//                if (state == State.DELAY_BEFORE_LONG_TOUCH) {
//                    clickTime = currTime
//                    state =
//                        State.DELAY_BEFORE_DOUBLE_TOUCH
//                }
//                else {
//                    state =
//                        State.IDLE
//                }
//            } while (false)
//        }
//    }
//
//    private fun isMoved(): Boolean =
//        (movementHolder?.getMovement()?:(movementThreshold + 1f)) >= movementThreshold
//
//    private fun releaseIfMovedOrPerform(action: () -> Unit) {
//        if (isMoved()) {
//            release()
//        } else {
//            action()
//        }
//    }
//
//    fun tick() {
//        val currTime = customClock.timeAfterDeviceStartup
//        if (state == State.DELAY_BEFORE_LONG_TOUCH) {
//            releaseIfMovedOrPerform {
//                if (currTime - downTime > longTouchDelay) {
//                    state = State.WAIT_FOR_RELEASE
//                    touchType = TouchType.LONG
//                }
//            }
//        } else if (state == State.DELAY_BEFORE_DOUBLE_TOUCH) {
//            if (currTime - clickTime > doubleTouchDelay) {
//                state = State.WAIT_FOR_RELEASE
//                touchType = TouchType.SHORT
//            }
//        }
//    }
//
//    override fun release() {
//        state = State.IDLE
//    }
//
//    fun isUpdated() = state == State.WAIT_FOR_RELEASE
//}
