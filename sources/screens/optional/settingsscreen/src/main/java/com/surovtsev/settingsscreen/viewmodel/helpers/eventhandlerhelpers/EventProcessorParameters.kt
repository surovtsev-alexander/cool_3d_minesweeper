package com.surovtsev.settingsscreen.viewmodel.helpers.eventhandlerhelpers

import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.settingsscreen.dagger.SettingsScreenScope
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenStateHolder
import javax.inject.Inject

@SettingsScreenScope
class EventProcessorParameters @Inject constructor(
    val stateHolder: SettingsScreenStateHolder,
    val settingsDao: SettingsDao,
    val saveController: SaveController,
)
