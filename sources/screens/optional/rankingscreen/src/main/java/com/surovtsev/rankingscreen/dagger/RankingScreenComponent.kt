package com.surovtsev.rankingscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.finitestatemachine.eventhandler.EventHandler
import com.surovtsev.finitestatemachine.eventhandlerOld.EventHandlerOld
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.RankingScreenData
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.typealiases.RankingScreenStateHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandlerhelpers.EventCheckerImp
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandlerhelpers.EventProcessorImp
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
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

    val eventHandlerOld: EventHandlerOld<EventToRankingScreenViewModel, RankingScreenData>

    val eventHandler: EventHandlerImp

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(appComponentEntryPoint: AppComponentEntryPoint): Builder

        fun stateHolder(@BindsInstance stateHolder: RankingScreenStateHolder): Builder

        fun build(): RankingScreenComponent
    }
}

@Module
object RankingScreenModule {

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
    fun provideEventHandler(
        eventCheckerImp: EventCheckerImp,
        eventProcessorImp: EventProcessorImp,
    ): EventHandlerOld<EventToRankingScreenViewModel, RankingScreenData> {
        return EventHandlerOld(
            eventCheckerImp,
            eventProcessorImp
        )
    }
}
