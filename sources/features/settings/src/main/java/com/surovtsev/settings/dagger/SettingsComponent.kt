package com.surovtsev.settings.dagger

import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.settings.viewmodel.SettingsScreenInitialState
import com.surovtsev.settings.viewmodel.SettingsScreenStateHolder
import com.surovtsev.settings.viewmodel.SettingsScreenStateValue
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@DefineComponent(
    parent = ViewModelComponent::class
)
@SettingsScope
interface SettingsComponent {


    @DefineComponent.Builder
    interface Builder {

        fun build(): SettingsComponent
    }
}

@InstallIn(SettingsComponent::class)
@EntryPoint
@SettingsScope
interface SettingsComponentEntryPoint {
    val settingsDao: SettingsDao
    val saveController: SaveController

    val settingsScreenStateHolder: SettingsScreenStateHolder
    val settingsScreenStateValue: SettingsScreenStateValue
}

@Module
@InstallIn(SettingsComponent::class)
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
