package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State

class TemplateScreenViewModelEventChecker<E: EventToViewModelAlt, D: ScreenData>(
    private val screenEventChecker: EventChecker<E, D>
):EventChecker<E, D> {
    override fun check(event: E, state: State<D>): EventCheckerResult<E> {
        return screenEventChecker.check(
            event,
            state,
        )
    }
}
