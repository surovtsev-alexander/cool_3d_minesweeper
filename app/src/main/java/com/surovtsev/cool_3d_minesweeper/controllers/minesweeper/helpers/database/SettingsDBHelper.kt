package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.Settings
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings

typealias DatabaseAction<T> = (db: SQLiteDatabase) -> T

class SettingsDBHelper(
    context: Context
): SQLiteOpenHelper(
    context,
    DBConfig.dataBaseName,
    null,
    DBConfig.dataBaseVersion
) {
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("TEST+++", "SettingsDBHelper onCreate")
        if (db == null) {
            Log.e("Minesweeper",  "Can not create Settings database.")
            return
        }

        db.execSQL(
            "CREATE TABLE ${DBConfig.settingsTableName} (" +
                    "${SettingsData.idColumnName} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${SettingsData.xCountColumnName} INTEGER," +
                    "${SettingsData.yCountColumnName} INTEGER," +
                    "${SettingsData.zCountColumnName} INTEGER," +
                    "${SettingsData.bombsPercentageColumnName} INTEGER" +
                    ");"
        )

        val defaultSettings = arrayOf(
            SettingsData(12, 20),
            SettingsData(10, 20),
            SettingsData(8, 16),
            SettingsData(5, 12)
        )

        defaultSettings.map {
            getInsertAction(it)(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("TEST+++", "SettingsDBHelper onUpgrade")
        TODO("Not yet implemented")
    }


    fun isPresent(settingsData: SettingsData): Boolean {
        return actionWithDB { db ->
            getIsPresentAction(settingsData)(db)
        }
    }

    fun delete(settingsData: SettingsData) {
        actionWithDB { db ->
            getDeleteAction(settingsData)(db)
        }
    }

    private fun getDeleteAction(settingsData: SettingsData): DatabaseAction<Unit> = { db ->
        val id = getIdCalculationAction(settingsData)(db)
        if (id == null) {
            Log.e("Minesweeper", "settings database can not delete value")
        } else {
            db.execSQL(
                "DELETE FROM ${DBConfig.settingsTableName} where id = $id",
            )
        }
    }

    private fun getIdCalculationAction(settingsData: SettingsData): DatabaseAction<Int?> = { db ->
        val c = db.rawQuery(
            "SELECT id " +
                    "from ${DBConfig.settingsTableName} " +
                    "where ${settingsData.getEqualsForWhereString()}",
            null)
        val res = if (c.moveToFirst()) {
            c.getInt(0)
        } else {
            null
        }
        c.close()
        res
    }

    fun insertIfNotPresent(settingsData: SettingsData) {
        actionWithDB { db ->
            val isPresent = getIsPresentAction(settingsData)(db)
            if (!isPresent) {
                getInsertAction(settingsData)(db)
            }
        }
    }

    private fun getInsertAction(settingsData: SettingsData): DatabaseAction<Unit> = { db ->
        db.insert(
            DBConfig.settingsTableName,
            null,
            settingsData.getContentValues()
        )
    }

    fun getId(settingsData: SettingsData): Int? =
        actionWithDB { db ->
            getIdAction(settingsData) (db)
        }

    private fun getIsPresentAction(settingsData: SettingsData): DatabaseAction<Boolean> = { db ->
        val c = db.rawQuery(
            "SELECT COUNT(*) " +
                    "FROM ${DBConfig}.settingsTableName " +
                    "WHERE ${SettingsData.xCountColumnName} = ${settingsData.xCount} " +
                    "and ${SettingsData.yCountColumnName} = ${settingsData.yCount} " +
                    "and ${SettingsData.zCountColumnName} = ${settingsData.zCount} " +
                    "and ${SettingsData.bombsPercentageColumnName} = ${settingsData.bombsPercentage}",
            null
        )

        val r = c.moveToFirst()

        val res = if (!r) {
            Log.e("Minesweeper", "Error while checking presence data in database.")
            false
        } else {
            c.getInt(0) > 0
        }

        c.close()

        res
    }

    private fun getIdAction(settingsData: SettingsData): DatabaseAction<Int?> = { db ->
        val c = db.rawQuery(
            "SELECT ${SettingsData.idColumnName} " +
                    "FROM ${DBConfig.settingsTableName} " +
                    "WHERE ${SettingsData.xCountColumnName} = ${settingsData.xCount} " +
                    "and ${SettingsData.yCountColumnName} = ${settingsData.yCount} " +
                    "and ${SettingsData.zCountColumnName} = ${settingsData.zCount} " +
                    "and ${SettingsData.bombsPercentageColumnName} = ${settingsData.bombsPercentage}",
            null
        )

        val r = c.moveToFirst()

        val res = if (!r) {
            null
        } else {
            c.getInt(0)
        }

        c.close()

        res
    }

    private fun <T> actionWithDB(f: DatabaseAction<T>): T {
        val db = writableDatabase
        val res = f(db)
        db.close()

        return res
    }

    fun getSettingsList(): List<SettingsData> {
        val res = mutableListOf<SettingsData>()

        actionWithDB { db ->
            val c = db.query(
                DBConfig.settingsTableName,
                null, null,
                null, null,
                null, null
            )

            if (c.moveToFirst()) {
                val xCountColIndex = c.getColumnIndex(SettingsData.xCountColumnName)
                val yCountColIndex = c.getColumnIndex(SettingsData.yCountColumnName)
                val zCountColIndex = c.getColumnIndex(SettingsData.zCountColumnName)
                val bombsPercentageColIndex =
                    c.getColumnIndex(SettingsData.bombsPercentageColumnName)

                do {
                    res.add(
                        SettingsData(
                            c.getInt(xCountColIndex),
                            c.getInt(yCountColIndex),
                            c.getInt(zCountColIndex),
                            c.getInt(bombsPercentageColIndex)
                        )
                    )
                } while (c.moveToNext())
            }

            c.close()
        }

        return res
    }
}