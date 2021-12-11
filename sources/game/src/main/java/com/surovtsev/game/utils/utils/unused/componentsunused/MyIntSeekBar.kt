package com.surovtsev.game.utils.utils.unused.componentsunused

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.surovtsev.game.R
import com.surovtsev.game.utils.utils.unused.interfaces.UIIntValueSelector

@Suppress("unused")
class MyIntSeekBar : LinearLayout, UIIntValueSelector {

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
    private var tvValue: TextView? = null
    private var tvMinMax: TextView? = null
    private var sbValue: SeekBar? = null

    private fun init(
        @Suppress("UNUSED_PARAMETER") attrs: AttributeSet?,
        @Suppress("UNUSED_PARAMETER") defStyle: Int
    ) {
        inflate(context, R.layout.my_int_seek_bar, this)

        tvName = findViewById(R.id.tvName)
        tvValue = findViewById(R.id.tvValue)
        tvMinMax = findViewById(R.id.tvMinMax)
        sbValue = findViewById(R.id.sbValue)

        sbValue!!.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvValue!!.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override var name: String
        get() = tvName?.text.toString()
        set(value) {
            tvName?.text = value
        }

    override var minValue: Int
        get() = sbValue?.min?:0
        set(value) {
            sbValue?.min = value
            borderUpdated()
        }

    override var maxValue: Int
        get() = sbValue?.max?:0
        set(value) {
            sbValue?.max = value
            borderUpdated()
        }

    override var value: Int
        get() = sbValue?.progress?:0
        set(v) {
            sbValue?.progress = v
        }

    private fun borderUpdated() {
        tvMinMax?.text = "(${minValue..maxValue})"
    }
}
