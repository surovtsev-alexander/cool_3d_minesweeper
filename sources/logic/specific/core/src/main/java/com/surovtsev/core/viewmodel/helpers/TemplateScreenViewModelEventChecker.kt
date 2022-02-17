package com.surovtsev.core.viewmodel.helpers

import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.ScreenData
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.finitestatemachine.state.StateDescription

class TemplateScreenViewModelEventChecker<E: EventToViewModel, D: ScreenData>(
    private val closeErrorAndFinishEvent: E
): EventChecker<E, D> {
    override fun check(event: E, state: State<D>): EventCheckerResult<E> {

        val screenData = state.data

        if (state.description !is StateDescription.Error) {
            return EventCheckerResult.Pass()
        }

        if (event !is EventToViewModel.CloseError) {
            return EventCheckerResult.Skip()
        }

        if (event is EventToViewModel.CloseErrorAndFinish) {
            return EventCheckerResult.Pass()
        }

        return if (screenData is ScreenData.InitializationIsNotFinished) {
            EventCheckerResult.ChangeWith(closeErrorAndFinishEvent)
        } else {
            EventCheckerResult.Pass()
        }
    }
}