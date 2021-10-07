package com.surovtsev.cool3dminesweeper.dagger.app.ranking

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.RankingList
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.RankingScreenEvents
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.SelectedSettingsId
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.WinsCount
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@DefineComponent(
    parent = ViewModelComponent::class,
)
@RankingScope
interface RankingComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): RankingComponent
    }
}

@InstallIn(RankingComponent::class)
@EntryPoint
@RankingScope
interface RankingComponentEntryPoint {
    val settingsDBQueries: SettingsDBQueries
    val rankingDBQueries: RankingDBQueries
    val rankingScreenEvents: RankingScreenEvents
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.RankingListName)
    fun provideRankingList(): RankingList {
        return MyLiveData(
            emptyList()
        )
    }

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.FilteredRankingListName)
    fun provideFilteredRankingList(): RankingList {
        return MyLiveData(
            emptyList()
        )
    }

    @RankingScope
    @Provides
    fun provideSelectedSettingsId(): SelectedSettingsId {
        return MyLiveData(-1)
    }

    @RankingScope
    @Provides
    fun provideWinsCount(): WinsCount {
        return MyLiveData(
            emptyMap()
        )
    }
}
