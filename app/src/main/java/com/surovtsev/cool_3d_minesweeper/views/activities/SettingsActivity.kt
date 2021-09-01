package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import com.surovtsev.cool_3d_minesweeper.utils.android_view.components.MyIntEdit
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private val controls: Map<String, MyIntEdit> by lazy {
        mapOf<String, MyIntEdit>(
            GameSettings.xCount to mie_xCount,
            GameSettings.yCount to mie_yCount,
            GameSettings.zCount to mie_zCount,
            GameSettings.bombsPercentage to mie_bombsPercentage
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val initMyIntEdit = { e: MyIntEdit, name: String ->
            e.name = name
            e.minValue = 3
            e.maxValue = 100
        }

        controls.map { initMyIntEdit(it.value, it.key) }

        val loadedGameSettings =
            ApplicationController.instance.saveController.tryToLoad<GameSettings>(
                SaveController.GameSettingsJson
            )

        val gameSettings = GameSettings.createObject(
            loadedGameSettings
        )

        gameSettings.settingsMap.map { (k, v) ->
            controls[k]?.value = v
        }

        btn_save.setOnClickListener {
            tryToSaveAndFinish()
        }
    }

    private fun tryToSaveAndFinish() {
        val invalidControls = controls.filter {
            !it.value.isValueInBorders()
        }

        val invalidControlsCount = invalidControls.count()
        if (invalidControlsCount == 0) {
            val m = controls.map { x ->
                x.key to x.value.value
            }.toMap()
            val gameSettings = GameSettings(m)
            ApplicationController.instance.saveController.save(
                SaveController.GameSettingsJson,
                gameSettings
            )

            finish()
            return
        }

        val names = invalidControls.asIterable().fold("") { acc, p ->
            acc + "\n${p.key}"
        }
        invalidControls.map { it.value.setMinValue() }

        Toast.makeText(
            this,
            "bad value${if (invalidControlsCount == 1) "" else "s"} in:\n$names",
            Toast.LENGTH_SHORT
        ).show()
    }
}