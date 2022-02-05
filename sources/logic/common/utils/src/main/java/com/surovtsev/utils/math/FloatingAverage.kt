package com.surovtsev.utils.math

class FloatingAverage(
    private val length: Int,
) {
    private val data = Array(length) { 0L }
    private var pos = 0

    private var filled = false

    private var sum = 0L

    fun next(nextValue: Long): Float {
        if (filled) {
            sum -= data[pos]
        }

        data[pos] = nextValue
        sum += nextValue

        pos += 1

        if (pos % length == 0) {
            pos = 0
            filled = true
        }

        return 1f * sum / if (filled) {
            length
        } else {
            pos
        }
    }
}