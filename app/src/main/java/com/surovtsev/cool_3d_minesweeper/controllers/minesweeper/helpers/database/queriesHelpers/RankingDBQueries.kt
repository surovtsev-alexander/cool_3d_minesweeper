package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers

import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.utils.constants.minesweeper.database.DBConfig
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.DatabaseAction
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.IDBHelper
import javax.inject.Inject

class RankingDBQueries (
    private val dBHelper: IDBHelper
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