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


package com.surovtsev.utils.compose.components.intslider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class SliderContext(
    val borders: IntRange,
    private val width: Int,
    private var position: Int,
) {
    private val bordersFirst = borders.first
    private val bordersLast = borders.last

    var prevDelta = 0f
        private set

    private val bordersWidth = bordersLast - bordersFirst
    private val deltaToPosCoefficient =
        if (width == 0 || bordersWidth == 0) 1f else bordersWidth.toFloat() / width

    suspend fun loop() {

    }

    fun erasePrevDelta() {
        prevDelta = 0f
        updateLineWidthRate()
    }

    fun setPosition(newPosition: Int) {
        if (isScrollInProgress) {
            return
        }
        if (newPosition != position) {
            prevDelta = 0f
            position = newPosition
            updateLineWidthRate()
        }
    }

    private val _lineWidthRate = MutableStateFlow(0f)
    val lineWidthRate = _lineWidthRate.asStateFlow()

    private fun updateLineWidthRate() {
        _lineWidthRate.value = ((position - bordersFirst).toFloat() / (bordersLast - bordersFirst)) + prevDelta / (if (width == 0) 1 else width)
    }

    init {
        updateLineWidthRate()
    }

    var isScrollInProgress = false


    fun slide(
        delta: Float,
        onChange: IntSliderOnChange,
    ) {
        prevDelta += delta

        if (prevDelta < 0) {
            val dx = (position - bordersFirst).coerceAtLeast(0)
            val availableDelta = -1 * dx / deltaToPosCoefficient

            prevDelta = prevDelta.coerceAtLeast(availableDelta)
        } else if (prevDelta > 0) {
            val dx = (bordersLast - position).coerceAtLeast(0)
            val availableDelta = dx / deltaToPosCoefficient

            prevDelta = prevDelta.coerceAtMost(availableDelta)
        }

        val rawDiffPos = prevDelta * deltaToPosCoefficient

        val diffPos = rawDiffPos.roundToInt()

        prevDelta -= diffPos / deltaToPosCoefficient

        val newPosition = Math.min(
            Math.max(
                position + diffPos,
                bordersFirst
            ),
            bordersLast
        )

        if (newPosition == bordersFirst && prevDelta < 0) {
            prevDelta = 0f
        }

        if (newPosition == bordersLast && prevDelta > 0) {
            prevDelta = 0f
        }


        if (newPosition != position) {
            position = newPosition
            onChange(newPosition)
        }
        updateLineWidthRate()
    }
}