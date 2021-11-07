package com.surovtsev.cool3dminesweeper.models.room.dao

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings

@Dao
interface SettingsDao  {

    companion object {
        const val settingsTableName = Settings.TableName.name
    }

    @Query("SELECT * FROM $settingsTableName")
    fun getAll(): List<Settings>

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
}