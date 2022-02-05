package com.surovtsev.core.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.surovtsev.core.room.entities.Ranking

typealias RankingList = List<Ranking>
typealias WinsCountMap = Map<Long, Long>


@Dao
interface RankingDao {

    companion object {
        const val rankingTableName = Ranking.TableName.name
    }

    @Query("SELECT * FROM $rankingTableName")
    fun getAll(): RankingList

    @Insert
    fun insert(ranking: Ranking)

    data class WinsCount(
        val settingsId: Long,
        val count: Long
    )

    @Query(
        "SELECT ${Ranking.RankingData.ColumnNames.settingsId} as settingsId, COUNT(*) as count\n" +
                "FROM $rankingTableName\n" +
                "GROUP BY ${Ranking.RankingData.ColumnNames.settingsId}"
    )
    fun getWinsCount(): List<WinsCount>

    fun getWinsCountMap(): WinsCountMap {
        val winsCount = getWinsCount()
        return winsCount.map {
            it.settingsId to it.count
        }.toMap()
    }

    @Query(
        "SELECT *\n" +
                "FROM $rankingTableName\n" +
                "WHERE ${Ranking.RankingData.ColumnNames.settingsId} = :settingsId"
    )
    fun getRankingListForSettingsId(
        settingsId: Long
    ): List<Ranking>
}