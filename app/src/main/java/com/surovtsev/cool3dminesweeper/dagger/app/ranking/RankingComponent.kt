package com.surovtsev.cool3dminesweeper.dagger.app.ranking

import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.dagger.app.ToastMessageData
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.*
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
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingScreenEvents: RankingScreenEvents
    val rankingTableSortTypeData: RankingTableSortTypeData
    val rankingListHelper: RankingListHelper
    val toastMessageData: ToastMessageData
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    @Named(RankingScreenEvents.RankingListName)
    fun provideRankingList(): RankingListData {
        return RankingListData(
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

    @Named(RankingScreenEvents.SelectedSettingsIdName)
    @RankingScope
    @Provides
    fun provideSelectedSettingsIdData(): SelectedSettingsIdData {
        return MyLiveData(-1)
    }

    @RankingScope
    @Provides
    fun provideWinsCount(): WinsCountMapData {
        return WinsCountMapData(
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
