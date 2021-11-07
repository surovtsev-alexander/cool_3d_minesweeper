package com.surovtsev.cool3dminesweeper.models.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.entities.Ranking
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings

@Database(
    entities = [
        Settings::class,
        Ranking::class,
    ],
    version = RankingDatabase.DatabaseInfo.version,
    exportSchema = false,
)
abstract class RankingDatabase: RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun rankingDao(): RankingDao

    object DatabaseInfo {
        const val version = 1
        const val name = "ranking-database"
    }
}