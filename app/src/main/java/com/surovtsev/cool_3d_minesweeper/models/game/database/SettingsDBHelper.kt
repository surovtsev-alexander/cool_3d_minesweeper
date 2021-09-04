package com.surovtsev.cool_3d_minesweeper.models.game.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import glm_.vec3.Vec3i

typealias DatabaseAction<T> = (db: SQLiteDatabase) -> T

class SettingsDBHelper(
    context: Context
): SQLiteOpenHelper(context, "minesweeperDB", null, 1) {
    data class SettingsData(
        val xCount: Int,
        val yCount: Int,
        val zCount: Int,
        val bombsPercentage: Int
    ) {
        constructor(count: Int, bombsPercentage: Int): this(
            count, count, count, bombsPercentage
        )

        constructor(values: Map<String, Int>): this(
            values[GameSettings.xCount]!!,
            values[GameSettings.yCount]!!,
            values[GameSettings.zCount]!!,
            values[GameSettings.bombsPercentage]!!
        )

        fun getContentValues() = ContentValues().apply {
            put(GameSettings.xCountColumnName, xCount)
            put(GameSettings.yCountColumnName, yCount)
            put(GameSettings.zCountColumnName, zCount)
            put(GameSettings.bombsPercentageColumnName, bombsPercentage)
        }

        fun getCounts() = Vec3i(xCount, yCount, zCount)

        fun getMap() = mapOf<String, Int>(
            GameSettings.xCount to xCount,
            GameSettings.yCount to yCount,
            GameSettings.zCount to zCount,
            GameSettings.bombsPercentage to bombsPercentage
        )

        fun getEqualsForWhereString(): String =
            "${GameSettings.xCountColumnName} = ${xCount} " +
                    "and ${GameSettings.yCountColumnName} = ${yCount} " +
                    "and ${GameSettings.zCountColumnName} = ${zCount} " +
                    "and ${GameSettings.bombsPercentageColumnName} = ${bombsPercentage}"
    }

    companion object {
        private const val tableName = "settings"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("TEST+++", "SettingsDBHelper onCreate")
        if (db == null) {
            Log.e("Minesweeper",  "Can not create Settings database.")
            return
        }

        db.execSQL(
            "create table $tableName (" +
                    "id integer PRIMARY KEY AUTOINCREMENT," +
                    "${GameSettings.xCountColumnName} INTEGER," +
                    "${GameSettings.yCountColumnName} INTEGER," +
                    "${GameSettings.zCountColumnName} INTEGER," +
                    "${GameSettings.bombsPercentageColumnName} INTEGER" +
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
                "DELETE FROM $tableName where id = $id",
            )
        }
    }

    private fun getIdCalculationAction(settingsData: SettingsData): DatabaseAction<Int?> = { db ->
        val c = db.rawQuery(
            "SELECT id " +
                    "from $tableName " +
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
            tableName,
            null,
            settingsData.getContentValues()
        )
    }

    private fun getIsPresentAction(settingsData: SettingsData): DatabaseAction<Boolean> = { db ->
        val c = db.rawQuery(
            "SELECT COUNT(*) " +
                    "FROM $tableName " +
                    "WHERE ${GameSettings.xCountColumnName} = ${settingsData.xCount} " +
                    "and ${GameSettings.yCountColumnName} = ${settingsData.yCount} " +
                    "and ${GameSettings.zCountColumnName} = ${settingsData.zCount} " +
                    "and ${GameSettings.bombsPercentageColumnName} = ${settingsData.bombsPercentage}",
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
                tableName,
                null, null,
                null, null,
                null, null
            )

            if (c.moveToFirst()) {
                val xCountColIndex = c.getColumnIndex(GameSettings.xCountColumnName)
                val yCountColIndex = c.getColumnIndex(GameSettings.yCountColumnName)
                val zCountColIndex = c.getColumnIndex(GameSettings.zCountColumnName)
                val bombsPercentageColIndex =
                    c.getColumnIndex(GameSettings.bombsPercentageColumnName)

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