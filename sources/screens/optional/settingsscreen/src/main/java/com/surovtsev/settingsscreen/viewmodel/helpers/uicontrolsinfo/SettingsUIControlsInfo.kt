package com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo

import com.surovtsev.core.room.entities.Settings
import kotlinx.coroutines.flow.MutableStateFlow


class SettingsUIControlsInfo {
    private val dimensionCellCount = 3..25
    private val bombsPercentageBorders = 10..40

    private fun updateDimensions(
        settingsData: Settings.SettingsData,
        dimensions: Settings.SettingsData.Dimensions
    ): Settings.SettingsData {
        return settingsData.copy(
            dimensions = dimensions
        )
    }

    val info = listOf(
        SettingUIControl(
            "x",
            dimensionCellCount,
            MutableStateFlow(0),
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
            dimensionCellCount,
            MutableStateFlow(0),
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
            dimensionCellCount,
            MutableStateFlow(0),
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
            MutableStateFlow(0),
            { it.bombsPercentage },
            { settingsData, newValue ->
                settingsData.copy( bombsPercentage = newValue )
            }
        )
    )
}