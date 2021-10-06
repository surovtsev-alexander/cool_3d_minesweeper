package com.surovtsev.cool_3d_minesweeper.dagger.app.ranking

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.RankingScope
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.RankingActivityEvents
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.RankingList
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.SelectedSettingsId
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.WinsCount
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
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
    val rankingActivityEvents: RankingActivityEvents
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    @Named(RankingActivityEvents.RankingListName)
    fun provideRankingList(): RankingList {
        return MyLiveData(
            listOf()
        )
    }

    @RankingScope
    @Provides
    @Named(RankingActivityEvents.FilteredRankingListName)
    fun provideFilteredRankingList(): RankingList {
        return MyLiveData(
            listOf()
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
            mapOf()
        )
    }
}
