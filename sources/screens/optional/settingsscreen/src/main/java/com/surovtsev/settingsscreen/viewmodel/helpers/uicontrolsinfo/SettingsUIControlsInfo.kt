/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo

import com.surovtsev.core.room.entities.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine


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

    fun updateInfo(
        settingsData: Settings.SettingsData
    ) {
        info.map {
            it.sliderPositionMutableStateFlow.value = it.valueCalculator(settingsData)
        }
    }

    fun getSettingsData(): Settings.SettingsData {
        var res = Settings.SettingsData()

        info.map {
            res = it.updateSettingsData(res)
        }
        return res
    }

    private val xUIInfo = SettingUIControl(
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
    )

    val yUIInfo = SettingUIControl(
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
    )

    val zUIInfo = SettingUIControl(
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
    )

    val bombsPercentageUIInfo = SettingUIControl(
        "bombs %",
        bombsPercentageBorders,
        MutableStateFlow(0),
        { it.bombsPercentage },
        { settingsData, newValue ->
            settingsData.copy(bombsPercentage = newValue)
        }
    )

    val info = listOf(
        xUIInfo,
        yUIInfo,
        zUIInfo,
        bombsPercentageUIInfo
    )

    val settingsDataFlow = combine(
        xUIInfo.sliderPositionMutableStateFlow,
        yUIInfo.sliderPositionMutableStateFlow,
        zUIInfo.sliderPositionMutableStateFlow,
        bombsPercentageUIInfo.sliderPositionMutableStateFlow
    ) { x, y, z, bombsPercentage ->
        Settings.SettingsData(
            Settings.SettingsData.Dimensions(
                x, y, z,
            ),
            bombsPercentage
        )
    }
}