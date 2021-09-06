package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.ContentValues
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettingsMap
import glm_.vec3.Vec3i

data class SettingsData(
    val xCount: Int,
    val yCount: Int,
    val zCount: Int,
    val bombsPercentage: Int
) {
    companion object {
        const val settingsIdName = "settings id"
        val settingsIdColumnName = DBConfig.glueWithUnderscores(settingsIdName)
        val xCountColumnName = DBConfig.glueWithUnderscores(GameSettingsMap.xCount)
        val yCountColumnName = DBConfig.glueWithUnderscores(GameSettingsMap.yCount)
        val zCountColumnName = DBConfig.glueWithUnderscores(GameSettingsMap.zCount)
        val bombsPercentageColumnName = DBConfig.glueWithUnderscores(GameSettingsMap.bombsPercentage)
    }

    constructor(count: Int, bombsPercentage: Int): this(
        count, count, count, bombsPercentage
    )

    constructor(values: Map<String, Int>): this(
        values[GameSettingsMap.xCount]!!,
        values[GameSettingsMap.yCount]!!,
        values[GameSettingsMap.zCount]!!,
        values[GameSettingsMap.bombsPercentage]!!
    )

    fun getContentValues() = ContentValues().apply {
        put(xCountColumnName, xCount)
        put(yCountColumnName, yCount)
        put(zCountColumnName, zCount)
        put(bombsPercentageColumnName, bombsPercentage)
    }

    fun getCounts() = Vec3i(xCount, yCount, zCount)

    fun getMap() = mapOf<String, Int>(
        GameSettingsMap.xCount to xCount,
        GameSettingsMap.yCount to yCount,
        GameSettingsMap.zCount to zCount,
        GameSettingsMap.bombsPercentage to bombsPercentage
    )

    fun getGameSettingsMap() = GameSettingsMap(getMap())

    fun getEqualsForWhereString(): String =
        "${xCountColumnName} = ${xCount} " +
                "and ${yCountColumnName} = ${yCount} " +
                "and ${zCountColumnName} = ${zCount} " +
                "and ${bombsPercentageColumnName} = ${bombsPercentage}"
}
