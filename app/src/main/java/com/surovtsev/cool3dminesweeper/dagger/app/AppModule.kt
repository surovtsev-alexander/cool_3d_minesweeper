package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import androidx.room.Room
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.databases.RankingDatabase
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.settings.SettingsListData
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.utils.coroutines.CustomScope
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanFlow
import com.surovtsev.utils.timers.TimeSpanFlowData
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRankingDatabase(
    @ApplicationContext context: Context,
    ): RankingDatabase {
        val res = Room
            .databaseBuilder(
                context,
                RankingDatabase::class.java,
                RankingDatabase.DatabaseInfo.name
            )
            .build()

        runBlocking {
            val customScope = CustomScope(Dispatchers.IO)
            customScope.launch {
                do {
                    val settingsDao = res.settingsDao()
                    val needToPrepopulate = settingsDao.getCount() == 0

                    if (!needToPrepopulate) {
                        break
                    }

                    val dataToPrepopulate = arrayOf(
                        Settings.SettingsData(
                            12, 20
                        ),
                        Settings.SettingsData(
                            10, 20
                        ),
                        Settings.SettingsData(
                            8, 16
                        ),
                        Settings.SettingsData(
                            5, 12
                        ),
                        Settings.SettingsData(
                            12, 30
                        ),
                        Settings.SettingsData(
                            12, 25
                        ),
                        Settings.SettingsData(
                            10, 18
                        ),
                    )

                    dataToPrepopulate.forEach {
                        settingsDao.insert(Settings(it))
                    }
                } while (false)
            }.join()
        }
        return res
    }

    @Singleton
    @Provides
    fun provideSettingsDao(
        rankingDatabase: RankingDatabase
    ): SettingsDao = rankingDatabase.settingsDao()

    @Singleton
    @Provides
    fun provideRankingDao(
        rankingDatabase: RankingDatabase
    ): RankingDao = rankingDatabase.rankingDao()

    @Singleton
    @Provides
    fun provideSaveController(
        @ApplicationContext context: Context
    ) = SaveController(context)

    @Singleton
    @Provides
    fun provideSettingsListData(): SettingsListData {
        return SettingsListData(
            emptyList()
        )
    }

    @Singleton
    @Provides
    fun provideRankingListHelper(
        rankingDao: RankingDao
    ): RankingListHelper {
        return RankingListHelper(
            rankingDao
        )
    }

    @Provides
    fun provideViewModelCoroutineScopeHelper(): ViewModelCoroutineScopeHelper =
        ViewModelCoroutineScopeHelperImpl()

    @Singleton
    @Provides
    fun provideTimeSpanHelperImp(
    ): TimeSpanHelperImp {
        return TimeSpanHelperImp()
    }

    @Singleton
    @Provides
    fun provideTimeSpan(
        timeSpanHelper: TimeSpanHelperImp,
        timeSpanFlowData: TimeSpanFlowData,
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
            timeSpanFlowData,
        )
    }

    @Singleton
    @Provides
    fun provideTimeSpanFlowData(
    ): TimeSpanFlowData {
        return MutableStateFlow(0)
    }

    @Singleton
    @Provides
    fun provideTimeSpanFlow(
        timeSpanFlowData: TimeSpanFlowData
    ): TimeSpanFlow {
        return timeSpanFlowData.asStateFlow()
    }
}
