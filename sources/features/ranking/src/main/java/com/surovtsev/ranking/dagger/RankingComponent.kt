package com.surovtsev.ranking.dagger

import com.surovtsev.core.helpers.*
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenInitialState
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateHolder
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateValue
import com.surovtsev.utils.timers.TimeSpan
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


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
    val rankingListHelper: RankingListHelper

    val rankingScreenStateHolder: RankingScreenStateHolder
    val rankingScreenStateValue: RankingScreenStateValue

    val timeSpan: TimeSpan
}

@Module
@InstallIn(RankingComponent::class)
object RankingModule {

    @RankingScope
    @Provides
    fun provideRankingScreenStateHolder(
    ): RankingScreenStateHolder {
        return RankingScreenStateHolder(RankingScreenInitialState)
    }


    @RankingScope
    @Provides
    fun provideRankingScreenStateValue(
        rankingScreenStateHolder: RankingScreenStateHolder
    ): RankingScreenStateValue {
        return rankingScreenStateHolder
    }
}