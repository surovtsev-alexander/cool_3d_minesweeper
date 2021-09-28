package com.surovtsev.cool_3d_minesweeper.models.game.database

import android.content.ContentValues
import com.surovtsev.cool_3d_minesweeper.utils.constants.minesweeper.database.DBConfig
import glm_.vec3.Vec3i

data class SettingsData(
    val xCount: Int,
    val yCount: Int,
    val zCount: Int,
    val bombsPercentage: Int
) {
    companion object {
        private const val settingsIdName = "settings id"
        const val xCountName = "x count"
        const val yCountName = "y count"
        const val zCountName = "z count"
        const val bombsPercentageName = "bombs percentage"

        val settingsIdColumnName = DBConfig.glueWithUnderscores(settingsIdName)
        val xCountColumnName = DBConfig.glueWithUnderscores(xCountName)
        val yCountColumnName = DBConfig.glueWithUnderscores(yCountName)
        val zCountColumnName = DBConfig.glueWithUnderscores(zCountName)
        val bombsPercentageColumnName = DBConfig.glueWithUnderscores(bombsPercentageName)

        const val xCountDefaultValue = 12
        const val yCountDefaultValue = 12
        const val zCountDefaultValue = 12
        const val bombsPercentageDefaultValue = 20
    }

    constructor() : this(
        xCountDefaultValue,
        yCountDefaultValue,
        zCountDefaultValue,
        bombsPercentageDefaultValue
    )

    constructor(count: Int, bombsPercentage: Int): this(
        count, count, count, bombsPercentage
    )


    constructor(values: Map<String, Int>): this(
        values[xCountName]!!,
        values[yCountName]!!,
        values[zCountName]!!,
        values[bombsPercentageName]!!
    )

    fun getContentValues() = ContentValues().apply {
        put(xCountColumnName, xCount)
        put(yCountColumnName, yCount)
        put(zCountColumnName, zCount)
        put(bombsPercentageColumnName, bombsPercentage)
    }

    fun getCounts() = Vec3i(xCount, yCount, zCount)

    @Suppress("unused")
    fun getMap() = mapOf(
        xCountName to xCount,
        yCountName to yCount,
        zCountName to zCount,
        bombsPercentageName to bombsPercentage
    )

    fun getEqualsForWhereString(): String =
        "$xCountColumnName = $xCount " +
                "and $yCountColumnName = $yCount " +
                "and $zCountColumnName = $zCount " +
                "and $bombsPercentageColumnName = $bombsPercentage"
}
