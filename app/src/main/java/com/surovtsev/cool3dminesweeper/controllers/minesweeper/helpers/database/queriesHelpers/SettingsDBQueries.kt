package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DatabaseAction
import com.surovtsev.cool3dminesweeper.models.game.database.DataWithId
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.utils.constants.minesweeper.database.DBConfig
import com.surovtsev.cool3dminesweeper.utils.listhelper.ListHelper.joinToCSVLine

class SettingsDBQueries(
    private val dbHelper: DBHelper
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

    @Suppress("unused")
    fun isPresent(settingsData: SettingsData): Boolean {
        return dbHelper.actionWithDB { db ->
            getIsPresentAction(settingsData)(db)
        }
    }

    @Suppress("unused")
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
            getDeleteAction(id)(db)
        }
    }

    fun delete(settingsId: Int) {
        dbHelper.actionWithDB { db ->
            getDeleteAction(settingsId)(db)
        }
    }

    private fun getDeleteAction(settingsId: Int): DatabaseAction<Unit> = { db ->
        db.execSQL(
            "DELETE FROM ${DBConfig.settingsTableName} where ${SettingsData.settingsIdColumnName} = $settingsId",
        )
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
                    val settingDataWithId = DataWithId(
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

    fun getTableStringData(): String {
        val res = StringBuilder()

        dbHelper.actionWithDB { db ->
            val query = db.query(
                DBConfig.settingsTableName,
                null, null,
                null, null,
                null, null
            )

            if (query.moveToFirst()) {
                val columnNames = SettingsData.columnNames
                val tableColumnNames = columnNames.joinToCSVLine()
                res.appendLine(tableColumnNames)


                val idColumnIndex = query.getColumnIndex(SettingsData.settingsIdColumnName)
                val xCountColIndex = query.getColumnIndex(SettingsData.xCountColumnName)
                val yCountColIndex = query.getColumnIndex(SettingsData.yCountColumnName)
                val zCountColIndex = query.getColumnIndex(SettingsData.zCountColumnName)
                val bombsPercentageColIndex =
                    query.getColumnIndex(SettingsData.bombsPercentageColumnName)

                do {
                    val settingsData  =
                        SettingsData(
                            query.getInt(xCountColIndex),
                            query.getInt(yCountColIndex),
                            query.getInt(zCountColIndex),
                            query.getInt(bombsPercentageColIndex)
                        )
                    val settingDataWithId = DataWithId(
                        query.getInt(idColumnIndex),
                        settingsData
                    )
                    val row = listOf(
                        query.getInt(idColumnIndex),
                        query.getInt(xCountColIndex),
                        query.getInt(yCountColIndex),
                        query.getInt(zCountColIndex),
                        query.getInt(bombsPercentageColIndex)
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