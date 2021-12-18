package com.surovtsev.ranking.dagger

import com.surovtsev.core.dagger.components.RootComponent
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenInitialState
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateHolder
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenStateValue
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.Component
import dagger.Module
import dagger.Provides


@RankingScope
@Component(
    dependencies = [
        RootComponent::class,
    ],
    modules = [
        RankingModule::class,
    ]
)
interface RankingComponent {

    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper

    val rankingScreenStateHolder: RankingScreenStateHolder
    val rankingScreenStateValue: RankingScreenStateValue

    val saveController: SaveController

    val timeSpan: TimeSpan
}


@Module
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

    @RankingScope
    @Provides
    fun provideTimeSpan(
        timeSpanHelper: TimeSpanHelperImp,
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
        )
    }
}