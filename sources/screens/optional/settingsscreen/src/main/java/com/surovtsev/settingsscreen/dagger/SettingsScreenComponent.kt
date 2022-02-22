package com.surovtsev.settingsscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenFiniteStateMachine
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenFiniteStateMachineFactory
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenStateHolder
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

    val settingsScreenFiniteStateMachine: SettingsScreenFiniteStateMachine

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(
            appComponentEntryPoint: AppComponentEntryPoint
        ): Builder

        fun stateHolder(
            @BindsInstance
            stateHolder: SettingsScreenStateHolder
        ): Builder

        fun settingsScreenFiniteStateMachineFactory(
            @BindsInstance
            settingsScreenFiniteStateMachineFactory: SettingsScreenFiniteStateMachineFactory
        ): Builder

        fun build(): SettingsScreenComponent
    }
}


@Module
object SettingsScreenModule {
    @SettingsScreenScope
    @Provides
    fun provideSettingsScreenFiniteStateMachine(
        settingsScreenFiniteStateMachineFactory: SettingsScreenFiniteStateMachineFactory,
        eventHandler: EventHandlerImp,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): SettingsScreenFiniteStateMachine {
        return settingsScreenFiniteStateMachineFactory(
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
