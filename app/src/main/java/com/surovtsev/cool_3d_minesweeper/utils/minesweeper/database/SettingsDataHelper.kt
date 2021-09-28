package com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database

import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData

typealias Borders = Map<String, IntRange>

/* TODO: move to dagger */
object SettingsDataHelper {
    val paramNames = arrayOf(
        SettingsData.xCountName,
        SettingsData.yCountName,
        SettingsData.zCountName,
        SettingsData.bombsPercentageName
    )

    private const val dimParamsCount = 3

    private val dimBorders = 3 .. 25
    private val bombsPercentageBorders = 10 .. 90

    val borders: Borders = (
            paramNames.take(dimParamsCount).map {
                it to dimBorders
            } + paramNames.drop(dimParamsCount).map {
                it to bombsPercentageBorders
            }).toMap()

    fun <T> getInvalidControls(values: Map<String, T>, getter: (T) -> Int) =
        values.filter { (k, v) ->
            getter(v) !in borders[k]!!
        }
}