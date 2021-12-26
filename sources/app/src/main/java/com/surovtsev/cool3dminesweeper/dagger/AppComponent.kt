package com.surovtsev.cool3dminesweeper.dagger

import android.content.Context
import androidx.room.Room
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.databases.RankingDatabase
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.gamescreen.viewmodel.GameScreenViewModel
import com.surovtsev.mainscreeen.viewmodel.MainScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenViewModel
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides


@AppScope
@Component(
    modules = [
        AppModule::class,
    ]
)
interface AppComponent: AppComponentEntryPoint {
    val mainScreenViewModelFactory: MainScreenViewModel.Factory
    val settingsScreenViewModelFactory: SettingsScreenViewModel.Factory
    val rankingScreenViewModelFactory: RankingScreenViewModel.Factory
    val gameScreenViewModelFactory: GameScreenViewModel.Factory

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun build(): AppComponent
    }

}

@Module
object AppModule {

    @AppScope
    @Provides
    fun provideRankingDatabase(
    context: Context,
    ): RankingDatabase {
        return Room
            .databaseBuilder(
                context,
                RankingDatabase::class.java,
                RankingDatabase.DatabaseInfo.name
            )
            .build()
    }

    @AppScope
    @Provides
    fun provideSettingsDao(
        rankingDatabase: RankingDatabase
    ): SettingsDao = rankingDatabase.settingsDao()

    @AppScope
    @Provides
    fun provideRankingDao(
        rankingDatabase: RankingDatabase
    ): RankingDao = rankingDatabase.rankingDao()

    @AppScope
    @Provides
    fun provideSaveController(
        context: Context
    ) = SaveController(context)

    @AppScope
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
}
