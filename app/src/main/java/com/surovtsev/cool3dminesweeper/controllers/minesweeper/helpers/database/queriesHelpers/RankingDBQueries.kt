package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DatabaseAction
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.utils.constants.minesweeper.database.DBConfig
import com.surovtsev.cool3dminesweeper.utils.listhelper.ListHelper.joinToCSVLine
import java.lang.StringBuilder

class RankingDBQueries (
    private val dBHelper: DBHelper
) {

    fun insert(rankingData: RankingData) {
        dBHelper.actionWithDB { db ->
            getInsertAction(rankingData)(db)
        }
    }

    private fun getInsertAction(rankingData: RankingData): DatabaseAction<Unit> = { db ->
        db.insert(
            DBConfig.rankingTableName,
            null,
            rankingData.getContentValues()
        )
    }

    fun getRankingList(): List<RankingData> {
        val res = mutableListOf<RankingData>()

        dBHelper.actionWithDB { db ->
            val query = db.query(
                DBConfig.rankingTableName,
                null, null,
                null, null,
                null, null
            )

            if (query.moveToFirst()) {
                val settingsIdColIndex = query.getColumnIndex(RankingData.settingsIdColumnName)
                val elapsedColIndex = query.getColumnIndex(RankingData.elapsedColumnName)
                val dateTimeColIndex = query.getColumnIndex(RankingData.dateTimeColumnName)

                do {
                    res.add(
                        RankingData(
                            query.getInt(settingsIdColIndex),
                            query.getLong(elapsedColIndex),
                            query.getString(dateTimeColIndex),
                        )
                    )
                } while (query.moveToNext())
            }

            query.close()
        }

        return res
    }

    fun getTableStringsData(): String {
        val res = StringBuilder()

        dBHelper.actionWithDB { db ->
            val query = db.query(
                DBConfig.rankingTableName,
                null, null,
                null, null,
                null, null
            )

            val columnNames = RankingData.columnNames

            val tableColumnNames = columnNames.joinToCSVLine()
            res.appendLine(tableColumnNames)

            if (query.moveToFirst()) {
                val columnIndexes = columnNames.map {
                    it to query.getColumnIndex(it)
                }.toMap()

                do {
                    val row = listOf(
                        query.getInt(columnIndexes[RankingData.rankingIdColumnName]!!),
                        query.getInt(columnIndexes[RankingData.settingsIdColumnName]!!),
                        query.getInt(columnIndexes[RankingData.elapsedColumnName]!!),
                        query.getString(columnIndexes[RankingData.dateTimeColumnName]!!)
                    )
                    val rowString = row.joinToCSVLine()
                    res.appendLine(rowString)

                } while (query.moveToNext())
            }

            query.close()
        }

        return res.toString()
    }
}