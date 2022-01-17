package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData

class EventCheckerImp(
): EventChecker<EventToGameScreenViewModel, GameScreenData> {

    override fun check(
        event: EventToGameScreenViewModel,
        state: State<GameScreenData>
    ): EventCheckerResult<EventToGameScreenViewModel> {
        return EventCheckerResult.Pass()
    }
}
