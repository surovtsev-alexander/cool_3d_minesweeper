package com.surovtsev.cool_3d_minesweeper.utils.android_view.components_unused

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IUiIntValueSelector


class MyIntEdit : LinearLayout, IUiIntValueSelector {

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

    var tvName: TextView? = null
    var etValue: EditText? = null
    var tvMinMax: TextView? = null

    private fun init(
        @Suppress("UNUSED_PARAMETER") attrs: AttributeSet?,
        @Suppress("UNUSED_PARAMETER") defStyle: Int
    ) {
        inflate(context, R.layout.my_int_edit, this)

        tvName = findViewById(R.id.tvName)
        etValue = findViewById(R.id.etValue)
        tvMinMax = findViewById(R.id.tvMinMax)
    }

    override var name: String
        get() = tvName?.text.toString()
        set(value) {
            tvName?.text = value
        }

    private var minValueData = 0

    override var minValue: Int
        get() = minValueData
        set(value) {
            minValueData = value

            borderUpdated()
        }

    private var maxValueData = 0

    override var maxValue: Int
        get() = maxValueData
        set(value) {
            maxValueData = value

            borderUpdated()
        }

    override var value: Int
        get() = Integer.parseInt(etValue?.text.toString())
        set(v) {
            etValue?.setText(v.toString())
        }

    private fun borderUpdated() {
        tvMinMax?.setText("(${minValueData..maxValueData})")
    }
}