package com.surovtsev.cool_3d_minesweeper.views.activities.multitouchTest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.surovtsev.cool_3d_minesweeper.R
import kotlinx.android.synthetic.main.activity_multitouch_test.*

class MultitouchTestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multitouch_test)


        mv_main.setOnTouchListener(object: View.OnTouchListener {
            var inTouch = false
            var upPI = 0
            var downPI = 0
//            val sb = StringBuilder()

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) {
                    return false
                }

                val (actionMask, pointerIndex, pointerCount) = event.run {
                    Triple(actionMasked, actionIndex, pointerCount)
                }

                when (actionMask) {
                    MotionEvent.ACTION_DOWN -> { // first touch
                        inTouch = true
                        downPI = pointerIndex
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> { // next touch
                        downPI = pointerIndex
                    }
                    MotionEvent.ACTION_UP -> { // last touch is released
                        inTouch = false
//                        sb.setLength(0)
                        upPI = pointerIndex
                    }
                    MotionEvent.ACTION_POINTER_UP -> { // touch is released
                        upPI = pointerIndex
                    }
//                    MotionEvent.ACTION_MOVE -> { // moving
//                        sb.setLength(0)
//                        for (i in 0 until pointerCount) {
//                            sb.append("Index: $i")
//                            sb.append(", ID = ${event.getPointerId(i)}")
//                            sb.append(", X = ${event.getX(i)}")
//                            sb.append(", Y = ${event.getY(i)}")
//                            sb.append("\r\n")
//                        }
//                    }
                }

                val res = "down: $downPI\nup: $upPI\npointerCount: $pointerCount"

                tv_main.addMessage(res) //+ sb.toString()
                return true
            }
        })
    }
}