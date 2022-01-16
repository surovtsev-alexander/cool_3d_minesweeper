package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers

import com.surovtsev.finitestatemachine.event.Event
import com.surovtsev.finitestatemachine.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.helpers.concrete.State
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenDataAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt

class RankingScreenEventChecker<E: EventToRankingScreenViewModelAlt, D: RankingScreenDataAlt>()
    : EventChecker<E, D>
{
    override fun check(event: Event, state: State<D>): EventCheckerResult {
        return EventCheckerResult.Process
    }
}