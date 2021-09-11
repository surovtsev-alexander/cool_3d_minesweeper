package com.surovtsev.cool_3d_minesweeper.utils.android_view.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.SeekBar
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IUiIntValueSelector
import kotlinx.android.synthetic.main.my_int_seek_bar.view.*

/**
 * TODO: document your custom view class.
 */
class MyIntSeekBar : LinearLayout, IUiIntValueSelector {

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
        inflate(context, R.layout.my_int_seek_bar, this)

        sb_value.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_value.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override var name: String
        get() = tv_name.text.toString()
        set(value) {
            tv_name.text = value
        }

    override var minValue: Int
        get() = sb_value.min
        set(value) {
            sb_value.min = value
            borderUpdated()
        }

    override var maxValue: Int
        get() = sb_value.max
        set(value) {
            sb_value.max = value
            borderUpdated()
        }

    override var value: Int
        get() = sb_value.progress
        set(v) {
            sb_value.progress = v
        }

    private fun borderUpdated() {
        tv_min_max.setText("(${minValue..maxValue})")
    }
}
