package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.ContentValues

data class RankingData(
    val settingId: Int,
    val elapsed: Long,
    val dateTime: String
) {
    companion object {
        const val settingsIdName = "settings id"
        const val elapsedName = "elapsed"
        const val dateTimeName = "date time"

        val settingsIdColumnName = DBConfig.glueWithUnderscores(settingsIdName)
        val elapsedColumnName = DBConfig.glueWithUnderscores(elapsedName)
        val dateTimeColumnName = DBConfig.glueWithUnderscores(dateTimeName)
    }

    fun getContentValues() = ContentValues().apply {
        put(settingsIdColumnName, settingId)
        put(elapsedColumnName, elapsed)
        put(dateTimeColumnName, dateTime)
    }
}