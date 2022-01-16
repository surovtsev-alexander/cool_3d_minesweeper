package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.rankingscreen.dagger.RankingComponent
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent

class RankingScreenDaggerComponentsHolder(
    val appComponentEntryPoint: AppComponentEntryPoint,
) {
    var timeSpanComponent: TimeSpanComponent? = null
    var rankingComponent: RankingComponent? = null
    var restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent? = null

    fun restartOrCreateRestartableCoroutineScopeComponent(): RestartableCoroutineScopeComponent {
        return restartableCoroutineScopeComponent.let {
            it?.also {
                it.subscriberImp.restart()
            } ?: DaggerRestartableCoroutineScopeComponent.create()
                .also {
                    restartableCoroutineScopeComponent = it
                }
        }
    }
}
