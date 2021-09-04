package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.ContentValues
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import glm_.vec3.Vec3i

data class SettingsData(
    val xCount: Int,
    val yCount: Int,
    val zCount: Int,
    val bombsPercentage: Int
) {
    companion object {
        const val idColumnName = "id"
        val xCountColumnName = DBConfig.glueWithUnderscores(GameSettings.xCount)
        val yCountColumnName = DBConfig.glueWithUnderscores(GameSettings.yCount)
        val zCountColumnName = DBConfig.glueWithUnderscores(GameSettings.zCount)
        val bombsPercentageColumnName = DBConfig.glueWithUnderscores(GameSettings.bombsPercentage)
    }

    constructor(count: Int, bombsPercentage: Int): this(
        count, count, count, bombsPercentage
    )

    constructor(values: Map<String, Int>): this(
        values[GameSettings.xCount]!!,
        values[GameSettings.yCount]!!,
        values[GameSettings.zCount]!!,
        values[GameSettings.bombsPercentage]!!
    )

    fun getContentValues() = ContentValues().apply {
        put(xCountColumnName, xCount)
        put(yCountColumnName, yCount)
        put(zCountColumnName, zCount)
        put(bombsPercentageColumnName, bombsPercentage)
    }

    fun getCounts() = Vec3i(xCount, yCount, zCount)

    fun getMap() = mapOf<String, Int>(
        GameSettings.xCount to xCount,
        GameSettings.yCount to yCount,
        GameSettings.zCount to zCount,
        GameSettings.bombsPercentage to bombsPercentage
    )

    fun getEqualsForWhereString(): String =
        "${xCountColumnName} = ${xCount} " +
                "and ${yCountColumnName} = ${yCount} " +
                "and ${zCountColumnName} = ${zCount} " +
                "and ${bombsPercentageColumnName} = ${bombsPercentage}"
}
