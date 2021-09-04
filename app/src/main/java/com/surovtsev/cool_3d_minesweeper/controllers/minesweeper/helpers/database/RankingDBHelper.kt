package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class RankingDBHelper(
    context: Context
): SQLiteOpenHelper(
    context,
    "ranking.db",
    null,
    DBConfig.dataBaseVersion
) {
    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            Log.e("Minesweeper",  "Can not create ranking database.")
            return
        }

        val query =
            "CREATE TABLE ${DBConfig.rankingTableName} (" +
                    "${RankingData.settingsIdColumnName} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${RankingData.elapsedColumnName} INTEGER, " +
                    "${RankingData.dateTimeColumnName} VARCHAR(20), " +
                    "FOREIGN KEY (${RankingData.settingsIdColumnName}) REFERENCES  ${DBConfig.settingsTableName} (${SettingsData.idColumnName}))"

        Log.d("TEST+++", "RankingDBHelper onCreate str:\n$query")
        db.execSQL(
            query
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    private fun <T> actionWithDB(f: DatabaseAction<T>): T {
        val db = writableDatabase
        val res = f(db)
        db.close()

        return res
    }

    fun insert(rankingData: RankingData) {
        actionWithDB { db ->
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

        actionWithDB { db ->
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