package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.rankingscreen.dagger.DaggerRankingComponent
import com.surovtsev.rankingscreen.dagger.RankingComponent
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.utils.dagger.componentholder.DaggerComponentHolder

class DaggerComponentsHolder(
    private val appComponentEntryPoint: AppComponentEntryPoint,
) {

    val restartableCoroutineScopeComponentHolder = DaggerComponentHolder<RestartableCoroutineScopeComponent> {
        DaggerRestartableCoroutineScopeComponent
            .create()
    }

    val timeSpanComponentHolder = DaggerComponentHolder<TimeSpanComponent> {
        DaggerTimeSpanComponent
            .builder()
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.create(
                    restartableCoroutineScopeComponentHolder.getOrCreate(),
                    "RankingScreenViewModel:TimeSpanComponent"
                )
            )
            .build()
    }

    val rankingComponentHolder = DaggerComponentHolder<RankingComponent>  {
        DaggerRankingComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .timeSpanComponentEntryPoint(timeSpanComponentHolder.getOrCreate())
            .build()
    }
}