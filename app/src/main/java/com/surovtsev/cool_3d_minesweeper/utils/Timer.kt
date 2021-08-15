package com.surovtsev.cool_3d_minesweeper.utils

import java.util.*

class Timer {
    private var mPrevMs: Long = 0L
    private var mCurrMs: Long = 0L

    fun push() {
        mPrevMs = mCurrMs
        mCurrMs = Calendar.getInstance().timeInMillis
    }

    fun push_hour_before() {
        mPrevMs = mCurrMs
        mCurrMs = Calendar.getInstance().timeInMillis - 1000L * 60L * 60L
    }

    fun diff() = mCurrMs - mPrevMs
}