package com.surovtsev.cool_3d_minesweeper.utils.interfaces

interface IUiIntValueSelector {
    var name: String
    var value: Int
    var minValue: Int
    var maxValue: Int

    fun isValueInBorders() =
        value in minValue..maxValue

    fun setMinValue(): Unit{
        value = minValue
    }
}