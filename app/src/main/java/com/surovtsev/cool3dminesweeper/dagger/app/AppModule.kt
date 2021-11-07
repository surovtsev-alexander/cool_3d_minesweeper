package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import androidx.room.Room
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.databases.RankingDatabase
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.cool3dminesweeper.utils.constants.Constants
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingListHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        return Room.databaseBuilder(
            context,
            RankingDatabase::class.java,
            RankingDatabase.DatabaseInfo.name
        ).build()
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
