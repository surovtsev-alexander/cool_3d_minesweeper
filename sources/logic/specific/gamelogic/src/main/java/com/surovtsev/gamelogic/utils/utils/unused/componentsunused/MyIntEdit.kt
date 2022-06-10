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


package com.surovtsev.gamelogic.utils.utils.unused.componentsunused

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.surovtsev.gamelogic.R
import com.surovtsev.gamelogic.utils.utils.unused.interfaces.UIIntValueSelector

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