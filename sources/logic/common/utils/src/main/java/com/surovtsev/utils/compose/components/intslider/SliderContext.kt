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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

typealias IntSliderOnChange = (newValue: Int) -> Unit

class SliderContext(
    coroutineScope: CoroutineScope,
) {
    private val ioScope = CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO)
    private val _progress = MutableSharedFlow<Float>()
    private val _sliderPosition = MutableStateFlow(0)
    private val _lineWidthRate = MutableStateFlow(0f)
    private val sliderActionsFlow = MutableSharedFlow<Float>(0)
    private var slidingJob: Job? = null

    /* region public fields and methods */
    val onChange = MutableStateFlow<IntSliderOnChange?>(null)
    val borders = MutableStateFlow(IntRange(0, 1))
    val layoutWidth = MutableStateFlow(0)
    val isScrollInProgress = MutableStateFlow(false)

    val progress = _progress.asSharedFlow()
    val sliderPosition = _sliderPosition.asStateFlow()
    val lineWidthRate = _lineWidthRate.asStateFlow()

    fun stopJob() {
        slidingJob?.cancel()
        slidingJob = null
    }

    fun setPosition(newPosition: Int) {
        if (isScrollInProgress.value) {
            return
        }
        ioScope.launch {
            _progress.emit(
                calculateProgressByPosition(
                    borders.value,
                    newPosition,
                )
            )
        }
    }

    suspend fun slideAlt(
        delta: Float,
    ) {
        if (abs(delta) < 0.1f) {
            return
        }
        sliderActionsFlow.emit(delta)
    }
    /* endregion public fields and methods */

    private val bordersFirst = borders.map { it.first }
    private val bordersLast = borders.map {  it.last }
    private val bordersWidth = combine(bordersFirst, bordersLast) { first, last ->
        last - first
    }
    private val deltaOffset = combine(sliderPosition, bordersFirst, bordersWidth, progress) { sliderPosition, borderFirst, borderWidth, progress ->
        val expectedProgress = (sliderPosition - borderFirst).toFloat() / borderWidth
        expectedProgress - progress
    }

    init {
        val j = Job()

        slidingJob = j

        val ioScope = CoroutineScope(ioScope.coroutineContext + j + Dispatchers.IO)
        collectActions(ioScope)
    }

    private fun calculateProgressByPosition(
        borders: IntRange,
        position: Int
    ): Float {
        return ((position - borders.first).toFloat() / (borders.last - borders.first))
            .coerceAtLeast(0f)
            .coerceAtMost(1f)
    }

    private fun collectActions(
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            borders.collect { borders ->
                _progress.emit(calculateProgressByPosition(borders, sliderPosition.value))
            }
        }

        coroutineScope.launch {
            progress.collect { progress ->
                val borders = borders.value
                _sliderPosition.value = borders.first + (progress * (borders.last - borders.first)).roundToInt()
            }
        }

        coroutineScope.launch {
            combine(deltaOffset, layoutWidth) { deltaOffset, layoutWidth ->
                _lineWidthRate.value = deltaOffset * layoutWidth
            }.collect()
        }

        coroutineScope.launch {
            val progressState = progress.stateIn(coroutineScope)
            sliderActionsFlow.collect { slide ->
                val progress = progressState.value
                val layoutWidth = layoutWidth.value
                val newProgress = progress + (if (layoutWidth == 0 ) 0f else slide / layoutWidth)

                _progress.emit(
                    newProgress
                        .coerceAtLeast(0f)
                        .coerceAtMost(1f)
                )
            }
        }

        coroutineScope.launch {
            combine(onChange, sliderPosition) { onChange, sliderPosition ->
                onChange?.invoke(sliderPosition)
            }.collect()
        }
    }
}
