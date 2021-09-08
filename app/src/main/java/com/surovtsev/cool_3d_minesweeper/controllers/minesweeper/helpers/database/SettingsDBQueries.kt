package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

import android.database.sqlite.SQLiteDatabase
import android.util.Log

class SettingsDBQueries(
    private val dbHelper: IDBHelper
)
{
    fun insertDefaultValues(db: SQLiteDatabase) {
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

    fun isPresent(settingsData: SettingsData): Boolean {
        return dbHelper.actionWithDB { db ->
            getIsPresentAction(settingsData)(db)
        }
    }

    fun delete(settingsData: SettingsData) {
        dbHelper.actionWithDB { db ->
            getDeleteAction(settingsData)(db)
        }
    }

    private fun getDeleteAction(settingsData: SettingsData): DatabaseAction<Unit> = { db ->
        val id = getIdCalculationAction(settingsData)(db)
        if (id == null) {
            Log.e("Minesweeper", "settings database can not delete value")
        } else {
            db.execSQL(
                "DELETE FROM ${DBConfig.settingsTableName} where ${SettingsData.settingsIdColumnName} = $id",
            )
        }
    }

    private fun getIdCalculationAction(settingsData: SettingsData): DatabaseAction<Int?> = { db ->
        val c = db.rawQuery(
            "SELECT ${SettingsData.settingsIdColumnName} " +
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

    fun insertIfNotPresent(settingsData: SettingsData): Int =
        dbHelper.actionWithDB { db ->
            getIdAction(settingsData)(db)?: getInsertAction(settingsData)(db)
        }

    private fun getInsertAction(settingsData: SettingsData): DatabaseAction<Int> = { db ->
        db.insert(
            DBConfig.settingsTableName,
            null,
            settingsData.getContentValues()
        ).toInt()
    }

    fun getId(settingsData: SettingsData): Int? =
        dbHelper.actionWithDB { db ->
            getIdAction(settingsData) (db)
        }

    private fun getIsPresentAction(settingsData: SettingsData): DatabaseAction<Boolean> = { db ->
        val c = db.rawQuery(
            "SELECT COUNT(*) " +
                    "FROM ${DBConfig.settingsTableName} " +
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
            "SELECT ${SettingsData.settingsIdColumnName} " +
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

    fun getSettingsList(): List<DataWithId<SettingsData>> {
        val res = mutableListOf<DataWithId<SettingsData>>()

        dbHelper.actionWithDB { db ->
            val c = db.query(
                DBConfig.settingsTableName,
                null, null,
                null, null,
                null, null
            )

            if (c.moveToFirst()) {
                val idColumnIndex = c.getColumnIndex(SettingsData.settingsIdColumnName)
                val xCountColIndex = c.getColumnIndex(SettingsData.xCountColumnName)
                val yCountColIndex = c.getColumnIndex(SettingsData.yCountColumnName)
                val zCountColIndex = c.getColumnIndex(SettingsData.zCountColumnName)
                val bombsPercentageColIndex =
                    c.getColumnIndex(SettingsData.bombsPercentageColumnName)

                do {
                    val settingsData  =
                        SettingsData(
                            c.getInt(xCountColIndex),
                            c.getInt(yCountColIndex),
                            c.getInt(zCountColIndex),
                            c.getInt(bombsPercentageColIndex)
                        )
                    val settingDataWithId = DataWithId<SettingsData>(
                        c.getInt(idColumnIndex),
                        settingsData
                    )
                    res.add(settingDataWithId)
                } while (c.moveToNext())
            }

            c.close()
        }

        return res
    }
}