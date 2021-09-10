package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

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

    val borders = (
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