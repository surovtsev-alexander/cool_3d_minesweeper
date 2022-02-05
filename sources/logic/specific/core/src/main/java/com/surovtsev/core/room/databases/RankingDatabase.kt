package com.surovtsev.core.room.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.entities.Ranking
import com.surovtsev.core.room.entities.Settings

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