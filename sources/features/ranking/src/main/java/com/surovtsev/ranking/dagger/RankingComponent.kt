package com.surovtsev.ranking.dagger

import com.surovtsev.core.dataconstructions.MyLiveData
import com.surovtsev.core.ranking.*
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateHolder
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateValue
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.RankingListWithPlacesData
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.RankingScreenEvents
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.SelectedSettingsIdData
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.WinsCountMapData
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

typealias ToastMessageData = MyLiveData<String>

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

    val rankingScreenStateHolder: RankingScreenStateHolder
    val rankingScreenStateValue: RankingScreenStateValue
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    fun provideRankingScreenStateHolder(
    ): RankingScreenStateHolder {
        return RankingScreenStateHolder()
    }

    @RankingScope
    @Provides
    fun provideRankingScreenStateValue(
        rankingScreenStateHolder: RankingScreenStateHolder
    ): RankingScreenStateValue {
        return rankingScreenStateHolder
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