/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.core.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.surovtsev.core.room.entities.Settings

typealias SettingsList = List<Settings>

@Dao
interface SettingsDao  {

    companion object {
        const val settingsTableName = Settings.TableName.name

        private val dataToPrepopulate = listOf(
            12 to 20,
            10 to 20,
            8 to 16,
            5 to 12,
            12 to 30,
            12 to 25,
            10 to 18,
        )
    }

    @Query("SELECT * FROM $settingsTableName")
    fun getAll(): SettingsList

    @Query("SELECT COUNT(*) FROM $settingsTableName")
    fun getCount(): Int

    @Insert
    fun insert(settings: Settings)

    @Query("DELETE FROM $settingsTableName WHERE ${Settings.ColumnNames.id} = :settingsId")
    fun delete(settingsId: Long)

    fun getOrCreate(
        settingsData: Settings.SettingsData
    ): Settings {
        return getBySettingsData(settingsData) ?: run {
            insert(Settings(settingsData))
            getBySettingsData(settingsData)!!
        }
    }

    fun getBySettingsData(
        settingsData: Settings.SettingsData
    ): Settings? {
        val dimensions = settingsData.dimensions

        return getBySettingsData(
            dimensions.x,
            dimensions.y,
            dimensions.z,
            settingsData.bombsPercentage
        )
    }

    @Query(
        "SELECT *\n" +
             "from  $settingsTableName\n" +
             "where ${Settings.SettingsData.Dimensions.ColumnNames.xCount} = :xCount\n" +
             "AND   ${Settings.SettingsData.Dimensions.ColumnNames.yCount} = :yCount\n" +
             "AND   ${Settings.SettingsData.Dimensions.ColumnNames.zCount} = :zCount\n" +
             "AND   ${Settings.SettingsData.ColumnNames.bombsPercentage} = :bombsPercentage\n")
    fun getBySettingsData(
        xCount: Int,
        yCount: Int,
        zCount: Int,
        bombsPercentage: Int,
    ): Settings?

    fun prepopulate() {
        dataToPrepopulate.forEach {
            this.insert(
                Settings(
                    Settings.SettingsData(
                        it.first,
                        it.second
                    )
                )
            )
        }
    }
}