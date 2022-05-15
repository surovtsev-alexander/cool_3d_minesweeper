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