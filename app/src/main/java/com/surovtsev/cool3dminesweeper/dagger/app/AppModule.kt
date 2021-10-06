package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.DBHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.models.game.database.DataWithId
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.utils.interfaces.minesweeper.database.IDBHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

typealias SettingsListWithIds = MyLiveData<List<DataWithId<SettingsData>>>

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDBHelper(
        @ApplicationContext context: Context
    ) = DBHelper(context)

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
    fun provideSettingsListWithIds(): SettingsListWithIds {
        return MyLiveData(
            listOf()
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface AppModuleBind {

    @Binds
    @Singleton
    fun bindIDBHelper(
        dbHelper: DBHelper
    ): IDBHelper
}
