package com.surovtsev.cool3dminesweeper.models.game.database

import android.content.ContentValues
import com.surovtsev.cool3dminesweeper.utils.constants.minesweeper.database.DBConfig

data class RankingData(
    val settingId: Int,
    val elapsed: Long,
    val dateTime: String
) {
    companion object {
        private const val rankingIdName = "ranking id"
        private const val settingsIdName = "settings id"
        private const val elapsedName = "elapsed"
        private const val dateTimeName = "date time"

        val rankingIdColumnName = DBConfig.glueWithUnderscores(rankingIdName)
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