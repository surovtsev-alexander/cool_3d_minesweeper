package com.surovtsev.gamescreen.viewmodel.helpers

import com.surovtsev.gamescreen.dagger.DaggerGameScreenComponent
import com.surovtsev.gamescreen.dagger.GameScreenComponent
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.DaggerTouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.utils.dagger.componentholder.DaggerComponentHolder

class DaggerComponentsHolder {
    val gameScreenComponentHolder = DaggerComponentHolder<GameScreenComponent> {
        DaggerGameScreenComponent
            .create()
    }

    val restartableCoroutineScopeComponentHolder = DaggerComponentHolder<RestartableCoroutineScopeComponent> {
        DaggerRestartableCoroutineScopeComponent
            .create()
    }

    val timeSpanComponentHolder = DaggerComponentHolder<TimeSpanComponent> {
        DaggerTimeSpanComponent
            .builder()
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponentHolder.getOrCreate(),
                    "GameScreenViewModel:TimeSpanComponent"
                )
            )
            .build()
    }

    val touchListenerComponentHolder= DaggerComponentHolder<TouchListenerComponent> {
        DaggerTouchListenerComponent
            .builder()
            .timeSpanComponentEntryPoint(
                timeSpanComponentHolder.getOrCreate()
            )
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponentHolder.getOrCreate(),
                    "GameScreenViewModel:TouchListener"
                )
            )
            .build()
    }
}