package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.constants.minesweeper.database.DBConfig
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.DatabaseAction
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database.IDBHelper

open class DBHelper constructor(
    context: Context
): SQLiteOpenHelper(
  context,
  DBConfig.dataBaseName,
  null,
  DBConfig.dataBaseVersion
), IDBHelper {

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)

        if (db != null && !db.isReadOnly) {
            // Enable foreign key constraints
            db.setForeignKeyConstraintsEnabled(true)
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            Log.e("Minesweeper", "Can not create ranking databases.")
            return
        }

        run {
            db.execSQL(
                "CREATE TABLE ${DBConfig.settingsTableName} (" +
                        "${SettingsData.settingsIdColumnName} INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "${SettingsData.xCountColumnName} INTEGER," +
                        "${SettingsData.yCountColumnName} INTEGER," +
                        "${SettingsData.zCountColumnName} INTEGER," +
                        "${SettingsData.bombsPercentageColumnName} INTEGER" +
                        ");"
            )

            val settingsDBHelper = SettingsDBQueries(this)
            settingsDBHelper.insertDefaultValues(db)
        }

        run {
            val query =
                "CREATE TABLE ${DBConfig.rankingTableName} (" +
                        "${RankingData.rankingIdColumnName} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "${RankingData.settingsIdColumnName}, " +
                        "${RankingData.elapsedColumnName} INTEGER, " +
                        "${RankingData.dateTimeColumnName} VARCHAR(30), " +
                        "CONSTRAINT fk_ranking_settings_id " +
                        "FOREIGN KEY (${RankingData.settingsIdColumnName})" +
                        "REFERENCES  ${DBConfig.settingsTableName} (${SettingsData.settingsIdColumnName}) " +
                        "ON DELETE CASCADE " +
                        ")"

            Log.d("TEST+++", "RankingDBQueries onCreate str:\n$query")
            db.execSQL(
                query
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    override fun <T> actionWithDB(f: DatabaseAction<T>): T {
        val db = writableDatabase
        val res = f(db)
        db.close()

        return res
    }
}