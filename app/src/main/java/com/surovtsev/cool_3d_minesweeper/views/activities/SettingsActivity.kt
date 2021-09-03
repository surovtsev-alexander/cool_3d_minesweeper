package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDBHelper
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IUiIntValueSelector
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.StringBuilder

class SettingsActivity : AppCompatActivity() {
    private val controls: Map<String, IUiIntValueSelector> by lazy {
        mapOf<String, IUiIntValueSelector>(
            GameSettings.xCount to ivs_xCount,
            GameSettings.yCount to ivs_yCount,
            GameSettings.zCount to ivs_zCount,
            GameSettings.bombsPercentage to ivs_bombsPercentage
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val borders = GameSettings.borders
        val initMyIntEdit = { e: IUiIntValueSelector, name: String ->
            e.name = name
            val (l, r) = borders[name]!!
            e.minValue = l
            e.maxValue = r
        }

        controls.map { initMyIntEdit(it.value, it.key) }

        val loadedGameSettings =
            ApplicationController.instance.saveController.tryToLoad<GameSettings>(
                SaveTypes.GameSettingsJson
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

        val settingsDB = SettingsDBHelper(this)
        val settingsList = settingsDB.getSettingsList()

        val sb = StringBuilder()
        sb.append("x\nsettingsList:\n")
        settingsList.map {
            sb.append("$it\n")
        }
        sb.append("----\n")
        Log.d("TEST+++", sb.toString())
    }

    override fun onResume() {
        super.onResume()
        ApplicationController.activityStarted()
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
                SaveTypes.GameSettingsJson,
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