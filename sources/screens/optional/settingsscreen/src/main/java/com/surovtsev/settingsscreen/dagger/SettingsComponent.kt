package com.surovtsev.settingsscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenInitialState
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenStateHolder
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenStateValue
import dagger.Component
import dagger.Module
import dagger.Provides

@SettingsScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
    ],
    modules = [
        SettingsModule::class,
    ]
)
interface SettingsComponent {
    val settingsDao: SettingsDao
    val saveController: SaveController
}


@Module
object SettingsModule {
}
