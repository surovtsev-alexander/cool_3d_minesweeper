package com.surovtsev.cool3dminesweeper.models.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.surovtsev.cool3dminesweeper.models.room.entities.Ranking

typealias RankingList = List<Ranking>

@Dao
interface RankingDao {

    @Query("SELECT * FROM ranking")
    fun getAll(): RankingList

    @Insert
    fun insert(ranking: Ranking)
}