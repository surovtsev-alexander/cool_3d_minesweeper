package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.settingsscreen.dagger.SettingsScreenScope
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.EventToSettingsScreenViewModel
import com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine.SettingsScreenData
import javax.inject.Inject


@SettingsScreenScope
class EventCheckerImp @Inject constructor(
): EventChecker<EventToSettingsScreenViewModel, SettingsScreenData> {
    override fun check(
        event: EventToSettingsScreenViewModel,
        state: State<SettingsScreenData>
    ): EventCheckerResult<EventToSettingsScreenViewModel> {

        return EventCheckerResult.Pass()

    }
}
