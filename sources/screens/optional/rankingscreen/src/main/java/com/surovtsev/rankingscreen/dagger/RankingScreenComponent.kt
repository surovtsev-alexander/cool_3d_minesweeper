/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.rankingscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.templateviewmodel.helpers.typealiases.FiniteStateMachineFactory
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides


@RankingScreenScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
    ],
    modules = [
        RankingScreenModule::class,
    ]
)
interface RankingScreenComponent {
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper
    val saveController: SaveController

    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent
    val timeSpanComponent: TimeSpanComponent

    val finiteStateMachine: FiniteStateMachine

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(
            appComponentEntryPoint: AppComponentEntryPoint
        ): Builder

        fun stateHolder(
            @BindsInstance
            stateHolder: StateHolder
        ): Builder

        fun finiteStateMachineFactory(
            @BindsInstance
            finiteStateMachineFactory: FiniteStateMachineFactory,
        ): Builder

        fun build(): RankingScreenComponent
    }
}

@Module
object RankingScreenModule {
    @RankingScreenScope
    @Provides
    fun provideRankingScreenFiniteStateMachine(
        finiteStateMachineFactory: FiniteStateMachineFactory,
        eventHandler: EventHandlerImp,
    ): FiniteStateMachine {
        return finiteStateMachineFactory(
            eventHandler,
        )
    }

    @RankingScreenScope
    @Provides
    fun provideRestartableCoroutineScopeComponent(
    ): RestartableCoroutineScopeComponent {
        return DaggerRestartableCoroutineScopeComponent
            .create()
    }

    @RankingScreenScope
    @Provides
    fun provideTimeSpanComponent(
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): TimeSpanComponent {
        return DaggerTimeSpanComponent
            .builder()
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "RankingScreen:TimeSpanComponent"
                )
            )
            .build()
    }

    @RankingScreenScope
    @Provides
    fun provideFSMStateFlow(
        stateHolder: StateHolder
    ) = stateHolder.fsmStateFlow
}
