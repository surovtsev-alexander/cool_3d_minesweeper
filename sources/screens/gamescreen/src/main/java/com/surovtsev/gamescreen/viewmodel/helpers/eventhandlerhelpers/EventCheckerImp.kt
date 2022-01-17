package com.surovtsev.gamescreen.viewmodel.helpers.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.gamescreen.viewmodel.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.GameScreenData

class EventCheckerImp(
): EventChecker<EventToGameScreenViewModel, GameScreenData> {

    override fun check(
        event: EventToGameScreenViewModel,
        state: State<GameScreenData>
    ): EventCheckerResult<EventToGameScreenViewModel> {
        return EventCheckerResult.Pass()
    }
}