package com.surovtsev.cool_3d_minesweeper.models.game.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameSettings
import glm_.vec3.Vec3i

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
    }

    companion object {
        private const val tableName = "settings"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("TEST+++", "SettingsDBHelper onCreate")
        if (db == null) {
            Log.e("Minesweeper",  "Can not create Settings database/")
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
            db.insert(
                tableName,
                null,
                it.getContentValues()
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("TEST+++", "SettingsDBHelper onUpgrade")
        TODO("Not yet implemented")
    }

    fun getSettingsList(): List<SettingsData> {
        val res = mutableListOf<SettingsData>()

        val db = writableDatabase

        val c = db.query(tableName,
            null, null,
            null, null,
            null, null)

        val rowCount = c.count

        if (c.moveToFirst()) {
            val xCountColIndex = c.getColumnIndex(GameSettings.xCountColumnName)
            val yCountColIndex = c.getColumnIndex(GameSettings.yCountColumnName)
            val zCountColIndex = c.getColumnIndex(GameSettings.zCountColumnName)
            val bombsPercentageColIndex = c.getColumnIndex(GameSettings.bombsPercentageColumnName)

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
        db.close()

        return res
    }
}