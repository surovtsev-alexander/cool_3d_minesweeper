package com.surovtsev.settingsscreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import dagger.Component
import dagger.Module

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
