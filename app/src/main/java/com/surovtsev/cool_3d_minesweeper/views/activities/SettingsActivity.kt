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
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDBHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IUiIntValueSelector
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity :
    AppCompatActivity(),
    SettingsRecyclerViewAdapter.ISettingsRVEventListener
{
    private val settingsDBHelper: SettingsDBHelper = SettingsDBHelper(DBHelper(this))


    private fun getDbSettingsList() =
        settingsDBHelper.getSettingsList().toMutableList()

    private val settingsRecyclerViewAdapter: SettingsRecyclerViewAdapter by lazy {
        SettingsRecyclerViewAdapter(getDbSettingsList(), this)
    }

    private val controls: Map<String, IUiIntValueSelector> by lazy {
        mapOf<String, IUiIntValueSelector>(
            SettingsData.xCountName to ivs_xCount,
            SettingsData.yCountName to ivs_yCount,
            SettingsData.zCountName to ivs_zCount,
            SettingsData.bombsPercentageName to ivs_bombsPercentage
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val borders = SettingsDataHelper.borders
        val initMyIntEdit = { e: IUiIntValueSelector, name: String ->
            e.name = name
            val lR = borders[name]!!
            e.minValue = lR.first
            e.maxValue = lR.last
        }

        controls.map { initMyIntEdit(it.value, it.key) }

        val loadedSettingsData =
            ApplicationController.instance.saveController.tryToLoad<SettingsData>(
                SaveTypes.GameSettingsJson
            )?: SettingsData()


        loadedSettingsData.getMap().map { (k, v) ->
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

            val settingsData = SettingsData(m)
            settingsDBHelper.insertIfNotPresent(
                settingsData
            )

            ApplicationController.instance.saveController.save(
                SaveTypes.GameSettingsJson,
                settingsData
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
