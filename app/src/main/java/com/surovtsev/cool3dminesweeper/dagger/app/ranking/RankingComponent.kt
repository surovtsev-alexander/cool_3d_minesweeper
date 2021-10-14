package com.surovtsev.cool3dminesweeper.dagger.app.ranking

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.*
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
    val rankingTableSortTypeData: RankingTableSortTypeData
    val rankingListHelper: RankingListHelper
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.RankingListName)
    fun provideRankingList(): RankingDataListData {
        return MyLiveData(
            emptyList()
        )
    }

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.FilteredRankingListName)
    fun provideFilteredRankingList(): RankingListWithPlacesData {
        return MyLiveData(
            emptyList()
        )
    }

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.RankingListToDisplay)
    fun provideRankingListToDisplay(): RankingListWithPlacesData {
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

    @RankingScope
    @Provides
    fun provideSortTypeData(): RankingTableSortTypeData =
        MyLiveData(
            RankingTableSortType(
                RankingColumn.SortableColumn.DateColumn,
                SortDirection.Descending
            )
        )
}
