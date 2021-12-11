package com.surovtsev.core.database

import com.surovtsev.core.room.entities.Settings

typealias Borders = Map<String, IntRange>

object SettingsDataHelper {
    val paramNames = arrayOf(
        Settings.SettingsData.Dimensions.ColumnNames.xCount,
        Settings.SettingsData.Dimensions.ColumnNames.yCount,
        Settings.SettingsData.Dimensions.ColumnNames.zCount,
        Settings.SettingsData.ColumnNames.bombsPercentage
    )

    const val DimParamsCount = "dimParamsCount"
    const val DimBorders = "dimBorders"
    const val BombsPercentageBorders = "bombsPercentageBorders"
}