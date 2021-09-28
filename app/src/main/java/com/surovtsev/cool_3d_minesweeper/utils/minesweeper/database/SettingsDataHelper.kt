package com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database

import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData

typealias Borders = Map<String, IntRange>

object SettingsDataHelper {
    val paramNames = arrayOf(
        SettingsData.xCountName,
        SettingsData.yCountName,
        SettingsData.zCountName,
        SettingsData.bombsPercentageName
    )

    const val DimParamsCount = "dimParamsCount"
    const val DimBorders = "dimBorders"
    const val BombsPercentageBorders = "bombsPercentageBorders"
}