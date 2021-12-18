package com.surovtsev.settings.dagger

import com.surovtsev.core.dagger.components.RootComponent
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.settings.viewmodel.SettingsScreenInitialState
import com.surovtsev.settings.viewmodel.SettingsScreenStateHolder
import com.surovtsev.settings.viewmodel.SettingsScreenStateValue
import dagger.Component
import dagger.Module
import dagger.Provides

@SettingsScope
@Component(
    dependencies = [
        RootComponent::class,
    ],
    modules = [
        SettingsModule::class,
    ]
)
interface SettingsComponent {
    val settingsDao: SettingsDao
    val saveController: SaveController

    val settingsScreenStateHolder: SettingsScreenStateHolder
    val settingsScreenStateValue: SettingsScreenStateValue
}


@Module
object SettingsModule {
    @SettingsScope
    @Provides
    fun provideSettingsScreenStateHolder(
    ): SettingsScreenStateHolder {
        return SettingsScreenStateHolder(SettingsScreenInitialState)
    }

    @SettingsScope
    @Provides
    fun provideSettingsScreenStateValue(
        settingsScreenStateHolder: SettingsScreenStateHolder
    ): SettingsScreenStateValue {
        return settingsScreenStateHolder
    }
}
