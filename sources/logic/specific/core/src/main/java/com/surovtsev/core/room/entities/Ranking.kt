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

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Settings::class,
            parentColumns = arrayOf(Settings.ColumnNames.id),
            childColumns = arrayOf(Ranking.RankingData.ColumnNames.settingsId),
            onDelete = ForeignKey.CASCADE
        )
    ],
    tableName = Ranking.TableName.name
)
data class Ranking (
    @Embedded val rankingData: RankingData,
) {
    @[PrimaryKey(autoGenerate = true) ColumnInfo(name = ColumnNames.id)] var id: Long = 0

    object TableName {
        const val name =  "ranking"
    }

    object ColumnNames {
        const val id = "id"
    }

    data class RankingData(
        @ColumnInfo(name = ColumnNames.settingsId) val settingsId: Long,
        @ColumnInfo(name = ColumnNames.elapsed) val elapsed: Long,
        @ColumnInfo(name = ColumnNames.dateTime) val dateTime: Long,
    ) {
        object ColumnNames {
            const val settingsId = "settings_id"
            const val elapsed = "elapsed"
            const val dateTime = "dateTime"
        }
    }
}
