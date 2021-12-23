package com.surovtsev.settingsscreen.viewmodel.helpers

import androidx.lifecycle.MutableLiveData
import com.surovtsev.core.room.entities.Settings


class SettingsUIInfo {
    private val dimBorders = 3..25
    private val bombsPercentageBorders = 10..40

    private fun updateDimensions(
        settingsData: Settings.SettingsData,
        dimensions: Settings.SettingsData.Dimensions
    ): Settings.SettingsData {
        return settingsData.copy(
            dimensions = dimensions
        )
    }

    val info = arrayOf(
        SettingUIControl(
            "x",
            dimBorders,
            MutableLiveData<Int>(0),
            { it.dimensions.x },
            { settingsData, newValue ->
                updateDimensions(
                    settingsData,
                    settingsData.dimensions.copy(x = newValue)
                )
            }
        ),
        SettingUIControl(
            "y",
            dimBorders,
            MutableLiveData<Int>(0),
            { it.dimensions.y },
            { settingsData, newValue ->
                updateDimensions(
                    settingsData,
                    settingsData.dimensions.copy(y = newValue)
                )
            }
        ),
        SettingUIControl(
            "z",
            dimBorders,
            MutableLiveData<Int>(0),
            { it.dimensions.z },
            { settingsData, newValue ->
                updateDimensions(
                    settingsData,
                    settingsData.dimensions.copy(z = newValue)
                )
            }
        ),
        SettingUIControl(
            "bombs %",
            bombsPercentageBorders,
            MutableLiveData<Int>(0),
            { it.bombsPercentage },
            { settingsData, newValue ->
                settingsData.copy( bombsPercentage = newValue )
            }
        )
    )
}