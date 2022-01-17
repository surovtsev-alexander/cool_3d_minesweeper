package com.surovtsev.settingsscreen.viewmodel.helpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.settingsscreen.viewmodel.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenData

class EventCheckerImp(
): EventChecker<EventToSettingsScreenViewModel, SettingsScreenData> {
    override fun check(
        event: EventToSettingsScreenViewModel,
        state: State<SettingsScreenData>
    ): EventCheckerResult<EventToSettingsScreenViewModel> {

        return EventCheckerResult.Pass()

    }
}