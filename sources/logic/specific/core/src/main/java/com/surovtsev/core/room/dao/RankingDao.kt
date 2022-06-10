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