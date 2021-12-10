package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import androidx.room.Room
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.databases.RankingDatabase
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.utils.constants.Constants
import com.surovtsev.utils.coroutines.CustomScope
import com.surovtsev.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingListHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

typealias SettingsList = List<Settings>
typealias SettingsListData = MyLiveData<SettingsList>
typealias ToastMessageData = MyLiveData<String>

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

    @Singleton
    @Provides
    fun provideToastMessageData(): ToastMessageData = ToastMessageData(Constants.emptyString)
}
