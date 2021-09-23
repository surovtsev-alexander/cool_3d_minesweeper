package com.surovtsev.cool_3d_minesweeper.dagger.app.ranking

import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_model_view.*
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.activities.RankingActivity
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Scope

@RankingScope
@Subcomponent(
    modules = [
        RankingModule::class
    ]
)
interface RankingComponent {

    fun inject(rankingActivity: RankingActivity)
}

@Module
object RankingModule {

    @RankingScope
    @Provides
    fun provideSettingsListWithIds(): SettingsListWithIds {
        return MyLiveData(
            listOf()
        );
    }

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

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RankingScope