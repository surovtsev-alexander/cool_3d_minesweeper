package com.surovtsev.cool3dminesweeper.models.room.entities

import androidx.room.*
import glm_.vec3.Vec3i

typealias SettingsDataFactory = () -> Settings.SettingsData

@Entity(
    indices = [
        Index(
            value = arrayOf(
                Settings.SettingsData.Dimensions.ColumnNames.xCount,
                Settings.SettingsData.Dimensions.ColumnNames.yCount,
                Settings.SettingsData.Dimensions.ColumnNames.zCount,
            ),
            unique = true
        )
    ],
    tableName = Settings.TableName.name
)
data class Settings (
    @Embedded val settingsData: SettingsData
) {
    @[PrimaryKey(autoGenerate = true) ColumnInfo(name = ColumnNames.id)] var id: Long = 0

    object TableName {
        const val name = "settings"
    }

    object ColumnNames {
        const val id = "id"
    }

    data class SettingsData(
        @Embedded val dimensions: Dimensions = Dimensions(
            Dimensions.DefaultValues.xCount,
            Dimensions.DefaultValues.yCount,
            Dimensions.DefaultValues.zCount,
        ),
        @ColumnInfo(name = ColumnNames.bombsPercentage) val bombsPercentage: Int = DefaultValues.bombsPercentage,
    ) {
        object ColumnNames {
            const val bombsPercentage = "bombs_percentage"
        }

        object DefaultValues {
            const val bombsPercentage = 20
        }

        data class Dimensions(
            @ColumnInfo(name = ColumnNames.xCount) val x: Int,
            @ColumnInfo(name = ColumnNames.yCount) val y: Int,
            @ColumnInfo(name = ColumnNames.zCount) val z: Int
        ) {
            fun toVec3i(): Vec3i {
                return Vec3i(
                    x, y, z
                )
            }

            object ColumnNames {
                const val xCount = "x_count"
                const val yCount = "y_count"
                const val zCount = "z_count"

            }

            object DefaultValues {
                const val xCount = 12
                const val yCount = 12
                const val zCount = 12
            }
        }
    }
}