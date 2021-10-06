package com.surovtsev.cool3dminesweeper.utils.minesweeper.database

import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData

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