package com.surovtsev.settingsscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.viewmodel.FiniteStateMachineFactory
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides


@SettingsScreenScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
    ],
    modules = [
        SettingsScreenModule::class,
    ]
)
interface SettingsScreenComponent {
    val settingsDao: SettingsDao
    val saveController: SaveController

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
            finiteStateMachineFactory: FiniteStateMachineFactory
        ): Builder

        fun build(): SettingsScreenComponent
    }
}


@Module
object SettingsScreenModule {
    @SettingsScreenScope
    @Provides
    fun provideSettingsScreenFiniteStateMachine(
        finiteStateMachineFactory: FiniteStateMachineFactory,
        eventHandler: EventHandlerImp,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): FiniteStateMachine {
        return finiteStateMachineFactory(
            eventHandler,
            SubscriptionsHolderComponentFactoryHolderImp
                .createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "SettingsScreen:FiniteStateMachine"
                )
                .subscriptionsHolder
        )
    }

    @SettingsScreenScope
    @Provides
    fun provideRestartableCoroutineScopeComponent(
    ): RestartableCoroutineScopeComponent {
        return DaggerRestartableCoroutineScopeComponent
            .create()
    }
}
