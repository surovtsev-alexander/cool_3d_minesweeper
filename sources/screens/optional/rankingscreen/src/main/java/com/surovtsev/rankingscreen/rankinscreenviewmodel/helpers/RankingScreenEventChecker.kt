package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandler.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenDataAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt

class RankingScreenEventChecker<E: EventToRankingScreenViewModelAlt, D: RankingScreenDataAlt>()
    : EventChecker<E, D>
{
    override fun check(event: E, state: State<D>): EventCheckerResult<E> {
        return EventCheckerResult.Pass()
    }
}