package com.surovtsev.cool_3d_minesweeper.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.ui.SettingsRecyclerViewAdapter
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettingsMap
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IUiIntValueSelector
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity :
    AppCompatActivity(),
    SettingsRecyclerViewAdapter.ISettingsRVEventListener
{
    private val settingsDBHelper: SettingsDBHelper by lazy {
        SettingsDBHelper(DBHelper(this))
    }

    private fun getDbSettingsList() =
        settingsDBHelper.getSettingsList().toMutableList()

    private val settingsRecyclerViewAdapter: SettingsRecyclerViewAdapter by lazy {
        SettingsRecyclerViewAdapter(getDbSettingsList(), this)
    }

    private val controls: Map<String, IUiIntValueSelector> by lazy {
        mapOf<String, IUiIntValueSelector>(
            GameSettingsMap.xCount to ivs_xCount,
            GameSettingsMap.yCount to ivs_yCount,
            GameSettingsMap.zCount to ivs_zCount,
            GameSettingsMap.bombsPercentage to ivs_bombsPercentage
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val borders = GameSettingsMap.borders
        val initMyIntEdit = { e: IUiIntValueSelector, name: String ->
            e.name = name
            val (l, r) = borders[name]!!
            e.minValue = l
            e.maxValue = r
        }

        controls.map { initMyIntEdit(it.value, it.key) }

        val loadedGameSettings =
            ApplicationController.instance.saveController.tryToLoad<GameSettingsMap>(
                SaveTypes.GameSettingsJson
            )

        val gameSettings = GameSettingsMap.createObject(
            loadedGameSettings
        )

        gameSettings.settingsMap.map { (k, v) ->
            controls[k]?.value = v
        }

        btn_save.setOnClickListener {
            tryToSaveAndFinish()
        }

        with (rv_settingsList) {
            val sA = this@SettingsActivity
            adapter = settingsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(sA)
            setHasFixedSize(true)
        }
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

            settingsDBHelper.insertIfNotPresent(
                SettingsData(
                    m
                )
            )

            val gameSettings = GameSettingsMap(m)
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

    override fun onItemClick(position: Int) {
        if (!settingsRecyclerViewAdapter.isValidPosition(position)) {
            return
        }
        val s = settingsRecyclerViewAdapter.get(position)
        s.getMap().map { (k, v) ->
            controls[k]?.value = v
        }

        if (true) {
            val x = settingsDBHelper.getId(s)
            Log.d("TEST+++", "SettingsActivity id: $x")
        }
    }

    override fun onItemDelete(position: Int) {
        if (!settingsRecyclerViewAdapter.isValidPosition(position)) {
            return
        }
        val settingsData = settingsRecyclerViewAdapter.get(position)
        settingsDBHelper.delete(settingsData)

        settingsRecyclerViewAdapter.removeAt(position)
    }
}
