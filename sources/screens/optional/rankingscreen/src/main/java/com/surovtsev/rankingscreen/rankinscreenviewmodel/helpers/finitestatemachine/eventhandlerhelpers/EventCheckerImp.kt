package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.eventhandlerhelpers

import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventChecker
import com.surovtsev.finitestatemachine.eventhandlerOld.eventchecker.EventCheckerResult
import com.surovtsev.finitestatemachine.state.State
import com.surovtsev.rankingscreen.dagger.RankingScreenScope
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.RankingScreenData
import javax.inject.Inject

@RankingScreenScope
class EventCheckerImp @Inject constructor(
): EventChecker<EventToRankingScreenViewModel, RankingScreenData>
{
    override fun check(
        event: EventToRankingScreenViewModel
        , state: State<RankingScreenData>
    ): EventCheckerResult<EventToRankingScreenViewModel> {
        return EventCheckerResult.Pass()
    }
}