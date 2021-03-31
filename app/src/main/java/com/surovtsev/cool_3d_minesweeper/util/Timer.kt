package com.surovtsev.cool_3d_minesweeper.util

import android.text.format.Time
import java.util.*

class Timer {
    private var mPrevMs: Long = 0L
    private var mCurrMs: Long = 0L

    fun push() {
        mPrevMs = mCurrMs
        mCurrMs = Calendar.getInstance().timeInMillis
    }

    fun diff() = mCurrMs - mPrevMs
}