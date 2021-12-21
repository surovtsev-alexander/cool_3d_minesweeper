package com.surovtsev.cool3dminesweeper.dagger.app

import android.content.Context
import androidx.room.Room
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.room.databases.RankingDatabase
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.settings.SettingsListData
import com.surovtsev.game.viewmodel.GameScreenViewModel
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenViewModel
import com.surovtsev.settings.viewmodel.SettingsScreenViewModel
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
        val res = Room
            .databaseBuilder(
                context,
                RankingDatabase::class.java,
                RankingDatabase.DatabaseInfo.name
            )
            .build()

        runBlocking {
            val customScope = CustomCoroutineScope(Dispatchers.IO)
            customScope.launch {
                do {
                    /* TODO: move from dagger. */
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
            customScope.turnOff()
        }
        return res
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
    fun provideSettingsListData(): SettingsListData {
        return SettingsListData(
            emptyList()
        )
    }

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
