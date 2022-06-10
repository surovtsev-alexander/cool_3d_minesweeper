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


package com.surovtsev.core.room.entities

import androidx.room.*
import glm_.vec3.Vec3i


@Entity(
    indices = [
        Index(
            value = arrayOf(
                Settings.SettingsData.Dimensions.ColumnNames.xCount,
                Settings.SettingsData.Dimensions.ColumnNames.yCount,
                Settings.SettingsData.Dimensions.ColumnNames.zCount,
                Settings.SettingsData.ColumnNames.bombsPercentage
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

    constructor(
        settingsData: SettingsData,
        id: Long
    ) : this(settingsData) {
        this.id = id
    }

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
        constructor(
            dim: Int,
            bombsPercentage: Int
        ): this(
            Dimensions(dim),
            bombsPercentage
        )

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
            constructor(
                dim: Int
            ): this(dim, dim, dim)

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