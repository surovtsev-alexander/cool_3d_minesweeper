package com.surovtsev.cool3dminesweeper.utils.math

import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object SliderMath {
    fun floatToInt(x: Float) = round(x).toInt()

    fun intToFloat(x: Int) = x.toFloat()

    fun getRateByPosition(
        sliderPosition: Float,
        borders: IntRange
    ): Float {
        val first = borders.first
        val last = borders.last
        return (sliderPosition - first) / (last - first)
    }

    fun getDiffByRate(
        rate: Float,
        borders: IntRange
    ): Float {
        val first = borders.first
        val last = borders.last
        return rate * (last - first)
    }

    fun clipPosition(
        sliderPosition: Float,
        borders: IntRange
    ) = max(
        borders.first.toFloat(),
        min(
            borders.last.toFloat(),
            sliderPosition
        )
    )
}
