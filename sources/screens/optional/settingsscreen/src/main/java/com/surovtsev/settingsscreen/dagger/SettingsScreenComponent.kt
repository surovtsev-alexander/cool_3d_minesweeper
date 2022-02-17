package com.surovtsev.settingsscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.finitestatemachine.eventhandlerOld.EventHandlerOld
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenEventHandler
import com.surovtsev.settingsscreen.viewmodel.helpers.typealiases.SettingsScreenStateHolder
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandlerhelpers.EventCheckerImp
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandlerhelpers.EventProcessorImp
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

    val eventHandlerOld: SettingsScreenEventHandler

    val eventHandler: EventHandlerImp

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(appComponentEntryPoint: AppComponentEntryPoint): Builder

        fun stateHolder(@BindsInstance stateHolder: SettingsScreenStateHolder): Builder

        fun build(): SettingsScreenComponent
    }
}


@Module
object SettingsScreenModule {

    @SettingsScreenScope
    @Provides
    fun provideEventHandler(
        eventCheckerImp: EventCheckerImp,
        eventProcessorImp: EventProcessorImp,
    ): SettingsScreenEventHandler {
        return EventHandlerOld(
            eventCheckerImp,
            eventProcessorImp,
        )
    }
}
