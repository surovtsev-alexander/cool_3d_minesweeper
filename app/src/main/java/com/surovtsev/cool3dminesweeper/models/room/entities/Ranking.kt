package com.surovtsev.cool3dminesweeper.models.room.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Settings::class,
            parentColumns = arrayOf(Settings.ColumnNames.id),
            childColumns = arrayOf(Ranking.RankingData.ColumnNames.settingsId),
            onDelete = CASCADE
        )
    ]
)
data class Ranking (
    @Embedded val rankingData: RankingData,
) {
    @[PrimaryKey(autoGenerate = true) ColumnInfo(name = ColumnNames.id)] var id: Long = 0

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
