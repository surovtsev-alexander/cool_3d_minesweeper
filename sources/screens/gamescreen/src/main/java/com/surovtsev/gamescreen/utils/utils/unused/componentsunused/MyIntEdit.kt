package com.surovtsev.gamescreen.utils.utils.unused.componentsunused

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.surovtsev.gamescreen.R
import com.surovtsev.gamescreen.utils.utils.unused.interfaces.UIIntValueSelector

@Suppress("unused")
class MyIntEdit : LinearLayout, UIIntValueSelector {

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

    private var tvName: TextView? = null
    private var etValue: EditText? = null
    private var tvMinMax: TextView? = null

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
        tvMinMax?.text = "${minValueData..maxValueData}"
    }
}