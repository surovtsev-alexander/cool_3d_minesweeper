package com.surovtsev.settings.viewmodel.helpers

import com.surovtsev.core.room.entities.Settings

object SettingsUIInfo {
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
            { it.bombsPercentage },
            { settingsData, newValue ->
                settingsData.copy( bombsPercentage = newValue )
            }
        )
    )
}