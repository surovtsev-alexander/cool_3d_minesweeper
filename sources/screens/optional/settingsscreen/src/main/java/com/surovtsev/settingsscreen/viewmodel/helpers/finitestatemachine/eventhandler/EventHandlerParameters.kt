package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.settingsscreen.dagger.SettingsScreenScope
import javax.inject.Inject

@SettingsScreenScope
class EventHandlerParameters @Inject constructor(
    val stateHolder: StateHolder,
    val settingsDao: SettingsDao,
    val saveController: SaveController,
)
