package com.surovtsev.cool_3d_minesweeper.utils.android_view.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.surovtsev.cool_3d_minesweeper.R
import glm_.toInt
import kotlinx.android.synthetic.main.my_int_edit.view.*
import java.lang.Exception

/**
 * TODO: document your custom view class.
 */
class MyIntEdit : LinearLayout {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(context, R.layout.my_int_edit, this)
    }

    var name: String
        get() = tv_name.text.toString()
        set(value) {
            tv_name.text = value
        }

    private var minValueData = 0
    var minValue: Int
        get() = minValueData
        set(value) {
            minValueData = value

            borderUpdated()
        }

    private var maxValueData = 0
    var maxValue: Int
        get() = maxValueData
        set(value) {
            maxValueData = value

            borderUpdated()
        }

    var value: Int
        get() = Integer.parseInt(et_value.text.toString())
        set(v) {
            et_value.setText(v.toString())
        }

    private fun borderUpdated() {
        tv_min_max.setText("($minValueData..$maxValueData)")
    }

    fun isValueInBorders() =
        value in minValue..maxValueData

    fun setMinValue(): Unit{
        value = minValue
    }
}