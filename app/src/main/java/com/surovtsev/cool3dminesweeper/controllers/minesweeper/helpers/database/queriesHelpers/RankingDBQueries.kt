package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DatabaseAction
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.utils.constants.minesweeper.database.DBConfig

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
            val c = db.query(
                DBConfig.rankingTableName,
                null, null,
                null, null,
                null, null
            )

            if (c.moveToFirst()) {
                val settingsIdColIndex = c.getColumnIndex(RankingData.settingsIdColumnName)
                val elapsedColIndex = c.getColumnIndex(RankingData.elapsedColumnName)
                val dateTimeColIndex = c.getColumnIndex(RankingData.dateTimeColumnName)

                do {
                    res.add(
                        RankingData(
                            c.getInt(settingsIdColIndex),
                            c.getLong(elapsedColIndex),
                            c.getString(dateTimeColIndex),
                        )
                    )
                } while (c.moveToNext())
            }

            c.close()
        }

        return res
    }
}