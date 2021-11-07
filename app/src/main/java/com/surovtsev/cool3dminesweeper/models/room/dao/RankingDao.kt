package com.surovtsev.cool3dminesweeper.models.room.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.surovtsev.cool3dminesweeper.models.room.entities.Ranking
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingDataWithPlaces

typealias RankingList = List<Ranking>
typealias WinsCountMap = Map<Long, Long>
typealias RankingListWithPlaces = List<RankingDataWithPlaces>


@Dao
interface RankingDao {

    @Query("SELECT * FROM ranking")
    fun getAll(): RankingList

    @Insert
    fun insert(ranking: Ranking)

    data class WinsCount(
        val settingsId: Long,
        val count: Long
    )

    @Query(
        "SELECT ${Ranking.RankingData.ColumnNames.settingsId} as settingsId, COUNT(*) as count\n" +
                "FROM ranking\n" +
                "GROUP BY ${Ranking.RankingData.ColumnNames.settingsId}"
    )
    fun getWinsCount(): List<WinsCount>

    fun getWinsCountMap(): WinsCountMap {
        val winsCount = getWinsCount()
        return winsCount.map {
            it.settingsId to it.count
        }.toMap()
    }
//
//    @Query(
//        "SELECT\n" +
//                "${Ranking.RankingData.ColumnNames.settingsId},\n" +
//                "${Ranking.RankingData.ColumnNames.elapsed},\n" +
//                "${Ranking.RankingData.ColumnNames.dateTime},\n" +
//                "ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS place" +
//                "FROM ranking\n" +
//                "WHERE ${Ranking.RankingData.ColumnNames.settingsId} = :settingsId"
//    )
//    fun getRankingListForSettingsId(
//        settingsId: Long
//    ): RankingListWithPlaces
}