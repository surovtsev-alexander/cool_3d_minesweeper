package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MyIntEdit
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    val controls: Array<Pair<MyIntEdit, String>> by lazy {
        arrayOf<Pair<MyIntEdit, String>>(
            mie_xCount to "x count",
            mie_yCount to "y count",
            mie_zCount to "z count",
            mie_bombsPercentage to "bombs percentage"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val initMyIntEdit = { e: MyIntEdit, name: String ->
            e.name = name
            e.minValue = 3
            e.maxValue = 100
            e.value = 12
        }

        controls.map { initMyIntEdit(it.first, it.second) }

        mie_bombsPercentage.value = 20

        Log.d("TEST+++", "${mie_bombsPercentage.value}")

        btn_save.setOnClickListener {
            tryToSaveAndFinish()
        }
    }

    private fun tryToSaveAndFinish() {
        val invalidControls = controls.filter {
            !it.first.isValueInBorders()
        }

        val invalidControlsCount = invalidControls.count()
        if (invalidControlsCount == 0) {
            finish()
            return
        }

        val names = invalidControls.fold("") { acc, p ->
            acc + "\n${p.second}"
        }
        invalidControls.map { it.first.setMinValue() }

        Toast.makeText(
            this,
            "bad value${if (invalidControlsCount == 1) "" else "s"} in:\n$names",
            Toast.LENGTH_SHORT
        ).show()
    }
}