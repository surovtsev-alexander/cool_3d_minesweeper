package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelperImp
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.models.game.database.DataWithId
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.RankingListHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

typealias SettingsDataWithIdsList = List<DataWithId<SettingsData>>
typealias SettingsDataWithIdsListData = MyLiveData<SettingsDataWithIdsList>

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDBHelperImp(
        @ApplicationContext context: Context
    ) = DBHelperImp(context)

    @Singleton
    @Provides
    fun provideRankingDBQueries(
        dbHelper: DBHelper
    ) = RankingDBQueries(dbHelper)

    @Singleton
    @Provides
    fun provideSettingsDBQueries(
        dbHelper: DBHelper
    ) = SettingsDBQueries(dbHelper)

    @Singleton
    @Provides
    fun provideSaveController(
        @ApplicationContext context: Context
    ) = SaveController(context)

    @Singleton
    @Provides
    fun provideSettingsListWithIds(): SettingsDataWithIdsListData {
        return MyLiveData(
            emptyList()
        )
    }

    @Singleton
    @Provides
    fun provideRankingListHelper(
        rankingDBQueries: RankingDBQueries
    ): RankingListHelper {
        return RankingListHelper(
            rankingDBQueries
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AppModuleBind {

    @Binds
    @Singleton
    fun bindDBHelper(
        dbHelper: DBHelperImp
    ): DBHelper
}
